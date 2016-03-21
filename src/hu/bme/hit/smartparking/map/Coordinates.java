package hu.bme.hit.smartparking.map;

public class Coordinates {

    private double latitude;
    private double longitude;

    public Coordinates(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
