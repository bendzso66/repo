import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CommonJdbcMethods {

    private static final int ONE_DAY = 600000 * 6 * 24 * 1000;

    private static final String CLOSE_RESULTSET_ERROR = "SQL error: cannot close the resultset.";
    private static final String CLOSE_STATEMENT_ERROR = "SQL error: cannot close the statement.";
    private static final String CLOSE_CONNECTION_ERROR = "SQL error: cannot close the connection.";

    public static ResultSet executeQueryStatement(Statement stmt,
            String sqlStmt, String errorMsg) throws ForwardedSqlException {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sqlStmt);
        } catch (SQLException e) {
            System.out.println(errorMsg);
            System.out.println(sqlStmt);
            e.printStackTrace();
            throw new ForwardedSqlException();
        }
        return rs;
    }

    public static void executeUpdateStatement(Statement stmt, String sqlStmt,
            String errorMsg) throws ForwardedSqlException {
        try {
            stmt.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            System.out.println(errorMsg);
            System.out.println(sqlStmt);
            e.printStackTrace();
            throw new ForwardedSqlException();
        }
    }

    public static double manageUserParameters(int userId, Statement stmt,
            String updateableUserField) throws Exception {

        int size = 0;
        double radius = 0;

        String sqlQueryFromUsersTable = "SELECT id, last_login, search_range FROM vehicle_data.smartparking_users WHERE id='"
                + userId
                + "';";
        String sqlQueryFromUsersTableErrorMsg = "SQL error: query from smartparking_users was unsuccessful.";
        ResultSet rs = executeQueryStatement(stmt, sqlQueryFromUsersTable,
                sqlQueryFromUsersTableErrorMsg);

        try {
            rs.last();
            size = rs.getRow();

            if (size == 1) {

                long lastLoginTime = 0;
                lastLoginTime = rs.getLong("last_login");

                if (lastLoginTime == 0) {
                    throw new InvalidLoginTimeException("INVALID_LOGIN_TIME");
                } else if (lastLoginTime + ONE_DAY < System.currentTimeMillis()) {
                    throw new NotLoggedInException("NOT_LOGGED_IN");
                } else {
                    radius = rs.getDouble("search_range");
                    String sqlUpdateInUsersTable = "UPDATE vehicle_data.smartparking_users SET "
                            + updateableUserField
                            + " = "
                            + updateableUserField
                            + " + 1 , last_login = '"
                            + System.currentTimeMillis()
                            + "' WHERE id='"
                            + userId
                            + "';";
                    String sqlUpdateInUsersTableErrorMsg = "SQL error: update in smartparking_users was unsuccessful.";
                    executeUpdateStatement(stmt, sqlUpdateInUsersTable,
                            sqlUpdateInUsersTableErrorMsg);
                }
            } else if (size == 0) {
                throw new InvalidIdException("INVALID_ID");
            } else {
                // This should never happen!
                throw new DuplicatedUserException("DUPLICATED_USER");
            }

        } catch (SQLException e) {
            String getRowErrorMsg = "JDBC error: cannot get the resultset from users table.";
            System.out.println(getRowErrorMsg);
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }

        return radius;
    }

    public static void closeConnections(Connection conn, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.out.println(CLOSE_STATEMENT_ERROR);
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(CLOSE_CONNECTION_ERROR);
                e.printStackTrace();
            }
        }
    }

    public static void closeConnections(Connection conn, Statement stmt,
            ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(CLOSE_RESULTSET_ERROR);
                e.printStackTrace();
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.out.println(CLOSE_STATEMENT_ERROR);
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(CLOSE_CONNECTION_ERROR);
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(CLOSE_RESULTSET_ERROR);
                e.printStackTrace();
            }
        }
    }

}
