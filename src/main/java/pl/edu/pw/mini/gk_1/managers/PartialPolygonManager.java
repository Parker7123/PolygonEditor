package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import pl.edu.pw.mini.gk_1.helpers.DrawingHelper;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.shapes.Oval;
import pl.edu.pw.mini.gk_1.shapes.PartialPolygon;
import pl.edu.pw.mini.gk_1.shapes.Polygon;
import pl.edu.pw.mini.gk_1.shapes.Vertex;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static pl.edu.pw.mini.gk_1.helpers.PointsHelper.arePointsCloseEnough;

public class PartialPolygonManager {

    private final GraphicsContext graphicsContext;
    private PartialPolygon partialPolygon;
    private final AtomicBoolean polygonBeingDrawn = new AtomicBoolean();
    private DrawingMode drawingMode = DrawingMode.NORMAL;
    private Oval oval;

    public PartialPolygonManager(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public boolean isPolygonBeingDrawn() {
        return polygonBeingDrawn.get();
    }

    public void setDrawingMode(DrawingMode drawingMode) {
        this.drawingMode = drawingMode;
    }

    public void startDrawingPolygon(Point2D point){
        if(isPolygonBeingDrawn()) {
            throw new IllegalStateException();
        }
        partialPolygon = new PartialPolygon();
        partialPolygon.addVertex(new Vertex(point.getX(), point.getY()));
        partialPolygon.draw(graphicsContext, drawingMode);
        polygonBeingDrawn.set(true);
    }

    public Optional<Polygon> continueDrawingPolygon(Point2D point) {
        if(!isPolygonBeingDrawn()) {
            throw new IllegalStateException();
        }
        var firstVertex = partialPolygon.getFirstVertex();
        if(!arePointsCloseEnough(point, firstVertex.getPoint())) {
            partialPolygon.addVertex(new Vertex(point.getX(), point.getY()));
            return Optional.empty();
        }
        polygonBeingDrawn.set(false);
        if (partialPolygon.getVertices().size() >= 3) {
            return Optional.of(new Polygon(partialPolygon.getVertices()));
        }
        return Optional.empty();
    }

    public void startDrawingOval(Point2D point) {
        if(isPolygonBeingDrawn()) {
            throw new IllegalStateException();
        }
        oval = new Oval(point, 0);
        oval.draw(graphicsContext, drawingMode);
        polygonBeingDrawn.set(true);
    }

    public Optional<Oval> continueDrawingOval(Point2D point) {
        if(!isPolygonBeingDrawn()) {
            throw new IllegalStateException();
        }
        double pointCenterDistance = point.distance(oval.getCenter());
        if(Math.abs(pointCenterDistance - oval.getR()) < 10){
            polygonBeingDrawn.set(false);
            return Optional.of(oval);
        }
        return Optional.empty();
    }

    public void drawPartialPolygonWithLine(Point2D point) {
        if(!isPolygonBeingDrawn()) {
            return;
        }
        var firstVertex = partialPolygon.getFirstVertex();
        if(arePointsCloseEnough(point, firstVertex.getPoint())) {
            Polygon.highlightVertex(graphicsContext, firstVertex);
        }
        partialPolygon.draw(graphicsContext, drawingMode);
        DrawingHelper.drawLineBetweenTwoPoints(graphicsContext, partialPolygon.getLastVertex().getPoint(), point, drawingMode);
    }

    public void drawOval(Point2D point) {
        if(!isPolygonBeingDrawn()) {
            return;
        }
        oval.setR(oval.getCenter().distance(point));
        oval.draw(graphicsContext, drawingMode);
    }

    public void cancelDrawing() {
        partialPolygon = new PartialPolygon();
        oval = new Oval(Point2D.ZERO, 0);
        polygonBeingDrawn.set(false);
    }
}
