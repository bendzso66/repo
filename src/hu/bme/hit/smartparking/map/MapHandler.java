package hu.bme.hit.smartparking.map;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class MapHandler {

    private static final String GEOCODING_COORDS_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String GEOCODING_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String COMMA_IN_URL = "%2C";
    private static final String KEY_STRING = "&key=";
    private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

    public static String geocoding(String lat, String lon) throws IOException {
        URL url = new URL(GEOCODING_COORDS_URL
                + lat
                + COMMA_IN_URL
                + lon
                + KEY_STRING
                + API_KEY);
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
        URL url = new URL(GEOCODING_ADDRESS_URL
                + address
                + KEY_STRING
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
