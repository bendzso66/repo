package hu.bme.hit.smartparking.servlet;

import static spark.Spark.get;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;

import hu.bme.hit.smartparking.jdbc.*;
import hu.bme.hit.smartparking.map.*;

public class SmartParkingServlet {

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String HOST = "localhost";
    private static final String DATABASE = "vehicle_data";
    private static final String CONNECTION = "jdbc:mysql://"
            + HOST
            + "/"
            + DATABASE
            + "?useUnicode=yes&characterEncoding=UTF-8";
    private static String userName = "smartparking";
    private static String password = "spict2015";
    private static final Properties p = new Properties();

    private static final String WAY_ID = "way_id";
    private static final String NAME_OF_WAY = "name_of_way";
    private static final String LATITUDE_1 = "latitude_1";
    private static final String LONGITUDE_1 = "longitude_1";
    private static final String LATITUDE_2 = "latitude_2";
    private static final String LONGITUDE_2 = "longitude_2";
    private static final String ALL_SPACES = "all_spaces";
    private static final String FREE_SPACES = "free_spaces";
    private static final String DISTANCE = "distance";

    private static final String CALL = "CALL ";
    private static final String GET_WAYS_PROCEDURE = ".GetWays(";
    private static final String COMMA = ", ";
    private static final String CLOSING_BRACKET = ");";
    private static final String SQL_ERROR_CANNOT_ALL_GETWAYS_PROCEDURE = "SQL error: call GetWays procedure was unsuccessful.";
    private static final String SQL_ERROR_CANNOT_READ_WAYS_TABLE = "SQL error: cannot read the list of ways.";

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

        p.put("user", userName);
        p.put("password", password);
        // setPort(5678); //<- Uncomment this if you wan't spark to listen on a
        // port different than 4567.

        get(new Route("/findFreeLot") {

            @Override
            public Object handle(Request request, Response response) {

                double lat = Double.parseDouble(request.queryParams("lat"));
                double lon = Double.parseDouble(request.queryParams("lon"));

                Set<String> queryParams = request.queryParams();

                int userId = 0;
                if (queryParams.contains("id")) {
                    userId = Integer.parseInt(request.queryParams("id"));
                }

                double radius = 0;
                if (queryParams.contains("rad")) {
                    radius = Double.parseDouble(request.queryParams("rad"));
                }

                return findFreeLot(lat, lon, userId, radius);
            }

        });

        get(new Route("/findFreeLotFromAddress") {

            @Override
            public Object handle(Request request, Response response) {

                String address = request.queryParams("address");

                Map<String, Double> coords;
                Double lat = null;
                Double lon = null;

                try {
                    coords = MapHandler.geocoding(address);
                    lat = coords.get("lat");
                    lon = coords.get("lon");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Set<String> queryParams = request.queryParams();

                int userId = 0;
                if (queryParams.contains("id")) {
                    userId = Integer.parseInt(request.queryParams("id"));
                }

                double radius = 0;
                if (queryParams.contains("rad")) {
                    radius = Double.parseDouble(request.queryParams("rad"));
                }

                return findFreeLot(lat, lon, userId, radius);
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
                        address = MapHandler.geocoding(lat, lon);
                        address = address.replace("'", "");
                    } catch (IOException e) {
                        address = "no address";
                    }

                    String sqlQueryInParkingLotsTable = "INSERT INTO "
                            + DATABASE
                            + ".smartparking_parking_lots (time_of_submission, latitude, longitude, user_id, parking_lot_availability, address) VALUES ("
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
                    String sqlUpdateQueryInUsersTable = "INSERT INTO "
                            + DATABASE
                            + ".smartparking_users (email, password, search_range, last_login, time_of_submission) "
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

                    String sqlQueryInUsersTable = "SELECT id FROM "
                            + DATABASE
                            + ".smartparking_users WHERE email = '"
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
                    String sqlQueryInUsersTable = "SELECT id, password FROM "
                            + DATABASE
                            + ".smartparking_users WHERE email='"
                            + mail
                            + "';";
                    String sqlQueryInUsersTableError = "SQL error: query in smartparking_users was unsuccessful.";
                    rs = CommonJdbcMethods.executeQueryStatement(stmt,
                            sqlQueryInUsersTable, sqlQueryInUsersTableError);

                    rs.last();
                    int size = rs.getRow();

                    if (size == 0) {
                        return "UNREGISTERED_EMAIL";
                    } else if (size == 1) {
                        if (pass.equals(rs.getString("password"))) {
                            int userId = rs.getInt("id");
                            String sqlUpdateQueryInUsersTable = "UPDATE "
                                    + DATABASE
                                    + ".smartparking_users SET last_login='"
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

    }

    private static List<RowInWays> getrowsInWays(ResultSet rs)
            throws ForwardedSqlException {

        ArrayList<RowInWays> lst = new ArrayList<RowInWays>();
        try {
            while (rs.next()) {
                RowInWays row = new RowInWays(rs.getInt(WAY_ID),
                        rs.getString(NAME_OF_WAY),
                        rs.getDouble(LATITUDE_1),
                        rs.getDouble(LONGITUDE_1),
                        rs.getDouble(LATITUDE_2),
                        rs.getDouble(LONGITUDE_2),
                        rs.getInt(ALL_SPACES),
                        rs.getInt(FREE_SPACES),
                        rs.getDouble(DISTANCE));

                lst.add(row);
            }
        } catch (SQLException e) {
            System.out
                    .println(SQL_ERROR_CANNOT_READ_WAYS_TABLE);
            e.printStackTrace();
            throw new ForwardedSqlException();
        }

        return lst;
    }

    private static String findFreeLot(double lat, double lon, int userId,
            double radius) {
        Connection c = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            c = DriverManager.getConnection(CONNECTION, p);
            stmt = c.createStatement();

            if (userId != 0) {
                try {
                    double storedRadius = CommonJdbcMethods
                            .manageUserParameters(userId, stmt, "lot_requests");
                    if (radius == 0) {
                        radius = storedRadius;
                    }
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

            if (radius == 0) {
                radius = 500;
            }

            String sqlCallGetWays = CALL
                    + DATABASE
                    + GET_WAYS_PROCEDURE
                    + lat
                    + COMMA
                    + lon
                    + COMMA
                    + (int) radius
                    + CLOSING_BRACKET;
            rs = CommonJdbcMethods.executeQueryStatement(stmt,
                    sqlCallGetWays,
                    SQL_ERROR_CANNOT_ALL_GETWAYS_PROCEDURE);

            if (rs == null) {
                return "RESULT_SET_IS_NULL";
            }

            List<RowInWays> lst = getrowsInWays(rs);

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

}
