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
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/smart_parking";
	private static final int R = 6371; // corrected earth radius, km
	private static final int ONE_DAY = 600000 * 6 * 24;
	private static String userName;
	private static String password;

	private static final String GEOCODING_COORDS_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
	private static final String GEOCODING_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
	private static final String COMMA_IN_URL = "%2C";
	private static final String KEY_STRING = "&key=";
	private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

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
				double radius = 0;
				Set<String> queryParams = request.queryParams();
				if (queryParams.contains("rad")) {
					radius = Double.parseDouble(request.queryParams("rad"));
				}
				try {
					if (queryParams.contains("id")) {
						int id = Integer.parseInt(request.queryParams("id"));
						ResultSet rs = stmt
								.executeQuery("SELECT user_id, last_login, search_range FROM users WHERE user_id='"
										+ id + "';");
						rs.last();
						int size = rs.getRow();

						if (size == 0) {
							return "INVALID_ID";
						} else if (size == 1) {
							if (rs.getLong("last_login") + ONE_DAY < System
									.currentTimeMillis()) {
								return "NOT_LOGED_IN";
							} else {
								if (radius == 0) {
									radius = rs.getDouble("search_range");
								}
								stmt.execute("UPDATE users SET lot_requests = lot_requests + 1 , last_login = '"
										+ System.currentTimeMillis()
										+ "' WHERE user_id='" + id + "';");
							}
						} else {
							// This should never happen!
							return "DUPLICATED_USER";
						}
					}

					if (radius == 0) {
						radius = 0.5;
					}

					// TODO try to find parking lots in SQL
					stmt.execute("SELECT * FROM smart_parking.parking_lots;");
					List<rowInParkingLots> lst = getrowsInParkingLots(
							stmt.getResultSet(),
							Double.parseDouble(request.queryParams("lat")),
							Double.parseDouble(request.queryParams("lon")),
							radius);
					Gson gson = new Gson();

					return gson.toJson(lst);

				} catch (SQLException e) {
					return "SQL_SERVER_ERROR";
				}
			}

		});

		get(new Route("/sendFreeLot") {

			@Override
			public Object handle(Request request, Response response) {

				int userId = 0;
				Set<String> queryParams = request.queryParams();
				try {
					if (queryParams.contains("id")) {
						userId = Integer.parseInt(request.queryParams("id"));
						ResultSet rs = stmt
								.executeQuery("SELECT user_id, last_login FROM users WHERE user_id='"
										+ userId + "';");
						rs.last();
						int size = rs.getRow();
						if (size == 0) {
							return "INVALID_ID";
						} else if (size == 1) {
							if (rs.getLong("last_login") + ONE_DAY < System
									.currentTimeMillis()) {
								return "NOT_LOGED_IN";
							} else {
								stmt.execute("UPDATE users SET recommended_lots = recommended_lots + 1 WHERE user_id='"
										+ userId + "';");
							}
						} else {
							// This should never happen!
							return "DUPLICATED_USER";
						}
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

					stmt.execute("INSERT INTO parking_lots (gps_time, latitude, longitude, user_id, parking_lot_availability, address) VALUES ("
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
							+ "');");

					return "SUCCESSFULL_REQUEST";
				} catch (SQLException e) {
					return "SQL_SERVER_ERROR";
				}
			}

		});

		get(new Route("/registration") {

			@Override
			public Object handle(Request request, Response response) {
				String mail = request.queryParams("mail");
				// TODO check password condition
				String pass = request.queryParams("pass");
				double radius = Double.parseDouble(request.queryParams("rad"));
				int userId;
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
					ResultSet rs = stmt
							.executeQuery("SELECT user_id FROM users WHERE email = '"
									+ mail + "';");
					rs.next();
					userId = rs.getInt("user_id");
				} catch (SQLException e) {
					return "USED_MAIL_ADDRESS";
				}
				return userId;
			}

		});

		get(new Route("/login") {

			@Override
			public Object handle(Request request, Response response) {
				String mail = request.queryParams("mail");
				String pass = request.queryParams("pass");
				try {
					ResultSet rs = stmt
							.executeQuery("SELECT user_id, password FROM users WHERE email='"
									+ mail + "';");
					rs.last();
					int size = rs.getRow();
					if (size == 0) {
						return "INVALID_MAIL";
					} else if (size == 1) {
						if (pass.equals(rs.getString("password"))) {
							int userId = rs.getInt("user_id");
							stmt.execute("UPDATE users SET last_login='"
									+ System.currentTimeMillis()
									+ "' WHERE email='" + mail
									+ "'AND password='" + pass + "';");
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
			double lat1, double lon1, double radius) throws SQLException {

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
				row.setDistance(distance);

				lst.add(row);
			}
		}
		Collections.sort(lst);
		return lst;
	}

	public static String geocoding(String lat, String lon) throws IOException {
		URL url = new URL(GEOCODING_COORDS_URL + lat + COMMA_IN_URL + lon
				+ KEY_STRING + API_KEY);
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
		URL url = new URL(GEOCODING_ADDRESS_URL + address + KEY_STRING
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
