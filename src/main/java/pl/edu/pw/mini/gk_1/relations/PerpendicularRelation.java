package pl.edu.pw.mini.gk_1.relations;

import pl.edu.pw.mini.gk_1.shapes.Edge;

public class PerpendicularRelation {
    private Edge edgeInRelation;

    public PerpendicularRelation(Edge edgeInRelation) {
        this.edgeInRelation = edgeInRelation;
    }

    public Edge getEdgeInRelation() {
        return edgeInRelation;
    }

    public void setEdgeInRelation(Edge edgeInRelation) {
        this.edgeInRelation = edgeInRelation;
    }
}
