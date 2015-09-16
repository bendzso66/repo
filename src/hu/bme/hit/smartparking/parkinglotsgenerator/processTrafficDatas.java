package hu.bme.hit.smartparking.parkinglotsgenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;

public class processTrafficDatas {

    static Random randIdAndAvailability = new Random();

    static String newLine;
    static String availability;

    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String COMMA_IN_URL = "%2C";
    private static final String KEY_STRING = "&key=";
    private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";
    private static final String FILE_NAME = "d://traffic.db";

    public static void main(String[] args) throws Exception {
        PrintWriter writer = new PrintWriter("d://insert_traffic_lines.sql", "UTF-8");
        Scanner input = new Scanner(new FileInputStream(FILE_NAME));
        for (int i = 0; i < 2400; i++) {
            for (int j = 0; j < 10; j++) {
                input.nextLine();
            }
            input.next();
            input.next();
            input.next();
            double latitude = Double.parseDouble(input.next());
            double longitude = Double.parseDouble(input.next());

            if (randIdAndAvailability.nextBoolean()) {
                availability = "free";
            } else {
                availability = "reserved";
            }

            String targetURL = GEOCODING_URL + latitude + COMMA_IN_URL + longitude + KEY_STRING + API_KEY;
            String address = geocodeCoords(targetURL);

            newLine = "INSERT INTO smartparking_parking_lots (gps_time, latitude, longitude, user_id, parking_lot_availability, address)\nVALUES ("
                    + "'"
                    + System.currentTimeMillis()
                    + "','"
                    + latitude
                    + "','"
                    + longitude
                    + "','"
                    + (randIdAndAvailability.nextInt(2000) + 1)
                    + "','"
                    + availability
                    + "','"
                    + address
                    + "');";
            System.out.println(newLine);
            writer.println(newLine);
            Thread.sleep(110);
        }
        writer.close();
        input.close();
    }

    public static String geocodeCoords(String targetURL) throws IOException {
        URL url;
        url = new URL(targetURL);
        Scanner scan = new Scanner(url.openStream());
        String str = new String();

        while (scan.hasNext()) {
            str += scan.nextLine();
        }
        scan.close();

        JSONObject obj = new JSONObject(str);
        if (!obj.getString("status").equals("OK")) {
            return "no valid address";
        } else {
            JSONObject res = obj.getJSONArray("results").getJSONObject(0);
            String formattedAddress = res.getString("formatted_address");
            String encodedAddress = new String(formattedAddress.getBytes(), "UTF-8");
            return encodedAddress;
        }
    }

}
