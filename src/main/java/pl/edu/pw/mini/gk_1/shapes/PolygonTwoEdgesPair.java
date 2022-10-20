package pl.edu.pw.mini.gk_1.shapes;

public class PolygonTwoEdgesPair {
    private final Polygon polygon;
    private Edge edge1;
    private Edge edge2;

    public PolygonTwoEdgesPair(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Edge getEdge1() {
        return edge1;
    }

    public void setEdge1(Edge edge1) {
        this.edge1 = edge1;
    }

    public Edge getEdge2() {
        return edge2;
    }

    public void setEdge2(Edge edge2) {
        this.edge2 = edge2;
    }

    public static PolygonTwoEdgesPair empty() {
        return new PolygonTwoEdgesPair(new Polygon(new VerticesList()));
    }
}
