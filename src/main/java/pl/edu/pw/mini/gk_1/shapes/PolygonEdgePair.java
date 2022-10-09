package pl.edu.pw.mini.gk_1.shapes;

public class PolygonEdgePair {
    private final Polygon polygon;
    private final Edge edge;

    public PolygonEdgePair(Polygon polygon, Edge edge) {
        this.polygon = polygon;
        this.edge = edge;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Edge getEdge() {
        return edge;
    }
}
