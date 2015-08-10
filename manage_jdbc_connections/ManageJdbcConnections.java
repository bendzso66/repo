import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManageJdbcConnections {

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
                System.out.println("SQL error: can not close the statement.");
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("SQL error: can not close the connection.");
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
                System.out.println("SQL error: can not close the resultset.");
                e.printStackTrace();
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.out.println("SQL error: can not close the statement.");
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("SQL error: can not close the connection.");
                e.printStackTrace();
            }
        }
    }

}
