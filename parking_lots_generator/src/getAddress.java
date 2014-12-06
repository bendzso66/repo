import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getAddress {

	private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
	private static final String COMMA_IN_URL = "%2C";
	private static final String KEY_STRING = "&key=";
	private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

	public static void main(String[] args) throws Exception {
		double lat = 47.5238609631467;
		double lon = 19.097345216191794;
		String targetURL = GEOCODING_URL + lat + COMMA_IN_URL + lon + KEY_STRING + API_KEY;
		String response = executeGet(targetURL);
		System.out.println(response);
	}

	public static String executeGet(String targetURL) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
