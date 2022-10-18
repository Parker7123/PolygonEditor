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
import java.util.stream.Collectors;

public class Polygon extends AbstractPolygon implements Movable, Cloneable {

    //    private final List<Relation> relations = new ArrayList<>();
    private final EdgesList edges;
    private final RelationsContainer relationsContainer = new RelationsContainer();

    public Polygon(VerticesList vertices) {
        this.vertices.addAll(vertices);
        this.edges = new EdgesList(vertices);
    }

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        edges.forEach(edge -> edge.draw(graphicsContext, drawingMode));
    }

    public void deleteVertex(Vertex vertex) {
        vertices.remove(vertex);
        int vIndex = vertices.indexOf(vertex);
        edges.removeAndRepair(vertex);

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

    public void addVertex(int position, Vertex vertex) {
        edges.addVertex(vertices.get(position - 1), vertex);
        vertices.add(position, vertex);
    }

    @Override
    public void move(Point2D vector) {
        vertices.forEach(vertex -> vertex.move(vector));
    }

    public RelationsContainer getRelationsContainer() {
        return relationsContainer;
    }

    public EdgesList getEdges() {
        return edges;
    }
}
