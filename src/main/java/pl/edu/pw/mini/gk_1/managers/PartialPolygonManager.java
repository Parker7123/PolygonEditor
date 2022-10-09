package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import pl.edu.pw.mini.gk_1.helpers.DrawingHelper;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.shapes.PartialPolygon;
import pl.edu.pw.mini.gk_1.shapes.Polygon;
import pl.edu.pw.mini.gk_1.shapes.Vertex;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static pl.edu.pw.mini.gk_1.helpers.PointsHelper.arePointsCloseEnough;

public class PartialPolygonManager {

    private final GraphicsContext graphicsContext;
    private PartialPolygon partialPolygon;
    private final AtomicBoolean polygonBeingDrawn = new AtomicBoolean();
    private DrawingMode drawingMode = DrawingMode.NORMAL;

    public PartialPolygonManager(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public boolean isPolygonBeingDrawn() {
        return polygonBeingDrawn.get();
    }

    public void setDrawingMode(DrawingMode drawingMode) {
        this.drawingMode = drawingMode;
    }

    public void startDrawing(Point2D point){
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

    public List<Vertex> endDrawing() {
        if(!isPolygonBeingDrawn()) {
            throw new IllegalStateException();
        }
        polygonBeingDrawn.set(false);
        return partialPolygon.getVertices();
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

    public void cancelDrawing() {
        partialPolygon = new PartialPolygon();
        polygonBeingDrawn.set(false);
    }
}
