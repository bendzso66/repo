import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class getAddress {

    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String COMMA_IN_URL = "%2C";
    private static final String KEY_STRING = "&key=";
    private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

    public static void main(String[] args) throws Exception {
        double lat = 47.5238609631467;
        double lon = 19.097345216191794;
        String targetURL = GEOCODING_URL + lat + COMMA_IN_URL + lon + KEY_STRING + API_KEY;
        geocodeCoords(targetURL);
    }

    public static void geocodeCoords(String targetURL) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            Scanner scan = new Scanner(url.openStream());
            String str = new String();

            while (scan.hasNext()) {
                str += scan.nextLine();
            }
            scan.close();

            JSONObject obj = new JSONObject(str);
            JSONObject res = obj.getJSONArray("results").getJSONObject(0);

            String formattedAddress = res.getString("formatted_address");
            System.out.println(new String(formattedAddress.getBytes(), "UTF-8"));

            if (!obj.getString("status").equals("OK")) {
                System.out.println("not ok geocoding");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
