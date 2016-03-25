package hu.bme.hit.smartparking.map;

public class Node extends Coordinates {

    private long nodeId;

    public Node(long id, double lat, double lon) {
        super(lat, lon);
        nodeId = id;
    }

    public long getNodeId() {
        return nodeId;
    }

}
