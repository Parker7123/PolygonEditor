package pl.edu.pw.mini.gk_1.shapes;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import pl.edu.pw.mini.gk_1.helpers.DrawingHelper;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.interfaces.Movable;
import pl.edu.pw.mini.gk_1.relations.Relation;
import pl.edu.pw.mini.gk_1.relations.RelationsContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Polygon extends AbstractPolygon implements Movable {

    //    private final List<Relation> relations = new ArrayList<>();
    private final RelationsContainer relationsContainer = new RelationsContainer();

    public Polygon() {
    }

    public Polygon(ArrayList<Vertex> vertices) {
        this.vertices.addAll(vertices);
    }

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        super.draw(graphicsContext, drawingMode);
        drawWithModifier(graphicsContext, drawingMode, edge -> {
            relationsContainer.getLengthRelationForEdge(edge).ifPresent(relation -> {
                relation.draw(graphicsContext, drawingMode);
            });
        });
        DrawingHelper.drawLineBetweenTwoPoints(
                graphicsContext, vertices.getLast().getPoint(), vertices.getFirst().getPoint(), drawingMode);
        relationsContainer.getLengthRelationForEdge(new Edge(vertices.getLast(), vertices.getFirst()))
                .ifPresent(relation -> relation.draw(graphicsContext, drawingMode));

    }

    public void deleteVertex(Vertex vertex) {
        vertices.remove(vertex);
        int vIndex = vertices.indexOf(vertex);
        if(vIndex > 0) {
            relationsContainer.removeLengthRelationFromEdge(new Edge(vertices.get(vIndex - 1), vertex));
        } else {
            relationsContainer.removeLengthRelationFromEdge(new Edge(vertex, vertices.getLast()));
        }
        relationsContainer.removeLengthRelationFromEdge(new Edge(vertices.get((vIndex + 1) & vertices.size()), vertex));
    }

    public boolean isPointInside(Point2D point) {
        var topMostVertex = vertices.stream().min(Comparator.comparingDouble(Vertex::getY)).orElseThrow();
        var bottomMostVertex = vertices.stream().max(Comparator.comparingDouble(Vertex::getY)).orElseThrow();
        var leftMostVertex = vertices.stream().min(Comparator.comparingDouble(Vertex::getX)).orElseThrow();
        var rightMostVertex = vertices.stream().max(Comparator.comparingDouble(Vertex::getX)).orElseThrow();

        double w = rightMostVertex.getX() - leftMostVertex.getX();
        double h = bottomMostVertex.getY() - topMostVertex.getY();
        var rectangle = new Rectangle2D(leftMostVertex.getPoint().getX(), topMostVertex.getPoint().getY(), w, h);

        return rectangle.contains(point);
    }

    @Override
    public void move(Point2D vector) {
        vertices.forEach(vertex -> vertex.move(vector));
    }

    public RelationsContainer getRelationsContainer() {
        return relationsContainer;
    }

//    public List<Relation> getRelations() {
//        return relations;
//    }
}
