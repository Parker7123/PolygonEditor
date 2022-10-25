package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.helpers.ShapeMode;
import pl.edu.pw.mini.gk_1.shapes.Oval;
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
            if (getShapeMode() == ShapeMode.POLYGON) {
                var newPolygon = partialPolygonManager.continueDrawingPolygon(point);
                newPolygon.ifPresent(polygons::add);
            } else {
                var newOval = partialPolygonManager.continueDrawingOval(point);
                newOval.ifPresent(ovals::add);
            }
        } else {
            if (getShapeMode() == ShapeMode.POLYGON) {
                partialPolygonManager.startDrawingPolygon(point);
            } else {
                partialPolygonManager.startDrawingOval(point);
            }
        }
        drawPolygonsWithPartialLine(point);
    }

    public DrawingManager(GraphicsContext graphicsContext, List<Polygon> polygons, List<Oval> ovals) {
        super(graphicsContext, polygons, ovals);
        this.partialPolygonManager = new PartialPolygonManager(graphicsContext);
    }

    public void drawPolygonsWithPartialLine(Point2D point) {
        drawPolygons();
        if (getShapeMode() == ShapeMode.POLYGON) {
            partialPolygonManager.drawPartialPolygonWithLine(point);
        } else {
            partialPolygonManager.drawOval(point);
        }
    }

    public void clearCanvas() {
        graphicsContext.clearRect(0, 0, 1500, 1500);
    }
}
