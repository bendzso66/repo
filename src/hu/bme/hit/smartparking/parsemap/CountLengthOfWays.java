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
	private static final String WHERE_WAY_ID_EQUALS = " WHERE " + WAY_ID + " =";
	private static final String SEMICOLON = ";";

	public static void main(String argv[]) throws ClassNotFoundException {

		Class.forName(DB_CLASS_NAME);
		final Properties p = new Properties();
		p.put(USER, userName);
		p.put(PASSWORD, pass);

		try {
			final Connection c = DriverManager.getConnection(CONNECTION, p);
			final Statement sectionsStatement = c.createStatement();
			final Statement waysStatement = c.createStatement();

			String sqlQueryFromSectionsTable = "SELECT way_id, sum(length_of_section) AS length_of_way FROM "
					+ DATABASE + ".budapest_way_sections" + " GROUP BY way_id;";
			String sqlQueryFromSectionsTableErrorMsg = "SQL error: query from budapest_ways was unsuccessful.";
			ResultSet sectionsResultSet = CommonJdbcMethods
					.executeQueryStatement(sectionsStatement,
							sqlQueryFromSectionsTable,
							sqlQueryFromSectionsTableErrorMsg);

			int wayCounter = 1;
			while (sectionsResultSet.next()) {
				long wayId = sectionsResultSet.getLong(WAY_ID);
				double lengthOfWay = sectionsResultSet.getDouble(LENGTH_OF_WAY);
				String sqlUpdateQueryInWaysTable = UPDATE
						+ BUDAPEST_WAYS_TABLE
						+ SET_LENGTH_OF_WAY_EQUALS
						+ lengthOfWay
						+ WHERE_WAY_ID_EQUALS
						+ wayId
						+ SEMICOLON;
				CommonJdbcMethods.executeUpdateStatement(waysStatement, sqlUpdateQueryInWaysTable, WAYS_UPDATE_ERROR_MESSAGE);
				System.out.println(wayCounter++);
			}

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

}
