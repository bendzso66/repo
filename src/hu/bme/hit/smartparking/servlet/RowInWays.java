package hu.bme.hit.smartparking.servlet;

public class RowInWays {

    private int wayId;
    private String nameOfWay;
    private double centerLatitude;
    private double centerLongitude;
    private double latitude1;
    private double longitude1;
    private double latitude2;
    private double longitude2;
    private int allSpaces;
    private int freeSpaces;
    private Double distance;

    public RowInWays(int wayId,
            String nameOfWay,
            double centerLatitude,
            double centerLongitude,
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2,
            int allSpaces,
            int freeSpaces,
            Double distance) {
        this.wayId = wayId;
        this.nameOfWay = nameOfWay;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.latitude1 = latitude1;
        this.longitude1 = longitude1;
        this.latitude2 = latitude2;
        this.longitude2 = longitude2;
        this.allSpaces = allSpaces;
        this.freeSpaces = freeSpaces;
        this.distance = distance;
    }

    public int getId() {
        return this.wayId;
    }

    public String getNameOfWay() {
        return this.nameOfWay;
    }

    public double getCenterLatitude() {
        return this.centerLatitude;
    }

    public double getCenterLongitude() {
        return this.centerLongitude;
    }

    public double getLatitude1() {
        return this.latitude1;
    }

    public double getLongitude1() {
        return this.longitude1;
    }

    public double getLatitude2() {
        return this.latitude2;
    }

    public double getLongitude2() {
        return this.longitude2;
    }

    public int getAllSpaces() {
        return this.allSpaces;
    }

    public int getFreeSpaces() {
        return this.freeSpaces;
    }

    public Double getDistance() {
        return this.distance;
    }

}
