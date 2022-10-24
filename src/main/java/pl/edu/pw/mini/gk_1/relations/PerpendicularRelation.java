package pl.edu.pw.mini.gk_1.relations;

import pl.edu.pw.mini.gk_1.shapes.Edge;
import pl.edu.pw.mini.gk_1.shapes.Polygon;
import pl.edu.pw.mini.gk_1.shapes.PolygonEdgePair;

import java.util.concurrent.atomic.AtomicBoolean;

public class PerpendicularRelation {
    private final Edge edgeInRelation;
    private final Polygon polygonInRelation;
    private final AtomicBoolean tested = new AtomicBoolean();

    public PerpendicularRelation(PolygonEdgePair polygonEdgePair) {
        this.edgeInRelation = polygonEdgePair.getEdge();
        this.polygonInRelation = polygonEdgePair.getPolygon();
    }

    public Edge getEdgeInRelation() {
        return edgeInRelation;
    }

    public Polygon getPolygonInRelation() {
        return polygonInRelation;
    }

    public AtomicBoolean getTested() {
        return tested;
    }
}
