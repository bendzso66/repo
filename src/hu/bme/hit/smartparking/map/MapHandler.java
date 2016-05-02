package hu.bme.hit.smartparking.map;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;

import hu.bme.hit.smartparking.map.Coordinates;

public class MapHandler {

    private static final String GEOCODING_COORDS_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String GEOCODING_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String COMMA_IN_URL = "%2C";
    private static final String KEY_STRING = "&key=";
    private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

    private static final int R = 6371; // corrected earth radius in km

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

    public static Map<String, Double> geocoding(String address)
            throws Exception {
        String urlString = GEOCODING_ADDRESS_URL
                + URLEncoder.encode(address, "UTF-8")
                + KEY_STRING
                + API_KEY;
        URL url = new URL(urlString);
        Scanner scan = new Scanner(url.openStream());
        String str = new String();

        while (scan.hasNext()) {
            str += scan.nextLine();
        }
        scan.close();

        JSONObject obj = new JSONObject(str);
        if (!obj.getString("status").equals("OK")) {
            throw new Exception();
        } else {
            JSONObject res = obj.getJSONArray("results").getJSONObject(0);
            JSONObject geom = new JSONObject(res.get("geometry").toString());
            JSONObject loc = new JSONObject(geom.get("location").toString());

            String lat = loc.get("lat").toString();
            String lon = loc.get("lng").toString();

            Map<String, Double> result = new HashMap<String, Double>();
            result.put("lat", Double.parseDouble(lat));
            result.put("lon", Double.parseDouble(lon));

            return result;
        }
    }

    public static double getDistance(Coordinates startCoords, Coordinates endCoords) {
        double latDiff = Math.toRadians(startCoords.getLatitude() - endCoords.getLatitude());
        double lonDiff = Math.toRadians(startCoords.getLongitude() - endCoords.getLongitude());
        double a = Math.sin(latDiff / 2)
                * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(endCoords.getLatitude()))
                * Math.cos(Math.toRadians(startCoords.getLatitude()))
                * Math.sin(lonDiff / 2)
                * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    public static double getDistanceFromSection(Coordinates pointCoords, Coordinates lineCoords1, Coordinates lineCoords2) {
        double dLat21 = lineCoords2.getLatitude() - lineCoords1.getLatitude();
        double dLon21 = lineCoords2.getLongitude() - lineCoords1.getLongitude();

        double lengthSquare = dLat21 * dLat21 + dLon21 * dLon21;
        double param = -1.0;

        if (lengthSquare != 0) {
            double dLat1 = pointCoords.getLatitude() - lineCoords1.getLatitude();
            double dLon1 = pointCoords.getLongitude() - lineCoords1.getLongitude();
            double dot = dLat1 * dLat21 + dLon1 * dLon21;

            param = dot / lengthSquare;
        }

        double closestLat, closestLon;
        if (param < 0) {
            closestLat = lineCoords1.getLatitude();
            closestLon = lineCoords1.getLongitude();
          }
          else if (param > 1) {
            closestLat = lineCoords2.getLatitude();
            closestLon = lineCoords2.getLongitude();
          }
          else {
            closestLat = lineCoords1.getLatitude() + param * dLat21;
            closestLon = lineCoords1.getLongitude() + param * dLon21;
          }

          double dLat = pointCoords.getLatitude() - closestLat;
          double dLon = pointCoords.getLongitude() - closestLon;
          return Math.sqrt(dLat * dLat + dLon * dLon);
    }

}
