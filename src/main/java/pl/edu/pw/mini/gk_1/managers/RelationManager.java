package pl.edu.pw.mini.gk_1.managers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import pl.edu.pw.mini.gk_1.helpers.PointsHelper;
import pl.edu.pw.mini.gk_1.relations.LengthRelation;
import pl.edu.pw.mini.gk_1.relations.PerpendicularRelation;
import pl.edu.pw.mini.gk_1.relations.RelationMode;
import pl.edu.pw.mini.gk_1.relations.RelationsContainer;
import pl.edu.pw.mini.gk_1.shapes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RelationManager extends AbstractManager {
    private final StringProperty lengthProperty;
    private RelationMode relationMode = RelationMode.LENGTH;
    private Optional<PolygonEdgePair> polygonEdgePair = Optional.empty();
    private PolygonTwoEdgesPair polygonTwoEdgesPair = PolygonTwoEdgesPair.empty();

    public RelationManager(GraphicsContext graphicsContext, List<Polygon> polygons, StringProperty lengthProperty) {
        super(graphicsContext, polygons);
        this.lengthProperty = lengthProperty;
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        Point2D point = PointsHelper.mouseEventToPoint2D(event);
        Optional<PolygonEdgePair> polygonWithCloseEdge = firstEdgeCloseEnough(point);
        switch (relationMode) {
            case LENGTH:
                polygonEdgePair = polygonWithCloseEdge;
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
        Edge selectedEdge = polygonEdgePair.getEdge();
        if (!polygonTwoEdgesPair.getPolygon().equals(polygonEdgePair.getPolygon())) {
            polygonTwoEdgesPair = new PolygonTwoEdgesPair(polygonEdgePair.getPolygon());
        }
        if (polygonTwoEdgesPair.getEdge1() == null) {
            polygonTwoEdgesPair.setEdge1(selectedEdge);
        } else if (polygonTwoEdgesPair.getEdge2() == null && !polygonTwoEdgesPair.getEdge1().equals(selectedEdge)) {
            polygonTwoEdgesPair.setEdge2(selectedEdge);
        } else if (!polygonTwoEdgesPair.getEdge2().equals(selectedEdge)) {
            polygonTwoEdgesPair.setEdge1(polygonTwoEdgesPair.getEdge2());
            polygonTwoEdgesPair.setEdge2(selectedEdge);
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

    private void applyPerpendicularRelation(Edge edge, boolean backwards) {
        PerpendicularRelation relation = edge.getPerpendicularRelation().orElseThrow();
        Edge edgeInRelation = relation.getEdgeInRelation();
        Point2D point1 = edge.getVertex1().getPoint();
        Point2D point2 = edge.getVertex2().getPoint();
        Point2D otherPoint1 = edgeInRelation.getVertex1().getPoint();
        Point2D otherPoint2 = edgeInRelation.getVertex2().getPoint();

        if (point2.equals(otherPoint1)) {
            Point2D perpendicularVector = PointsHelper.perpendicularVector(point2.subtract(point1));
            edgeInRelation.getVertex2().setPoint(perpendicularVector.multiply(otherPoint2.distance(otherPoint1)/point2.distance(point1)).add(otherPoint1));
        } else if (point1.equals(otherPoint2)) {
            if(backwards) {
                Point2D perpendicularVector = PointsHelper.perpendicularVector(point2.subtract(point1));
                edgeInRelation.getVertex1().setPoint(perpendicularVector.multiply(otherPoint2.distance(otherPoint1)/point2.distance(point1)).add(point1));
            } else {
                Point2D perpendicularVector = PointsHelper.perpendicularVector(otherPoint2.subtract(otherPoint1));
                edge.getVertex2().setPoint(perpendicularVector.multiply(point2.distance(point1)/otherPoint2.distance(otherPoint1)).add(otherPoint2));
            }
        } else {
            if (backwards) {
                Point2D perpendicularVector = PointsHelper.perpendicularVector(point2.subtract(point1));
                edgeInRelation.getVertex1().setPoint(perpendicularVector.multiply(otherPoint2.distance(otherPoint1)/point2.distance(point1)).add(otherPoint2));
            } else {
                Point2D perpendicularVector = PointsHelper.perpendicularVector(point2.subtract(point1));
                edgeInRelation.getVertex2().setPoint(perpendicularVector.multiply(otherPoint2.distance(otherPoint1)/point2.distance(point1)).add(otherPoint1));

            }
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        highlightSelectedEdges();
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

    public void addPerpendicularRelation() {
        if (polygonTwoEdgesPair.getEdge1() == null || polygonTwoEdgesPair.getEdge2() == null) {
            return;
        }
        polygonTwoEdgesPair.getEdge1().setPerpendicularRelation(new PerpendicularRelation(polygonTwoEdgesPair.getEdge2()));
        polygonTwoEdgesPair.getEdge2().setPerpendicularRelation(new PerpendicularRelation(polygonTwoEdgesPair.getEdge1()));
        applyRelationsStartingAtVertex(new PolygonVertexPair(polygonTwoEdgesPair.getPolygon(), polygonTwoEdgesPair.getEdge1().getVertex1()));
        applyRelationsStartingAtVertex(new PolygonVertexPair(polygonTwoEdgesPair.getPolygon(), polygonTwoEdgesPair.getEdge2().getVertex1()));
    }

    public void removeLengthRelation() {
        polygonEdgePair.ifPresent(pair -> pair.getEdge().removeLengthRelation());
    }

    public void applyRelationsStartingAtVertex(PolygonVertexPair polygonVertexPair) {
        Polygon polygon = polygonVertexPair.getPolygon();
        Vertex vertex = polygonVertexPair.getVertex();
        EdgesList edges = polygon.getEdges();

        Set<Edge> perpRelationsApplied = new HashSet<>();
        Stream.concat(polygon.getEdges().stream().dropWhile(edge -> !edge.getVertex1().equals(vertex)),
                        polygon.getEdges().stream().takeWhile(edge -> !edge.getVertex1().equals(vertex)))
                .takeWhile(edge -> edge.getLengthRelation().isPresent() || edge.getPerpendicularRelation().isPresent())
                .forEach(edge -> {
                    if (!perpRelationsApplied.contains(edge) || true) {
                        edge.getPerpendicularRelation().ifPresent(perpendicularRelation -> {
                            applyPerpendicularRelation(edge, false);
                            perpRelationsApplied.add(edge);
                            perpRelationsApplied.add(perpendicularRelation.getEdgeInRelation());
                        });
                    }
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
                    if (!perpRelationsApplied.contains(edge) || true) {
                        edge.getPerpendicularRelation().ifPresent(perpendicularRelation -> {
                            applyPerpendicularRelation(edge, true);
                            perpRelationsApplied.add(edge);
                            perpRelationsApplied.add(perpendicularRelation.getEdgeInRelation());
                        });
                    }
                    edge.getLengthRelation().ifPresent(lengthRelation -> applyLengthRelation(edge, true));
                });
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
        polygonTwoEdgesPair = PolygonTwoEdgesPair.empty();
        updateLengthProperty();
    }

    public void highlightSelectedEdges() {
        switch (relationMode) {
            case LENGTH:
                polygonEdgePair.ifPresent(pair -> Polygon.highlightEdge(graphicsContext, pair.getEdge(), drawingMode));
                break;
            case PERPENDICULAR:
                if (polygonTwoEdgesPair.getEdge1() != null) {
                    Polygon.highlightEdge(graphicsContext, polygonTwoEdgesPair.getEdge1(), drawingMode);
                }
                if (polygonTwoEdgesPair.getEdge2() != null) {
                    Polygon.highlightEdge(graphicsContext, polygonTwoEdgesPair.getEdge2(), drawingMode);
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
}
