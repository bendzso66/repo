import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;

public class iParkingInterface {

	private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/smart_parking";
	private static final int R = 6371; // corrected earth radius, km
	private static String userName;
	private static String password;

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		try {
			System.out.println("Enter MySQL login name:");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			userName = in.readLine();
			System.out.println("Enter password:");
			in = new BufferedReader(new InputStreamReader(System.in));
			password = in.readLine();
		} catch (IOException e) {
			System.out.println("IOException has been caught" + e);
		}

		Class.forName(DB_CLASS_NAME);
		Properties p = new Properties();
		p.put("user", userName);
		p.put("password", password);

		final Connection c = DriverManager.getConnection(CONNECTION, p);
		final Statement stmt = c.createStatement();

		// setPort(5678); //<- Uncomment this if you wan't spark to listen on a
		// port different than 4567.

		get(new Route("/findFreeLot") {

			@Override
			public Object handle(Request request, Response response) {
				try {
					double radius;
					// TODO do this without try-catch
					try {
						radius = Double.parseDouble(request.queryParams("rad"));
					} catch (Exception e) {
						radius = 1; // 1 km
					}
					// TODO try to find parking lots in SQL
					stmt.execute("SELECT * FROM smart_parking.parking_lots;");
					ArrayList<rowInParkingLots> lst = getrowsInParkingLots(
							stmt.getResultSet(),
							Double.parseDouble(request.queryParams("lat")),
							Double.parseDouble(request.queryParams("lon")),
							radius);
					Gson gson = new Gson();
					return gson.toJson(lst);
				} catch (SQLException e) {
					return "Exception" + e;
				}
			}

		});

		get(new Route("/sendFreeLot") {

			@Override
			public Object handle(Request request, Response response) {
				String answer;
				try {
					stmt.execute("INSERT INTO parking_lots (gps_time, latitude, longitude, user_id, parking_lot_availability, address) VALUES ("
							+ "'"
							+ request.queryParams("time")
							+ "','"
							+ request.queryParams("lat")
							+ "','"
							+ request.queryParams("lon")
							+ "','"
							+ request.queryParams("user")
							+ "','"
							+ request.queryParams("avail")
							+ "','"
							+ request.queryParams("addr") + "');");
					answer = "New row is created.";
				} catch (SQLException e) {
					response.status(202);
					answer = "Wrong syntax to create new row. Error message: "
							+ e;
				}
				return answer;
			}

		});

		get(new Route("/registration") {

			@Override
			public Object handle(Request request, Response response) {
				String mail = request.queryParams("mail");
				// TODO check password condition
				String pass = request.queryParams("pass");
				double radius = Double.parseDouble(request.queryParams("rad"));
				try {
					stmt.execute("INSERT INTO users (email, password, search_range, last_login, recommended_lots, lot_requests) "
							+ "VALUES ('"
							+ mail
							+ "','"
							+ pass
							+ "','"
							+ radius
							+ "','"
							+ System.currentTimeMillis()
							+ "','0','0');");
				} catch (SQLException e) {
					return "Unsuccessfull registration: " + e;
				}
				return "Registration is successfull.";
			}

		});

		get(new Route("/login") {

			@Override
			public Object handle(Request request, Response response) {
				String mail = request.queryParams("mail");
				String pass = request.queryParams("pass");
				try {
					ResultSet rs = stmt
							.executeQuery("SELECT user_id FROM users WHERE email='"
									+ mail + "'AND password='" + pass + "';");
					rs.last();
					int size = rs.getRow();
					if (size == 0) {
						return "Wrong email address or password!";
					} else if (size == 1) {
						return "Successfull login!";
					} else {
						// This should never happen!
						return "Login error!";
					}
				} catch (SQLException e) {
					return "Unsuccessfull registration: " + e;
				}
			}

		});

	}

	private static ArrayList<rowInParkingLots> getrowsInParkingLots(
			ResultSet rs, double lat1, double lon1, double radius)
			throws SQLException {

		ArrayList<rowInParkingLots> lst = new ArrayList<rowInParkingLots>();
		rowInParkingLots row = null;
		Double distance = null;

		while (rs.next()) {
			double dLat = Math.toRadians(Double.parseDouble(rs
					.getString("latitude")) - lat1);
			double dLon = Math.toRadians(Double.parseDouble(rs
					.getString("longitude")) - lon1);
			double a = Math.sin(dLat / 2)
					* Math.sin(dLat / 2)
					+ Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(Double.parseDouble(rs
							.getString("latitude")))) * Math.sin(dLon / 2)
					* Math.sin(dLon / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			distance = R * c;
			if (distance <= radius
					& rs.getString("parking_lot_availability").equals("free")) {
				row = new rowInParkingLots();
				row.setId(rs.getInt("ID"));
				row.setGpsTime(rs.getLong("gps_time"));
				row.setLatitude(rs.getDouble("latitude"));
				row.setLongitude(rs.getDouble("longitude"));
				row.setUserId(rs.getLong("user_id"));
				row.setParkingLotAvailability(rs
						.getString("parking_lot_availability"));
				row.setAddress(rs.getString("address"));

				lst.add(row);
			}
		}

		return lst;

	}
}