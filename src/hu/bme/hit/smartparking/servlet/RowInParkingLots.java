package hu.bme.hit.smartparking.servlet;

public class RowInParkingLots implements Comparable<RowInParkingLots> {

    private int ID;
    private long gpsTime;
    private double latitude;
    private double longitude;
    private long userId;
    private String parkingLotAvailability;
    private String address;
    private Double distance;

    public void setId(int ID) {
        this.ID = ID;
    }

    public int getId() {
        return this.ID;
    }

    public void setGpsTime(long gpsTime) {
        this.gpsTime = gpsTime;
    }

    public long getGpsTime() {
        return this.gpsTime;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setParkingLotAvailability(String parkingLotAvailability) {
        this.parkingLotAvailability = parkingLotAvailability;
    }

    public String getParkingLotAvailability() {
        return this.parkingLotAvailability;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDistance() {
        return this.distance;
    }

    @Override
    public int compareTo(RowInParkingLots other) {
        return Double.compare(this.distance, other.getDistance());
    }

}
