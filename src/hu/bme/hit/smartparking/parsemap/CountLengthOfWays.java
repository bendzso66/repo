package hu.bme.hit.smartparking.parsemap;

import hu.bme.hit.smartparking.jdbc.CommonJdbcMethods;
import hu.bme.hit.smartparking.jdbc.ForwardedSqlException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class CountLengthOfWays {

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String HOST = "localhost";
    private static final String DATABASE = "vehicle_data";
    private static final String CONNECTION = "jdbc:mysql://"
            + HOST
            + "/"
            + DATABASE
            + "?useUnicode=yes&characterEncoding=UTF-8";
    private static final String USER = "user";
    private static final String userName = "smartparking";
    private static final String PASSWORD = "password";
    private static final String pass = "spict2015";

    private static final String CONNECTION_ERROR = "SQL error: cannot create the connection.";
    private static final String SQL_QUERY_ERROR = "SQL error: cannot execute query.";
    private static final String SQL_CONNECTIONS_ARE_CLOSED = "SQL connections are closed!";

    private static final String WAY_ID = "way_id";
    private static final String LENGTH_OF_WAY = "length_of_way";
    private static final String WAYS_UPDATE_ERROR_MESSAGE = "SQL error: cannot execute update query in table budapest_ways.";
    private static final String UPDATE = "UPDATE ";
    private static final String BUDAPEST_WAYS_TABLE = "vehicle_data.budapest_ways ";
    private static final String SET_LENGTH_OF_WAY_EQUALS = "SET budapest_ways.length_of_way=";
    private static final String COMMA = ", ";
    private static final String ALL_SPACES_EQUALS = "budapest_ways.all_spaces=";
    private static final String WHERE_WAY_ID_EQUALS = " WHERE " + WAY_ID + "=";
    private static final String SEMICOLON = ";";

    private static final String NUM_OF_LANES = "num_of_lanes";
    private static final String FROM_LANES_TABLE_WEHER_WAY_ID_EQUALS = " FROM vehicle_data.budapest_parking_lanes"
            + WHERE_WAY_ID_EQUALS;
    private static final String SELECT_COUNT_FROM_LANES_TABLE_WHERE_WAY_ID_EQUALS = "SELECT count(*) AS "
            + NUM_OF_LANES
            + FROM_LANES_TABLE_WEHER_WAY_ID_EQUALS;
    private static final String COUNT_LANES_ERROR_MESSAGE = "SQL error: cannot select count update query in table budapest_parking_lanes.";
    private static final String SELECT_FROM_LANES_TABLE_WHERE_WAY_ID_EQUALS = "SELECT side, direction"
            + FROM_LANES_TABLE_WEHER_WAY_ID_EQUALS;
    private static final String LANES_TABLE_ERROR_MESSAGE = "SQL error: cannot execute select query in table budapest_parking_lanes.";
    private static final String DOUBLED_BOTH_LANE_ERROR = "ERROR: Two both lane founded at way: ";

    private static final String SIDE = "side";
    private static final String DIRECTION = "direction";
    private static final String BOTH = "both";
    private static final String PARALLEL = "parallel";
    private static final String DIAGONAL = "diagonal";
    private static final String PERPENDICULAR = "perpendicular";
    private static final String NO_PARKING = "no_parking";
    private static final String NO_STOPPING = "no_stopping";

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put(USER, userName);
        p.put(PASSWORD, pass);

        try {
            final Connection c = DriverManager.getConnection(CONNECTION, p);
            final Statement sectionsStatement = c.createStatement();
            final Statement waysStatement = c.createStatement();
            final Statement lanesStatement = c.createStatement();

            String sqlQueryFromSectionsTable = "SELECT way_id, sum(length_of_section) AS length_of_way FROM "
                    + DATABASE
                    + ".budapest_way_sections"
                    + " GROUP BY way_id;";
            String sqlQueryFromSectionsTableErrorMsg = "SQL error: query from budapest_ways was unsuccessful.";
            ResultSet sectionsResultSet = CommonJdbcMethods
                    .executeQueryStatement(sectionsStatement,
                            sqlQueryFromSectionsTable,
                            sqlQueryFromSectionsTableErrorMsg);

            int wayCounter = 1;
            while (sectionsResultSet.next()) {
                long wayId = sectionsResultSet.getLong(WAY_ID);
                double lengthOfWay = sectionsResultSet.getDouble(LENGTH_OF_WAY);

                String sqlCountQueryFromLanesTable = SELECT_COUNT_FROM_LANES_TABLE_WHERE_WAY_ID_EQUALS
                        + wayId
                        + SEMICOLON;
                ResultSet lanesCounterResultSet = CommonJdbcMethods
                        .executeQueryStatement(lanesStatement,
                                sqlCountQueryFromLanesTable,
                                COUNT_LANES_ERROR_MESSAGE);
                lanesCounterResultSet.next();
                int numOfLanes = lanesCounterResultSet.getInt(NUM_OF_LANES);
                CommonJdbcMethods.closeResultSet(lanesCounterResultSet);

                int numOfSpaces = 0;
                if (numOfLanes > 0) {
                    String sqlQueryFromLanesTable = SELECT_FROM_LANES_TABLE_WHERE_WAY_ID_EQUALS
                            + wayId
                            + SEMICOLON;
                    ResultSet lanesResultSet = CommonJdbcMethods
                            .executeQueryStatement(lanesStatement,
                                    sqlQueryFromLanesTable,
                                    LANES_TABLE_ERROR_MESSAGE);

                    boolean foundBoth = false;
                    while (lanesResultSet.next()) {
                        String side = lanesResultSet.getString(SIDE);
                        String direction = lanesResultSet.getString(DIRECTION);

                        numOfSpaces += countCapacity(lengthOfWay, direction,
                                wayId);
                        if (side.equals(BOTH)) {
                            if (foundBoth) {
                                System.out.println(DOUBLED_BOTH_LANE_ERROR
                                        + wayId);
                                System.exit(1);
                            }
                            foundBoth = true;
                            numOfSpaces *= 2;
                        }
                    }
                    CommonJdbcMethods.closeResultSet(lanesResultSet);
                } else {
                    numOfSpaces = countCapacity(lengthOfWay, PARALLEL, wayId);
                }

                String sqlUpdateQueryInWaysTable = UPDATE
                        + BUDAPEST_WAYS_TABLE
                        + SET_LENGTH_OF_WAY_EQUALS
                        + lengthOfWay
                        + COMMA
                        + ALL_SPACES_EQUALS
                        + numOfSpaces
                        + WHERE_WAY_ID_EQUALS
                        + wayId
                        + SEMICOLON;
                CommonJdbcMethods.executeUpdateStatement(waysStatement,
                        sqlUpdateQueryInWaysTable, WAYS_UPDATE_ERROR_MESSAGE);
                System.out.println(wayCounter++);
            }

            CommonJdbcMethods.closeConnections(c, lanesStatement);
            CommonJdbcMethods.closeConnections(c, sectionsStatement,
                    sectionsResultSet);
            CommonJdbcMethods.closeConnections(c, waysStatement);
            System.out.println(SQL_CONNECTIONS_ARE_CLOSED);
        } catch (SQLException e) {
            System.out.println(CONNECTION_ERROR);
            e.printStackTrace();
        } catch (ForwardedSqlException e) {
            System.out.println(SQL_QUERY_ERROR);
            e.printStackTrace();
        }

    }

    private static int countCapacity(double lengthOfWay, String direction,
            long wayId) {

        double lengthOfParallelCar = 100.0 / 17.0;
        double lengthOfDiagonalCar = 100.0 / 31.0;
        double lengthOfPerpendicularCar = 100.0 / 40.0;

        int capacity;

        if (direction.equals(PARALLEL)) {
            capacity = (int) Math.floor(lengthOfWay / lengthOfParallelCar);
        } else if (direction.equals(DIAGONAL)) {
            capacity = (int) Math.floor(lengthOfWay / lengthOfDiagonalCar);
        } else if (direction.equals(PERPENDICULAR)) {
            capacity = (int) Math.floor(lengthOfWay / lengthOfPerpendicularCar);
        } else if (direction.equals(NO_PARKING)
                || direction.equals(NO_STOPPING)) {
            capacity = 0;
        } else {
            System.out.println("WARNING: Unknown direction: "
                    + direction
                    + " at way_id: "
                    + wayId);
            capacity = 0;
        }

        return capacity;
    }

}
