package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.PointsHelper;
import pl.edu.pw.mini.gk_1.shapes.Polygon;
import pl.edu.pw.mini.gk_1.shapes.PolygonEdgePair;
import pl.edu.pw.mini.gk_1.shapes.PolygonVertexPair;

import java.util.List;

import static pl.edu.pw.mini.gk_1.managers.ManipulationManager.ManipulationType.*;

public class ManipulationManager extends AbstractManager {

    private PolygonVertexPair polygonVertexPair;
    private PolygonEdgePair polygonEdgePair;
    private ManipulationType manipulationType;
    private final RelationManager relationManager;
    private Point2D prevDragPoint;

    public ManipulationManager(GraphicsContext graphicsContext, List<Polygon> polygons, RelationManager relationManager) {
        super(graphicsContext, polygons);
        this.relationManager = relationManager;
    }

    @Override
    public void onMouseClick(MouseEvent event) {

    }

    @Override
    public void onMouseMoved(MouseEvent event) {

    }

    private void moveVertex(Point2D point) {
        if(polygonVertexPair == null) {
            return;
        }
        var vector = point.subtract(prevDragPoint);
        polygonVertexPair.getVertex().move(vector);
        prevDragPoint = point;
        relationManager.applyRelationsStartingAtVertex(polygonVertexPair);
    }

    private void moveEdge(Point2D point) {
        if(polygonEdgePair == null) {
            return;
        }
        var vector = point.subtract(prevDragPoint);
        var edge = polygonEdgePair.getEdge();
        edge.getVertex1().move(vector);
        relationManager.applyRelationsStartingAtVertex(new PolygonVertexPair(polygonEdgePair.getPolygon(), edge.getVertex1()));
        edge.getVertex2().move(vector);
        relationManager.applyRelationsStartingAtVertex(new PolygonVertexPair(polygonEdgePair.getPolygon(), edge.getVertex2()));
        prevDragPoint = point;
    }

    private void movePolygon(Point2D point) {
        if(polygonVertexPair == null) {
            return;
        }
        var vector = point.subtract(prevDragPoint);
        polygonVertexPair.getPolygon().move(vector);
        prevDragPoint = point;
    }

    public void onMouseDragged(MouseEvent event) {
        var point = PointsHelper.mouseEventToPoint2D(event);
        if (manipulationType == null) manipulationType = POLYGON;
        switch (manipulationType) {
            case VERTEX:
                moveVertex(point);
                break;
            case POLYGON:
                movePolygon(point);
                break;
            case EDGE:
                moveEdge(point);
        }
    }

    public void onMousePressed(MouseEvent event) {
        var point = PointsHelper.mouseEventToPoint2D(event);
        prevDragPoint = PointsHelper.mouseEventToPoint2D(event);
        var selectedVertex = firstVertexCloseEnough(point);
        if(selectedVertex.isPresent()) {
            polygonVertexPair = selectedVertex.get();
            manipulationType = VERTEX;
            return;
        }
        var selectedEdge = firstEdgeCloseEnough(point);
        if (selectedEdge.isPresent()) {
            polygonEdgePair = selectedEdge.get();
            manipulationType = EDGE;
            return;
        }
        var selectedPolygon = polygons.stream()
                .filter(polygon -> polygon.isPointInside(point))
                .findFirst();
        if (selectedPolygon.isPresent()) {
            polygonVertexPair = new PolygonVertexPair(selectedPolygon.get(), null);
            manipulationType = POLYGON;
            return;
        }
        polygonVertexPair = null;
        polygonEdgePair = null;
    }

    enum ManipulationType {
        VERTEX, POLYGON, EDGE
    }
}
