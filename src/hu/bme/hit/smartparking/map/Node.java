package hu.bme.hit.smartparking.map;

public class Node {

    private long nodeId;
    private Coordinates coordinates;

    public Node(long id, Coordinates coords) {
        nodeId = id;
        coordinates = coords;
    }

    public long getNodeId() {
        return nodeId;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

}
