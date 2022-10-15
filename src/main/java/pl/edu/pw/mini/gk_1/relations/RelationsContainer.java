package pl.edu.pw.mini.gk_1.relations;

import pl.edu.pw.mini.gk_1.shapes.Edge;
import pl.edu.pw.mini.gk_1.shapes.Vertex;

import java.util.*;

public class RelationsContainer {
    private final Map<Edge, LengthRelation> lengthRelationMap = new HashMap<>();
    private final Set<Relation> relationSet = new HashSet<>();

    public void addLengthRelation(LengthRelation relation) {
//        relationSet.add(relation);
        lengthRelationMap.put(relation.getEdge(), relation);
    }

    public void removeLengthRelationFromEdge(Edge edge) {
        lengthRelationMap.remove(edge);
    }

    public Optional<LengthRelation> getLengthRelationForEdge(Edge edge) {
        return Optional.ofNullable(lengthRelationMap.get(edge));
    }

    public boolean doesEdgeHaveLengthRelation(Edge edge) {
        return lengthRelationMap.get(edge) != null;
    }
}
