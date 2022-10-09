package pl.edu.pw.mini.gk_1.shapes;

public class PolygonVertexPair {
    private final Polygon polygon;
    private final Vertex vertex;

    public PolygonVertexPair(Polygon polygon, Vertex vertex) {
        this.polygon = polygon;
        this.vertex = vertex;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public Vertex getVertex() {
        return vertex;
    }
}
