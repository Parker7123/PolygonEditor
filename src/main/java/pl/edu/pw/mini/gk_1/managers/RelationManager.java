package pl.edu.pw.mini.gk_1.managers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import pl.edu.pw.mini.gk_1.helpers.PointsHelper;
import pl.edu.pw.mini.gk_1.relations.LengthRelation;
import pl.edu.pw.mini.gk_1.relations.RelationMode;
import pl.edu.pw.mini.gk_1.relations.RelationsContainer;
import pl.edu.pw.mini.gk_1.shapes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RelationManager extends AbstractManager {
    private final StringProperty lengthProperty;
    private RelationMode relationMode = RelationMode.LENGTH;
    private Optional<PolygonEdgePair> polygonEdgePair = Optional.empty();

    public RelationManager(GraphicsContext graphicsContext, List<Polygon> polygons, StringProperty lengthProperty) {
        super(graphicsContext, polygons);
        this.lengthProperty = lengthProperty;
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        Point2D point = PointsHelper.mouseEventToPoint2D(event);
        polygonEdgePair = firstEdgeCloseEnough(point);
        highlightSelectedEdge();
        updateLengthProperty();
    }


    public void applyLengthRelation(Edge edge, boolean backwards) {
        LengthRelation relation = edge.getLengthRelation().orElseThrow();
        Point2D point1 = edge.getVertex1().getPoint();
        Point2D point2 = edge.getVertex2().getPoint();
        if (backwards) {
            var t = point1;
            point1 = point2;
            point2 = t;
        }
        double edgeLength = edge.length();
        double ratio = relation.getLength() / edgeLength;
        double x = (1 - ratio) * point1.getX() + ratio * point2.getX();
        double y = (1 - ratio) * point1.getY() + ratio * point2.getY();
        if (backwards) {
            edge.getVertex1().setPoint(new Point2D(x, y));
        } else {
            edge.getVertex2().setPoint(new Point2D(x, y));
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        highlightSelectedEdge();
    }

    public void addLengthRelation() {
        polygonEdgePair.ifPresent(pair -> {
            Polygon polygon = pair.getPolygon();
            Edge edge = pair.getEdge();
            edge.getLengthRelation().ifPresentOrElse(relation -> {
                relation.setLength(getRequiredLength());
            }, () -> {
                edge.setLengthRelation(new LengthRelation(getRequiredLength()));
            });
            applyRelationsStartingAtVertex(new PolygonVertexPair(polygon, edge.getVertex1()));
        });
    }

    public void applyRelationsStartingAtVertex(PolygonVertexPair polygonVertexPair) {
        Polygon polygon = polygonVertexPair.getPolygon();
        Vertex vertex = polygonVertexPair.getVertex();
        EdgesList edges = polygon.getEdges();

        Stream.concat(
                        polygon.getEdges().stream()
                                .dropWhile(edge -> !edge.getVertex1().equals(vertex)),
                        polygon.getEdges().stream().takeWhile(edge -> !edge.getVertex1().equals(vertex))
                )
                .takeWhile(edge -> edge.getLengthRelation().isPresent())
                .forEach(edge -> applyLengthRelation(edge, false));

        int num = edges.size() - 1;
        Stream.concat(
                        IntStream.rangeClosed(0, num)
                                .mapToObj(i -> edges.get(num - i))
                                .dropWhile(edge -> !edge.getVertex2().equals(vertex)),
                        IntStream.rangeClosed(0, num)
                                .mapToObj(i -> edges.get(num - i))
                                .takeWhile(edge -> !edge.getVertex2().equals(vertex))
                )
                .takeWhile(edge -> edge.getLengthRelation().isPresent())
                .forEach(edge -> applyLengthRelation(edge, true));
    }

    private void updateLengthProperty() {
        polygonEdgePair.ifPresentOrElse(pair -> {
            pair.getPolygon().getRelationsContainer().getLengthRelationForEdge(pair.getEdge()).ifPresentOrElse(relation -> {
                lengthProperty.set(String.valueOf(relation.getLength()));
            }, () -> lengthProperty.set(
                    BigDecimal.valueOf(pair.getEdge().length()).setScale(2, RoundingMode.HALF_UP).toPlainString()));
        }, () -> lengthProperty.set(""));
    }

    public void resetRelationMode() {
        polygonEdgePair = Optional.empty();
        updateLengthProperty();
    }

    public void highlightSelectedEdge() {
        polygonEdgePair.ifPresent(pair -> Polygon.highlightEdge(graphicsContext, pair.getEdge(), drawingMode));
    }

    private double getRequiredLength() {
        return Double.parseDouble(lengthProperty.get());
    }

    public void setRelationMode(RelationMode relationMode) {
        this.relationMode = relationMode;
    }
}
