package pl.edu.pw.mini.gk_1.managers;

import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.PointsHelper;
import pl.edu.pw.mini.gk_1.relations.LengthRelation;
import pl.edu.pw.mini.gk_1.relations.PerpendicularRelation;
import pl.edu.pw.mini.gk_1.relations.RelationMode;
import pl.edu.pw.mini.gk_1.shapes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RelationManager extends AbstractManager {
    private final StringProperty lengthProperty;
    private RelationMode relationMode = RelationMode.LENGTH;
    private PolygonEdgePair polygonEdgePair1 = null;
    private PolygonEdgePair polygonEdgePair2 = null;

    public RelationManager(GraphicsContext graphicsContext, List<Polygon> polygons, StringProperty lengthProperty, List<Oval> ovals) {
        super(graphicsContext, polygons, ovals);
        this.lengthProperty = lengthProperty;
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        Point2D point = PointsHelper.mouseEventToPoint2D(event);
        Optional<PolygonEdgePair> polygonWithCloseEdge = firstEdgeCloseEnough(point);
        switch (relationMode) {
            case LENGTH:
                polygonWithCloseEdge.ifPresent(pair -> polygonEdgePair1 = pair);
                highlightSelectedEdges();
                updateLengthProperty();
                break;
            case PERPENDICULAR:
                polygonWithCloseEdge.ifPresent(this::updatePolygonTwoEdgesPair);
                highlightSelectedEdges();
                break;
        }
    }

    private void updatePolygonTwoEdgesPair(PolygonEdgePair polygonEdgePair) {
        if (polygonEdgePair1 == null) {
            polygonEdgePair1 = polygonEdgePair;
        } else if (polygonEdgePair2 == null) {
            polygonEdgePair2 = polygonEdgePair;
        } else if (!polygonEdgePair1.getEdge().equals(polygonEdgePair.getEdge()) &&
                !polygonEdgePair2.getEdge().equals(polygonEdgePair.getEdge())) {
            polygonEdgePair1 = polygonEdgePair2;
            polygonEdgePair2 = polygonEdgePair;
        }
    }

    private void applyLengthRelation(Edge edge, boolean backwards) {
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

    private void applyPerpendicularRelation(PolygonEdgePair polygonEdgePair, boolean backwards) {
        Edge edge = polygonEdgePair.getEdge();
        Polygon polygon = polygonEdgePair.getPolygon();
        PerpendicularRelation relation = edge.getPerpendicularRelation().orElseThrow();
        Edge edgeInRelation = relation.getEdgeInRelation();
        Polygon polygonInRelation = relation.getPolygonInRelation();
        Point2D point1 = edge.getVertex1().getPoint();
        Point2D point2 = edge.getVertex2().getPoint();
        Point2D otherPoint1 = edgeInRelation.getVertex1().getPoint();
        Point2D otherPoint2 = edgeInRelation.getVertex2().getPoint();
        Point2D mainVector = point2.subtract(point1);
        Point2D otherVector = otherPoint2.subtract(otherPoint1);

        if (point2.equals(otherPoint1)) {
            Point2D perpendicularVector = PointsHelper.closestPerpendicularVector(mainVector, otherVector);
            edgeInRelation.getVertex2().setPoint(perpendicularVector.multiply(otherVector.magnitude() / mainVector.magnitude()).add(otherPoint1));
        } else if (point1.equals(otherPoint2)) {
            if (backwards) {
                Point2D perpendicularVector = PointsHelper.closestPerpendicularVector(mainVector, otherVector.multiply(-1));
                edgeInRelation.getVertex1().setPoint(perpendicularVector.multiply(otherVector.magnitude() / mainVector.magnitude()).add(point1));
            } else {
                Point2D perpendicularVector = PointsHelper.closestPerpendicularVector(otherVector, mainVector);
                edge.getVertex2().setPoint(perpendicularVector.multiply(mainVector.magnitude() / otherVector.magnitude()).add(otherPoint2));
            }
        } else {
            if (backwards) {
                Point2D perpendicularVector = PointsHelper.closestPerpendicularVector(otherVector.multiply(-1), mainVector.multiply(-1));
                edge.getVertex1().setPoint(perpendicularVector.multiply(mainVector.magnitude() / otherVector.magnitude()).add(point2));
            } else {
                Point2D perpendicularVector = PointsHelper.closestPerpendicularVector(otherVector, mainVector);
                edge.getVertex2().setPoint(perpendicularVector.multiply(mainVector.magnitude() / otherVector.magnitude()).add(point1));
            }
            if (polygon.getId() != polygonInRelation.getId() && relation.getTested().compareAndSet(false, true)) {
                applyPerpendicularRelation(new PolygonEdgePair(polygonInRelation, edgeInRelation), false);
            }
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        highlightSelectedEdges();
    }

    public void addLengthRelation() {
        if (polygonEdgePair1 != null) {
            Polygon polygon = polygonEdgePair1.getPolygon();
            Edge edge = polygonEdgePair1.getEdge();
            edge.getLengthRelation().ifPresentOrElse(relation -> relation.setLength(getRequiredLength()), () -> edge.setLengthRelation(new LengthRelation(getRequiredLength())));
            applyRelationsStartingAtVertex(new PolygonVertexPair(polygon, edge.getVertex1()));
        }
    }

    public void addPerpendicularRelation() {
        if (polygonEdgePair1.getEdge() == null || polygonEdgePair2.getEdge() == null) {
            return;
        }
        polygonEdgePair1.getEdge().setPerpendicularRelation(new PerpendicularRelation(polygonEdgePair2));
        polygonEdgePair2.getEdge().setPerpendicularRelation(new PerpendicularRelation(polygonEdgePair1));
        applyRelationsStartingAtVertex(new PolygonVertexPair(polygonEdgePair1.getPolygon(), polygonEdgePair1.getEdge().getVertex1()));
//        applyRelationsStartingAtVertex(new PolygonVertexPair(polygonTwoEdgesPair.getPolygon(), polygonTwoEdgesPair.getEdge2().getVertex1()));
    }

    public void applyRelationsStartingAtVertex(PolygonVertexPair polygonVertexPair) {
        Polygon polygon = polygonVertexPair.getPolygon();
        Vertex vertex = polygonVertexPair.getVertex();
        EdgesList edges = polygon.getEdges();

        Stream.concat(polygon.getEdges().stream().dropWhile(edge -> !edge.getVertex1().equals(vertex)),
                        polygon.getEdges().stream().takeWhile(edge -> !edge.getVertex1().equals(vertex)))
                .takeWhile(edge -> edge.getLengthRelation().isPresent() || edge.getPerpendicularRelation().isPresent())
                .forEach(edge -> {
                    edge.getPerpendicularRelation().ifPresent(perpendicularRelation -> applyPerpendicularRelation(new PolygonEdgePair(polygon, edge), false));
                    edge.getLengthRelation().ifPresent(lengthRelation -> applyLengthRelation(edge, false));
                });

        int num = edges.size() - 1;
        Stream.concat(
                        IntStream.rangeClosed(0, num)
                                .mapToObj(i -> edges.get(num - i))
                                .dropWhile(edge -> !edge.getVertex2().equals(vertex)),
                        IntStream.rangeClosed(0, num)
                                .mapToObj(i -> edges.get(num - i))
                                .takeWhile(edge -> !edge.getVertex2().equals(vertex))
                )
                .takeWhile(edge -> edge.getLengthRelation().isPresent() || edge.getPerpendicularRelation().isPresent())
                .forEach(edge -> {
                    edge.getPerpendicularRelation().ifPresent(perpendicularRelation -> applyPerpendicularRelation(new PolygonEdgePair(polygon, edge), true));
                    edge.getLengthRelation().ifPresent(lengthRelation ->
                            applyLengthRelation(edge, true));
                });
    }

    private void updateLengthProperty() {
        if (polygonEdgePair1 != null) {
            var roundedLength = BigDecimal.valueOf(polygonEdgePair1.getEdge().length())
                    .setScale(2, RoundingMode.HALF_UP);
            var propertyValue = String.valueOf(roundedLength);
            lengthProperty.set(propertyValue);

        } else {
            lengthProperty.set("");
        }
    }

    public void resetRelationMode() {
        polygonEdgePair1 = null;
        polygonEdgePair2 = null;
        updateLengthProperty();
    }

    public void highlightSelectedEdges() {
        switch (relationMode) {
            case LENGTH:
                if (polygonEdgePair1 != null) {
                    Polygon.highlightEdge(graphicsContext, polygonEdgePair1.getEdge(), drawingMode);
                }
                break;
            case PERPENDICULAR:
                if (polygonEdgePair1 != null) {
                    Polygon.highlightEdge(graphicsContext, polygonEdgePair1.getEdge(), drawingMode);
                }
                if (polygonEdgePair2 != null) {
                    Polygon.highlightEdge(graphicsContext, polygonEdgePair2.getEdge(), drawingMode);
                }
                break;
        }
    }

    private double getRequiredLength() {
        return Double.parseDouble(lengthProperty.get());
    }

    public void setRelationMode(RelationMode relationMode) {
        this.relationMode = relationMode;
    }

    public void removePerpendicularRelation() {
        if (polygonEdgePair1.getEdge() != null) {
            polygonEdgePair1.getEdge().removePerpendicularRelation();
        }
        if (polygonEdgePair2.getEdge() != null) {
            polygonEdgePair2.getEdge().removePerpendicularRelation();
        }
    }

    public void removeLengthRelation() {
        if (polygonEdgePair1 != null) {
            polygonEdgePair1.getEdge().removeLengthRelation();
        }
    }
}
