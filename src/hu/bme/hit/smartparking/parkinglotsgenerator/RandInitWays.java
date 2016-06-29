package hu.bme.hit.smartparking.parkinglotsgenerator;

import hu.bme.hit.smartparking.jdbc.CommonJdbcMethods;
import hu.bme.hit.smartparking.jdbc.ForwardedSqlException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class RandInitWays {

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

    private static final String WAY_ID = "way_id";
    private static final String ALL_SPACES = "all_spaces";
    private static final String UPDATE_BUDAPEST_WAYS_SET_FREE_SPACES_EQUALS = "UPDATE vehicle_data.budapest_ways SET free_spaces=";
    private static final String WHERE_WAY_ID_EQUALS = " WHERE way_id=";
    private static final String SEMICOLON = ";";
    private static final String SQL_UPDATE_IN_BUDAPEST_WAYS_ERROR_MSG = "SQL error: update in budapest_ways table wasn't successfull.";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, ForwardedSqlException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put(USER, userName);
        p.put(PASSWORD, password);

        Connection c = null;
        Statement selectStmt = null;
        Statement updateStmt = null;
        ResultSet rs = null;


        try {
            c = DriverManager.getConnection(CONNECTION, p);
            selectStmt = c.createStatement();
            updateStmt = c.createStatement();

            String sqlQueryFromWays = "SELECT way_id, all_spaces FROM vehicle_data.budapest_ways;";
            String sqlQueryFromWaysErrorMsg = "SQL error: query from budapest_ways table wasn't successfull.";
            rs = CommonJdbcMethods.executeQueryStatement(selectStmt, sqlQueryFromWays, sqlQueryFromWaysErrorMsg);

            int wayCounter = 0;
            while (rs.next()) {
                long wayId = rs.getLong(WAY_ID);
                int allSpaces = rs.getInt(ALL_SPACES);
                double randNum = ThreadLocalRandom.current().nextDouble(0, 0.4);
                int freeSpaces = (int) Math.round(allSpaces * randNum);

                String sqlUpdateInWays = UPDATE_BUDAPEST_WAYS_SET_FREE_SPACES_EQUALS
                        + freeSpaces
                        + WHERE_WAY_ID_EQUALS
                        + wayId
                        + SEMICOLON;
                CommonJdbcMethods.executeUpdateStatement(updateStmt, sqlUpdateInWays, SQL_UPDATE_IN_BUDAPEST_WAYS_ERROR_MSG);

                System.out.println(wayCounter++);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CommonJdbcMethods.closeConnections(c, selectStmt, rs);
            CommonJdbcMethods.closeConnections(c, updateStmt);
        }
    }

}
