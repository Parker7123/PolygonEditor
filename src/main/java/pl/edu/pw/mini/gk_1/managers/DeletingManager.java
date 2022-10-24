package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.shapes.Polygon;
import pl.edu.pw.mini.gk_1.shapes.Vertex;

import java.util.List;
import java.util.Optional;

import static pl.edu.pw.mini.gk_1.helpers.PointsHelper.mouseEventToPoint2D;

public class DeletingManager extends AbstractManager {

    public DeletingManager(GraphicsContext graphicsContext, List<Polygon> polygons) {
        super(graphicsContext, polygons);
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        var point = mouseEventToPoint2D(event);
        var deletedVertex = deleteNearestVertex(point);
        if(deletedVertex.isPresent()) {
            return;
        }
        addVertexOnEdge(point);
    }

    @Override
    public void onMouseMoved(MouseEvent event) {

    }

    private Optional<Vertex> deleteNearestVertex(Point2D point) {
        var polygonVertexPair = firstVertexCloseEnough(point);
        if (polygonVertexPair.isPresent()) {
            var pair = polygonVertexPair.get();
            var polygon = pair.getPolygon();
            var vertex = pair.getVertex();
            if (polygon.getVertices().size() == 3) {
                polygons.remove(polygon);
            } else {
                polygon.deleteVertex(vertex);
            }
            return Optional.of(vertex);
        }
        return Optional.empty();
    }

    private void addVertexOnEdge(Point2D point) {
        var polygonEdgePair = firstEdgeCloseEnough(point);
        polygonEdgePair.ifPresent(pair -> {
            var polygon = pair.getPolygon();
            var edge = pair.getEdge();
            var newVertex = new Vertex(edge.midPoint());
            long indexOfNewVertex = polygon.getVertices().indexOf(edge.getVertex1());
            polygon.addVertex((int) indexOfNewVertex + 1, newVertex);
        });
    }
}
