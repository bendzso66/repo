import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CommonJdbcMethods {

    private static final String CLOSE_RESULTSET_ERROR = "SQL error: cannot close the resultset.";
    private static final String CLOSE_STATEMENT_ERROR = "SQL error: cannot close the statement.";
    private static final String CLOSE_CONNECTION_ERROR = "SQL error: cannot close the connection.";

    public static void executeStatement(Statement stmt, String sqlStmt,
            String errorMsg) {
        try {
            stmt.execute(sqlStmt);
        } catch (SQLException e) {
            System.out.println(errorMsg);
            System.out.println(sqlStmt);
            e.printStackTrace();
        }
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

}
