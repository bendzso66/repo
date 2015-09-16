package hu.bme.hit.smartparking.parkinglotsgenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;

public class generateParkingLots {

    static double maxLong = 19.178057;
    static double minLong = 18.963774;
    static double rangeLong = maxLong - minLong;
    static Random randLong = new Random();

    static double maxLat = 47.543692;
    static double minLat = 47.473005;
    static double rangeLat = maxLat - minLat;
    static Random randLat = new Random();

    static Random randIdAndAvailability = new Random();

    static String newLine;
    static String availability;

    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String COMMA_IN_URL = "%2C";
    private static final String KEY_STRING = "&key=";
    private static final String API_KEY = "AIzaSyBGcrE7i3y8AsCY5R7ZEHIWB3jRDMMkIlo";

    public static void main(String[] args) throws Exception {
        PrintWriter writer = new PrintWriter("d://insert_random_lines.sql", "UTF-8");
        for (int i = 0; i < 1000; i++) {
            double latitude = randLat.nextDouble() * rangeLat + minLat;
            double longitude = randLong.nextDouble() * rangeLong + minLong;

            if (randIdAndAvailability.nextBoolean()) {
                availability = "free";
            } else {
                availability = "reserved";
            }

            String targetURL = GEOCODING_URL + latitude + COMMA_IN_URL + longitude + KEY_STRING + API_KEY;
            String address = geocodeCoords(targetURL);

            newLine = "INSERT INTO parking_lots (gps_time, latitude, longitude, user_id, parking_lot_availability, address)\nVALUES ("
                    + "'" + System.currentTimeMillis() + "','" + latitude + "','" + longitude + "','"
                    + (randIdAndAvailability.nextInt(2000) + 1) + "','" + availability + "','" + address + "');";
            System.out.println(newLine);
            writer.println(newLine);
            Thread.sleep(110);
        }
        writer.close();
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

// CREATE table parking_lots (gps_time LONG, latitude REAL, longitude REAL,
// user_id LONG, parking_lot_availability VARCHAR(10), address VARCHAR(100));
// latitude longitude
// felsõ/alsó bal/jobb
// 47.469458, 19.053066 bal alsó
// 47.473656, 18.968753

// 47.468664, 19.064653 jobb alsó
// 47.473005, 19.178014

// 47.477138, 19.061548 jobb felsõ
// 47.536349, 19.178057

// 47.476721, 19.050733 bal felsõ
// 47.543692, 18.963774
