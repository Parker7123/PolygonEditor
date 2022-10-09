package pl.edu.pw.mini.gk_1.managers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.shapes.Polygon;

import java.util.List;

import static pl.edu.pw.mini.gk_1.helpers.PointsHelper.mouseEventToPoint2D;

public class AnimationManager extends AbstractManager {
    public AnimationManager(GraphicsContext graphicsContext, List<Polygon> polygons) {
        super(graphicsContext, polygons);
    }

    @Override
    public void onMouseClick(MouseEvent event) {

    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        var point = mouseEventToPoint2D(event);
        var polygonVertexPair = firstVertexCloseEnough(point);
        if (polygonVertexPair.isPresent()) {
            Polygon.highlightVertex(graphicsContext, polygonVertexPair.get().getVertex());
            return;
        }
        var polygonEdgePair = firstEdgeCloseEnough(point);
        if (polygonEdgePair.isPresent()) {
            Polygon.highlightEdge(graphicsContext, polygonEdgePair.get().getEdge(), drawingMode);
        }
    }
}
