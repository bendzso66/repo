package hu.bme.hit.smartparking.servlet;

import java.util.ArrayList;
import java.util.List;

public class RowInWays {

    private int wayId;
    private String nameOfWay;
    private double latitude1;
    private double longitude1;
    private double latitude2;
    private double longitude2;
    private int allSpaces;
    private int freeSpaces;
    private Double distance;
    private List<Double> latitudes = new ArrayList<Double>();
    private List<Double> longitudes = new ArrayList<Double>();

    public RowInWays(int wayId,
            String nameOfWay,
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2,
            int allSpaces,
            int freeSpaces,
            Double distance,
            List<Double> latitudes,
            List<Double> longitudes) {
        this.wayId = wayId;
        this.nameOfWay = nameOfWay;
        this.latitude1 = latitude1;
        this.longitude1 = longitude1;
        this.latitude2 = latitude2;
        this.longitude2 = longitude2;
        this.allSpaces = allSpaces;
        this.freeSpaces = freeSpaces;
        this.distance = distance;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public int getId() {
        return this.wayId;
    }

    public String getNameOfWay() {
        return this.nameOfWay;
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

    public List<Double> getLatitudes() {
        return this.latitudes;
    }

    public List<Double> getLongitudes() {
        return this.longitudes;
    }

}
