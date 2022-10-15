package pl.edu.pw.mini.gk_1.relations;

import pl.edu.pw.mini.gk_1.shapes.Edge;

import java.util.Objects;

public abstract class Relation {
    private final Edge edge;

    public Relation(Edge edge) {
        this.edge = edge;
    }

    public Edge getEdge() {
        return edge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;
        Relation relation = (Relation) o;
        return Objects.equals(edge, relation.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge);
    }
}
