package pl.edu.pw.mini.gk_1.managers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.PointsHelper;
import pl.edu.pw.mini.gk_1.relations.LengthRelation;
import pl.edu.pw.mini.gk_1.relations.RelationMode;
import pl.edu.pw.mini.gk_1.relations.RelationsContainer;
import pl.edu.pw.mini.gk_1.shapes.Edge;
import pl.edu.pw.mini.gk_1.shapes.Polygon;
import pl.edu.pw.mini.gk_1.shapes.PolygonEdgePair;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    @Override
    public void onMouseMoved(MouseEvent event) {
       highlightSelectedEdge();
    }

    public void applyLengthRelation(LengthRelation relation) {
        double length = relation.getLength();
        Edge edge = relation.getEdge();
        Point2D point1 = edge.getVertex1().getPoint();
        Point2D point2 = edge.getVertex2().getPoint();
        double edgeLength = point1.distance(point2);
        double ratio = length / edgeLength;
        double x = (1 - ratio) * point1.getX() + ratio * point2.getX();
        double y = (1 - ratio) * point1.getY() + ratio * point2.getY();
        edge.getVertex2().setPoint(new Point2D(x, y));
//        Point2D midPoint = point2.midpoint(point1);
//        Point2D newPoint2 = new Point2D(midPoint.getX() + (Math.abs((point2.getX() - midPoint.getX())) * length / edgeLength),
//                midPoint.getY() + (Math.abs((point2.getY() - midPoint.getY())) * length / edgeLength));
//        Point2D newPoint1 = new Point2D(midPoint.getX() - (Math.abs((point1.getX() - midPoint.getX())) * length / edgeLength),
//                midPoint.getY() - ((Math.abs(point1.getY() - midPoint.getY())) * length / edgeLength));
//        edge.getVertex1().setPoint(newPoint1);
//        edge.getVertex2().setPoint(newPoint2);
    }

    public void addLengthRelation() {
        long time = Instant.now().toEpochMilli();
        polygonEdgePair.ifPresent(pair -> {
            Polygon polygon = pair.getPolygon();
            Edge edge = pair.getEdge();
            RelationsContainer relationsContainer = polygon.getRelationsContainer();
            relationsContainer.getLengthRelationForEdge(edge).ifPresentOrElse(relation -> {
                relation.setLength(getRequiredLength());
            }, () -> {
                relationsContainer.addLengthRelation(new LengthRelation(edge, getRequiredLength()));
            });
            applyLengthRelation(relationsContainer.getLengthRelationForEdge(edge).get());
        });
    }

    private void updateLengthProperty() {
        polygonEdgePair.ifPresentOrElse(pair -> {
            pair.getPolygon().getRelationsContainer().getLengthRelationForEdge(pair.getEdge()).ifPresentOrElse(relation -> {
                lengthProperty.set(String.valueOf(relation.getLength()));
            }, () -> lengthProperty.set(""));
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
