package hu.bme.hit.smartparking.servlet;

import static spark.Spark.get;

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
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static String userName = "smartparking";
    private static String password = "spict2015";

    private static final String FIND_FREE_LOT = "/findFreeLot";
    private static final String FIND_FREE_LOT_FROM_ADDRESS = "/findFreeLotFromAddress";
    private static final String SEND_LOT_AVAILABILITY = "/sendLotAvailability";
    private static final String REGISTRATION = "/registration";
    private static final String LOGIN = "/login";

    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String ID = "id";
    private static final String RAD = "rad";
    private static final String ADDRESS = "address";
    private static final String MAIL = "mail";
    private static final String PASS = "pass";
    private static final String AVAIL = "avail";
    private static final String FREE = "free";
    private static final String RESERVED = "reserved";

    private static final String LOT_REQUESTS = "lot_requests";
    private static final String RECOMMENDED_LOTS = "recommended_lots";
    private static final String WAY_ID = "way_id";
    private static final String NAME_OF_WAY = "name_of_way";
    private static final String CENTER_LATITUDE = "center_latitude";
    private static final String CENTER_LONGITUDE = "center_longitude";
    private static final String LATITUDE_1 = "latitude_1";
    private static final String LONGITUDE_1 = "longitude_1";
    private static final String LATITUDE_2 = "latitude_2";
    private static final String LONGITUDE_2 = "longitude_2";
    private static final String ALL_SPACES = "all_spaces";
    private static final String FREE_SPACES = "free_spaces";
    private static final String DISTANCE = "distance";

    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String INSERT_INTO_USERS_HEADER = ".smartparking_users (email, password, search_range, last_login, time_of_submission) VALUES ('";
    private static final String SELECT_ID_FROM = "SELECT id FROM ";
    private static final String USERS_WHERE_EMAIL_EQUALS = ".smartparking_users WHERE email='";
    private static final String SELECT_ID_PASSWOR_FROM = "SELECT id, password FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String USERS_SET_LAST_LOGIN_EQUALS = ".smartparking_users SET last_login='";
    private static final String WHERE_EMAIL_EQUALS = "' WHERE email='";
    private static final String AND_PASSWORD_EQUALS = "' AND password='";
    private static final String CALL = "CALL ";
    private static final String COMMA_WITH_QUATITION_MARKS = "','";
    private static final String GET_WAYS_PROCEDURE = ".GetWays(";
    private static final String COMMA = ", ";
    private static final String CLOSING_BRACKET = ");";
    private static final String GET_SECTIONS_PROCEDURE = ".GetSections(";
    private static final String SET_FREE_SPACE_PROCEDURE = ".SetFreeSpace(";
    private static final String SQL_ERROR_CANNOT_CALL_GETSECTIONS_PROCEDURE = "SQL error: call GetWays procedure was unsuccessful.";
    private static final String SQL_ERROR_CANNOT_UPDATE_USERS_TABLE = "SQL error: update in smartparking_users was unsuccessful.";
    private static final String SQL_ERROR_CANNOT_READUSERS_TABLE = "SQL error: query in smartparking_users was unsuccessful.";
    private static final String SQL_ERROR_CANNOT_CALL_GETWAYS_PROCEDURE = "SQL error: call GetWays procedure was unsuccessful.";
    private static final String SQL_ERROR_CANNOT_READ_WAYS_TABLE = "SQL error: cannot read the list of ways.";
    private static final String SQL_ERROR_CANNOT_CALL_SETFREESPACES_PROCEDURE = "SQL error: call SetFreeSpaces procedure was unsuccessful.";

    private static final String QUOTITION_MARK = "'";
    private static final String SEMICOLON = ";";
    private static final String CHECK_EMAIL_PATTERN = "com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '*@*.*' for key 'email'";

    private static final String GEOCODING_ERROR = "Geocoding error.";
    private static final String LOGIN_ERROR = "LOGIN_ERROR";
    private static final String SUCCESSFULL_REQUEST = "SUCCESSFULL_REQUEST";
    private static final String SQL_SERVER_ERROR = "SQL_SERVER_ERROR";
    private static final String SQL_QUERY_ERROR = "SQL_QUERY_ERROR";
    private static final String EMAIL_IS_ALREADY_REGISTERED = "EMAIL_IS_ALREADY_REGISTERED";
    private static final String UNREGISTERED_EMAIL = "UNREGISTERED_EMAIL";
    private static final String WRONG_PASSWORD = "WRONG_PASSWORD";
    private static final String DUPLICATED_USER = "DUPLICATED_USER";
    private static final String RESULT_SET_IS_NULL = "RESULT_SET_IS_NULL";
    private static final String SQL_CONNECTION_ERROR = "SQL_CONNECTION_ERROR";
    private static final String WRONG_AVAILABILITY_CONDITION = "WRONG_AVAILABILITY_CONDITION";

    private static final Properties p = new Properties();

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

        p.put(USER, userName);
        p.put(PASSWORD, password);
        // setPort(5678); //<- Uncomment this if you wan't spark to listen on a
        // port different than 4567.

        get(new Route(FIND_FREE_LOT) {

            @Override
            public Object handle(Request request, Response response) {

                Coordinates targetCoords = new Coordinates(Double.parseDouble(request.queryParams(LAT)),
                        Double.parseDouble(request.queryParams(LON)));

                Set<String> queryParams = request.queryParams();

                int userId = 0;
                if (queryParams.contains(ID)) {
                    userId = Integer.parseInt(request.queryParams(ID));
                }

                int radius = 0;
                if (queryParams.contains(RAD)) {
                    radius = Integer.parseInt(request.queryParams(RAD));
                }

                return findFreeLot(targetCoords, userId, radius);
            }

        });

        get(new Route(FIND_FREE_LOT_FROM_ADDRESS) {

            @Override
            public Object handle(Request request, Response response) {

                String address = request.queryParams(ADDRESS);

                Coordinates targetCoords = null;

                try {
                    Map<String, Double> geocodedAddress = MapHandler.geocoding(address);
                    targetCoords = new Coordinates(geocodedAddress.get(LAT),
                            geocodedAddress.get(LON));
                } catch (Exception e) {
                    System.out.println(GEOCODING_ERROR);
                    e.printStackTrace();
                }

                Set<String> queryParams = request.queryParams();

                int userId = 0;
                if (queryParams.contains(ID)) {
                    userId = Integer.parseInt(request.queryParams(ID));
                }

                int radius = 0;
                if (queryParams.contains(RAD)) {
                    radius = Integer.parseInt(request.queryParams(RAD));
                }

                return findFreeLot(targetCoords, userId, radius);
            }

        });

        get(new Route(SEND_LOT_AVAILABILITY) {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;
                ResultSet rs = null;

                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();

                    Set<String> queryParams = request.queryParams();

                    if (queryParams.contains(ID)) {
                        int userId = Integer.parseInt(request.queryParams(ID));

                        try {
                            CommonJdbcMethods.manageUserParameters(userId, stmt,
                                    RECOMMENDED_LOTS);
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
                            return LOGIN_ERROR;
                        }
                    }

                    String availability = request.queryParams(AVAIL);
                    ParkingLot lot;
                    if (availability.equals(FREE)) {
                        lot = new ParkingLot(Double.parseDouble(request.queryParams(LAT)),
                                Double.parseDouble(request.queryParams(LON)),
                                true);
                    } else if (availability.equals(RESERVED)) {
                        lot = new ParkingLot(Double.parseDouble(request.queryParams(LAT)),
                                Double.parseDouble(request.queryParams(LON)),
                                false);
                    } else {
                        return WRONG_AVAILABILITY_CONDITION;
                    }

                    String sqlCallGetSections = CALL
                            + DATABASE
                            + GET_SECTIONS_PROCEDURE
                            + lot.getLatitude()
                            + COMMA
                            + lot.getLongitude()
                            + CLOSING_BRACKET;
                    rs = CommonJdbcMethods.executeQueryStatement(stmt,
                            sqlCallGetSections,
                            SQL_ERROR_CANNOT_CALL_GETSECTIONS_PROCEDURE);

                    if (rs == null) {
                        return RESULT_SET_IS_NULL;
                    }

                    setSaturationInWays(stmt, rs, lot);

                    return SUCCESSFULL_REQUEST;
                } catch (SQLException e) {
                    return SQL_SERVER_ERROR;
                } catch (ForwardedSqlException e) {
                    return SQL_QUERY_ERROR;
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt, rs);
                }
            }

        });

        get(new Route(REGISTRATION) {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;
                ResultSet rs = null;

                // TODO check email
                String mail = request.queryParams(MAIL);
                // TODO check password condition
                String pass = request.queryParams(PASS);
                double radius = Double.parseDouble(request.queryParams(RAD));
                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();

                    long currentTime = System.currentTimeMillis();
                    String sqlUpdateQueryInUsersTable = INSERT_INTO
                            + DATABASE
                            + INSERT_INTO_USERS_HEADER
                            + mail
                            + COMMA_WITH_QUATITION_MARKS
                            + pass
                            + COMMA_WITH_QUATITION_MARKS
                            + radius
                            + COMMA_WITH_QUATITION_MARKS
                            + currentTime
                            + COMMA_WITH_QUATITION_MARKS
                            + +currentTime
                            + QUOTITION_MARK
                            + CLOSING_BRACKET;
                    CommonJdbcMethods.executeUpdateStatement(stmt,
                            sqlUpdateQueryInUsersTable,
                            SQL_ERROR_CANNOT_UPDATE_USERS_TABLE);

                    String sqlQueryInUsersTable = SELECT_ID_FROM
                            + DATABASE
                            + USERS_WHERE_EMAIL_EQUALS
                            + mail
                            + QUOTITION_MARK
                            + SEMICOLON;
                    rs = CommonJdbcMethods.executeQueryStatement(stmt,
                            sqlQueryInUsersTable, SQL_ERROR_CANNOT_READUSERS_TABLE);

                    rs.next();
                    int userId = rs.getInt(ID);

                    return userId;
                } catch (SQLException e) {
                    return SQL_SERVER_ERROR;
                } catch (ForwardedSqlException e) {
                    if (e.getCause().toString().matches(CHECK_EMAIL_PATTERN)) {
                        return EMAIL_IS_ALREADY_REGISTERED;
                    } else {
                        return SQL_QUERY_ERROR;
                    }
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt, rs);
                }
            }

        });

        get(new Route(LOGIN) {

            @Override
            public Object handle(Request request, Response response) {

                Connection c = null;
                Statement stmt = null;
                ResultSet rs = null;

                String mail = request.queryParams(MAIL);
                String pass = request.queryParams(PASS);

                try {
                    c = DriverManager.getConnection(CONNECTION, p);
                    stmt = c.createStatement();
                    String sqlQueryInUsersTable = SELECT_ID_PASSWOR_FROM
                            + DATABASE
                            + USERS_WHERE_EMAIL_EQUALS
                            + mail
                            + QUOTITION_MARK
                            + SEMICOLON;
                    rs = CommonJdbcMethods.executeQueryStatement(stmt,
                            sqlQueryInUsersTable, SQL_ERROR_CANNOT_READUSERS_TABLE);

                    rs.last();
                    int size = rs.getRow();

                    if (size == 0) {
                        return UNREGISTERED_EMAIL;
                    } else if (size == 1) {
                        if (pass.equals(rs.getString(PASSWORD))) {
                            int userId = rs.getInt(ID);
                            String sqlUpdateQueryInUsersTable = UPDATE
                                    + DATABASE
                                    + USERS_SET_LAST_LOGIN_EQUALS
                                    + System.currentTimeMillis()
                                    + WHERE_EMAIL_EQUALS
                                    + mail
                                    + AND_PASSWORD_EQUALS
                                    + pass
                                    + QUOTITION_MARK
                                    + SEMICOLON;
                            CommonJdbcMethods.executeUpdateStatement(stmt,
                                    sqlUpdateQueryInUsersTable,
                                    SQL_ERROR_CANNOT_UPDATE_USERS_TABLE);

                            return userId;
                        } else {
                            return WRONG_PASSWORD;
                        }
                    } else {
                        // This should never happen!
                        return DUPLICATED_USER;
                    }
                } catch (SQLException e) {
                    return SQL_SERVER_ERROR;
                } catch (ForwardedSqlException e) {
                    return SQL_QUERY_ERROR;
                } finally {
                    CommonJdbcMethods.closeConnections(c, stmt, rs);
                }
            }

        });

    }

    private static List<RowInWays> getRowsInWays(ResultSet rs)
            throws ForwardedSqlException {

        ArrayList<RowInWays> lst = new ArrayList<RowInWays>();
        try {
            while (rs.next()) {
                RowInWays row = new RowInWays(rs.getInt(WAY_ID),
                        rs.getString(NAME_OF_WAY),
                        rs.getDouble(CENTER_LATITUDE),
                        rs.getDouble(CENTER_LONGITUDE),
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

    private static void setSaturationInWays(Statement stmt, ResultSet rs, ParkingLot lot)
            throws ForwardedSqlException {

        try {
            int closestWayId = 0;
            double minDistance = Double.MAX_VALUE;

            while (rs.next()) {
                Coordinates lineCoords1 = new Coordinates(rs.getDouble(LATITUDE_1), rs.getDouble(LONGITUDE_1));
                Coordinates lineCoords2 = new Coordinates(rs.getDouble(LATITUDE_2), rs.getDouble(LONGITUDE_2));
                double distance = MapHandler.getDistanceFromSection(lot, lineCoords1, lineCoords2);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestWayId = rs.getInt(WAY_ID);
                }
            }

            String sqlUpdateWaysStaturation = CALL
                    + DATABASE
                    + SET_FREE_SPACE_PROCEDURE
                    + closestWayId
                    + COMMA
                    + (lot.getAvailability() ? 1 : 0)
                    + CLOSING_BRACKET;
            CommonJdbcMethods.executeQueryStatement(stmt, sqlUpdateWaysStaturation,
                    SQL_ERROR_CANNOT_CALL_SETFREESPACES_PROCEDURE);
        } catch (SQLException e) {
            System.out.println(SQL_ERROR_CANNOT_READ_WAYS_TABLE);
            e.printStackTrace();
            throw new ForwardedSqlException();
        }
    }

    private static String findFreeLot(Coordinates targetCoords, int userId,
            int radius) {
        Connection c = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            c = DriverManager.getConnection(CONNECTION, p);
            stmt = c.createStatement();

            if (userId != 0) {
                try {
                    int storedRadius = CommonJdbcMethods
                            .manageUserParameters(userId, stmt, LOT_REQUESTS);
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
                    return LOGIN_ERROR;
                }
            }

            if (radius == 0) {
                radius = 500;
            }

            String sqlCallGetWays = CALL
                    + DATABASE
                    + GET_WAYS_PROCEDURE
                    + targetCoords.getLatitude()
                    + COMMA
                    + targetCoords.getLongitude()
                    + COMMA
                    + radius
                    + CLOSING_BRACKET;
            rs = CommonJdbcMethods.executeQueryStatement(stmt,
                    sqlCallGetWays,
                    SQL_ERROR_CANNOT_CALL_GETWAYS_PROCEDURE);

            if (rs == null) {
                return RESULT_SET_IS_NULL;
            }

            List<RowInWays> lst = getRowsInWays(rs);

            Gson gson = new Gson();
            return gson.toJson(lst);

        } catch (SQLException e) {
            e.printStackTrace();
            return SQL_CONNECTION_ERROR;
        } catch (ForwardedSqlException e) {
            return SQL_QUERY_ERROR;
        } finally {
            CommonJdbcMethods.closeConnections(c, stmt, rs);
        }
    }

}
