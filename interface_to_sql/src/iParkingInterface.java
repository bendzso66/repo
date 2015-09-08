import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONObject;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;

public class iParkingInterface {

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://localhost/smart_parking";
    // private static final String CONNECTION =
    // "jdbc:mysql://impala.aut.bme.hu/vehicle_data";
    private static final int R = 6371; // corrected earth radius, km
    private static String userName;
    private static String password;

    private static final String GEOCODING_COORDS_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String GEOCODING_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String COMMA_IN_URL = "%2C";
    private static final String KEY_STRING = "&key=";
    private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

    public static void main(String[] args) throws ClassNotFoundException,
            SQLException {

        // try {
        // System.out.println("Enter MySQL login name:");
        // BufferedReader in = new BufferedReader(new InputStreamReader(
        // System.in));
        // userName = in.readLine();
        // System.out.println("Enter password:");
        // in = new BufferedReader(new InputStreamReader(System.in));
        // password = in.readLine();
        // } catch (IOException e) {
        // System.out.println("IOException has been caught: " + e);
        // }

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        // p.put("user", userName);
        // p.put("password", password);
        p.put("user", "smartparking");
        p.put("password", "spict2015");
        // setPort(5678); //<- Uncomment this if you wan't spark to listen on a
        // port different than 4567.

        get(new Route("/findFreeLot") {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;
                ResultSet rs = null;

                double radius = 0;
                Set<String> queryParams = request.queryParams();

                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();

                    if (queryParams.contains("id")) {
                        int userId = Integer
                                .parseInt(request.queryParams("id"));
                        try {
                            radius = CommonJdbcMethods.manageUserParameters(
                                    userId, stmt, "lot_requests");
                        } catch (InvalidIdException e) {
                            return e.getMessage();
                        } catch (DuplicatedUserException e) {
                            return e.getMessage();
                        } catch (InvalidLoginTimeException e) {
                            return e.getMessage();
                        } catch (NotLoggedInException e) {
                            return e.getMessage();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "LOGIN_ERROR";
                        }
                    }

                    if (queryParams.contains("rad")) {
                        radius = Double.parseDouble(request.queryParams("rad"));
                    }
                    if (radius == 0) {
                        radius = 0.5;
                    }

                    // TODO try to find parking lots in SQL
                    String sqlQueryFromParkingLotsTable = "SELECT * FROM vehicle_data.smartparking_parking_lots;";
                    String sqlQueryFromParkingLotsTableErrorMsg = "SQL error: query from smartparking_parking_lots was unsuccessful.";
                    rs = CommonJdbcMethods.executeQueryStatement(stmt,
                            sqlQueryFromParkingLotsTable,
                            sqlQueryFromParkingLotsTableErrorMsg);

                    if (rs == null) {
                        return "RESULT_SET_IS_NULL";
                    }

                    List<rowInParkingLots> lst = getrowsInParkingLots(rs,
                            Double.parseDouble(request.queryParams("lat")),
                            Double.parseDouble(request.queryParams("lon")),
                            radius);

                    Gson gson = new Gson();
                    return gson.toJson(lst);

                } catch (SQLException e) {
                    e.printStackTrace();
                    return "SQL_CONNECTION_ERROR";
                } catch (ForwardedSqlException e) {
                    return "SQL_QUERY_ERROR";
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt, rs);
                }
            }

        });

        get(new Route("/sendFreeLot") {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;

                Set<String> queryParams = request.queryParams();

                int userId;
                if (queryParams.contains("id")) {
                    userId = Integer.parseInt(request.queryParams("id"));
                } else {
                    return "MISSING_USER_ID";
                }

                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();

                    try {
                        CommonJdbcMethods.manageUserParameters(userId, stmt,
                                "recommended_lots");
                    } catch (InvalidIdException e) {
                        return e.getMessage();
                    } catch (DuplicatedUserException e) {
                        return e.getMessage();
                    } catch (InvalidLoginTimeException e) {
                        return e.getMessage();
                    } catch (NotLoggedInException e) {
                        return e.getMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "LOGIN_ERROR";
                    }

                    String lat = request.queryParams("lat");
                    String lon = request.queryParams("lon");

                    String address;
                    try {
                        address = geocoding(lat, lon);
                        address = address.replace("'", "");
                    } catch (IOException e) {
                        address = "no address";
                    }

                    String sqlQueryInParkingLotsTable = "INSERT INTO vehicle_data.smartparking_parking_lots (time_of_submission, latitude, longitude, user_id, parking_lot_availability, address) VALUES ("
                            + "'"
                            + System.currentTimeMillis()
                            + "','"
                            + lat
                            + "','"
                            + lon
                            + "','"
                            + userId
                            + "','"
                            + request.queryParams("avail")
                            + "','"
                            + address
                            + "');";
                    String sqlQueryInParkingLotsTableError = "SQL error: update in smartparking_parking_lots was unsuccessful.";
                    CommonJdbcMethods.executeUpdateStatement(stmt,
                            sqlQueryInParkingLotsTable,
                            sqlQueryInParkingLotsTableError);

                    return "SUCCESSFULL_REQUEST";
                } catch (SQLException e) {
                    return "SQL_SERVER_ERROR";
                } catch (ForwardedSqlException e) {
                    return "SQL_QUERY_ERROR";
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt);
                }
            }

        });

        get(new Route("/registration") {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;
                ResultSet rs = null;

                // TODO check email
                String mail = request.queryParams("mail");
                // TODO check password condition
                String pass = request.queryParams("pass");
                double radius = Double.parseDouble(request.queryParams("rad"));
                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();

                    long currentTime = System.currentTimeMillis();
                    String sqlUpdateQueryInUsersTable = "INSERT INTO vehicle_data.smartparking_users (email, password, search_range, last_login, time_of_submission) "
                            + "VALUES ('"
                            + mail
                            + "','"
                            + pass
                            + "','"
                            + radius
                            + "','"
                            + currentTime
                            + "','"
                            + +currentTime
                            + "');";
                    String sqlUpdateQueryInUsersTableError = "SQL error: update in smartparking_users was unsuccessful.";
                    CommonJdbcMethods.executeUpdateStatement(stmt,
                            sqlUpdateQueryInUsersTable,
                            sqlUpdateQueryInUsersTableError);

                    String sqlQueryInUsersTable = "SELECT id FROM vehicle_data.smartparking_users WHERE email = '"
                            + mail
                            + "';";
                    String sqlQueryInUsersTableError = "SQL error: query in smartparking_users was unsuccessful.";
                    rs = CommonJdbcMethods.executeQueryStatement(stmt,
                            sqlQueryInUsersTable, sqlQueryInUsersTableError);

                    rs.next();
                    int userId = rs.getInt("id");

                    return userId;
                } catch (SQLException e) {
                    return "SQL_SERVER_ERROR";
                } catch (ForwardedSqlException e) {
                    String pattern = "com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '*@*.*' for key 'email'";
                    if (e.getCause().toString().matches(pattern)) {
                        return "EMAIL_IS_ALREADY_REGISTERED";
                    } else {
                        return "SQL_QUERY_ERROR";
                    }
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt, rs);
                }
            }

        });

        get(new Route("/login") {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;
                ResultSet rs = null;

                String mail = request.queryParams("mail");
                String pass = request.queryParams("pass");

                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();
                    String sqlQueryInUsersTable = "SELECT id, password FROM vehicle_data.smartparking_users WHERE email='"
                            + mail
                            + "';";
                    String sqlQueryInUsersTableError = "SQL error: query in smartparking_users was unsuccessful.";
                    rs = CommonJdbcMethods.executeQueryStatement(
                            stmt, sqlQueryInUsersTable,
                            sqlQueryInUsersTableError);

                    rs.last();
                    int size = rs.getRow();

                    if (size == 0) {
                        return "UNREGISTERED_EMAIL";
                    } else if (size == 1) {
                        if (pass.equals(rs.getString("password"))) {
                            int userId = rs.getInt("id");
                            String sqlUpdateQueryInUsersTable = "UPDATE vehicle_data.smartparking_users SET last_login='"
                                    + System.currentTimeMillis()
                                    + "' WHERE email='"
                                    + mail
                                    + "'AND password='"
                                    + pass
                                    + "';";
                            String sqlUpdateQueryInUsersTableError = "SQL error: update in smartparking_users was unsuccessful.";
                            CommonJdbcMethods.executeUpdateStatement(stmt,
                                    sqlUpdateQueryInUsersTable,
                                    sqlUpdateQueryInUsersTableError);

                            return userId;
                        } else {
                            return "WRONG_PASSWORD";
                        }
                    } else {
                        // This should never happen!
                        return "DUPLICATED_USER";
                    }
                } catch (SQLException e) {
                    return "SQL_SERVER_ERROR";
                } catch (ForwardedSqlException e) {
                    return "SQL_QUERY_ERROR";
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt, rs);
                }
            }

        });

        get(new Route("/getAddress") {

            @Override
            public Object handle(Request request, Response response) {
                String address = request.queryParams("address");
                String coords;
                try {
                    coords = geocoding(address);
                } catch (IOException e) {
                    coords = "NO_COORDS";
                }
                return coords;
            }

        });

    }

    private static List<rowInParkingLots> getrowsInParkingLots(ResultSet rs,
            double lat1, double lon1, double radius)
            throws ForwardedSqlException {

        ArrayList<rowInParkingLots> lst = new ArrayList<rowInParkingLots>();
        rowInParkingLots row = null;
        Double distance = null;
        try {
            while (rs.next()) {
                double dLat = Math.toRadians(Double.parseDouble(rs
                        .getString("latitude")) - lat1);
                double dLon = Math.toRadians(Double.parseDouble(rs
                        .getString("longitude")) - lon1);
                double a = Math.sin(dLat / 2)
                        * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(Double.parseDouble(rs
                                .getString("latitude"))))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                distance = R * c;
                if (distance <= radius
                        & rs.getString("parking_lot_availability").equals(
                                "free")) {
                    row = new rowInParkingLots();
                    row.setId(rs.getInt("ID"));
                    row.setGpsTime(rs.getLong("time_of_submission"));
                    row.setLatitude(rs.getDouble("latitude"));
                    row.setLongitude(rs.getDouble("longitude"));
                    row.setUserId(rs.getLong("id"));
                    row.setParkingLotAvailability(rs
                            .getString("parking_lot_availability"));
                    row.setAddress(rs.getString("address"));
                    row.setDistance(distance);

                    lst.add(row);
                }
            }
        } catch (SQLException e) {
            System.out
                    .println("SQL error: cannot create the list of parking lots.");
            e.printStackTrace();
            throw new ForwardedSqlException();
        }
        Collections.sort(lst);
        return lst;
    }

    public static String geocoding(String lat, String lon) throws IOException {
        URL url = new URL(GEOCODING_COORDS_URL
                + lat
                + COMMA_IN_URL
                + lon
                + KEY_STRING
                + API_KEY);
        Scanner scan = new Scanner(url.openStream());
        String str = new String();

        while (scan.hasNext()) {
            str += scan.nextLine();
        }
        scan.close();

        JSONObject obj = new JSONObject(str);
        if (!obj.getString("status").equals("OK")) {
            return "no address";
        } else {
            JSONObject res = obj.getJSONArray("results").getJSONObject(0);
            String formattedAddress = res.getString("formatted_address");
            String encodedAddress = new String(formattedAddress.getBytes(),
                    "UTF-8");
            return encodedAddress;
        }
    }

    public static String geocoding(String address) throws IOException {
        URL url = new URL(GEOCODING_ADDRESS_URL
                + address
                + KEY_STRING
                + API_KEY);
        Scanner scan = new Scanner(url.openStream());
        String str = new String();

        while (scan.hasNext()) {
            str += scan.nextLine();
        }
        scan.close();

        JSONObject obj = new JSONObject(str);
        if (!obj.getString("status").equals("OK")) {
            return "NO_COORDS";
        } else {
            JSONObject res = obj.getJSONArray("results").getJSONObject(0);
            JSONObject geom = new JSONObject(res.get("geometry").toString());
            JSONObject loc = new JSONObject(geom.get("location").toString());

            String lat = loc.get("lat").toString();
            String lon = loc.get("lng").toString();

            return "lat: " + lat + ", lon: " + lon;
        }
    }

}
