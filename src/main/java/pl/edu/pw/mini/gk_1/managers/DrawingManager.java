package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.shapes.Polygon;

import java.util.List;

import static pl.edu.pw.mini.gk_1.helpers.PointsHelper.mouseEventToPoint2D;

public class DrawingManager extends AbstractManager {

    private final PartialPolygonManager partialPolygonManager;

    @Override
    public void onMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            onLeftMouseClick(event);
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        clearCanvas();
        if (!partialPolygonManager.isPolygonBeingDrawn()) {
            drawPolygons();
            return;
        }
        drawPolygonsWithPartialLine(mouseEventToPoint2D(event));
    }

    @Override
    public void setDrawingMode(DrawingMode drawingMode) {
        super.setDrawingMode(drawingMode);
        partialPolygonManager.setDrawingMode(drawingMode);
    }

    public void onKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE && partialPolygonManager.isPolygonBeingDrawn()) {
            partialPolygonManager.cancelDrawing();
            clearCanvas();
            drawPolygons();
        }
    }

    private void onLeftMouseClick(MouseEvent event) {
        var point = mouseEventToPoint2D(event);
        clearCanvas();
        if(partialPolygonManager.isPolygonBeingDrawn()) {
            var newPolygon = partialPolygonManager.continueDrawingPolygon(point);
            newPolygon.ifPresent(polygons::add);
        } else {
            partialPolygonManager.startDrawing(point);
        }
        drawPolygonsWithPartialLine(point);
    }

    public DrawingManager(GraphicsContext graphicsContext, List<Polygon> polygons) {
        super(graphicsContext, polygons);
        this.partialPolygonManager = new PartialPolygonManager(graphicsContext);
    }

    public void drawPolygonsWithPartialLine(Point2D point) {
        drawPolygons();
        partialPolygonManager.drawPartialPolygonWithLine(point);
    }

    public void clearCanvas() {
        graphicsContext.clearRect(0, 0, 1500, 1500);
    }
}
