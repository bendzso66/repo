package hu.bme.hit.smartparking.map;

public class ParkingLot extends Coordinates {

    private boolean isFree;

    public ParkingLot(double lat, double lon, boolean isFree) {
        super(lat, lon);
        this.isFree = isFree;
    }

    public boolean getAvailability() {
        return isFree;
    }

}
