package pl.edu.pw.mini.gk_1.shapes;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import pl.edu.pw.mini.gk_1.helpers.DrawingHelper;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.interfaces.Drawable;

import java.util.List;

public class Oval extends Polygon implements Drawable {
    private Point2D center;
    private double r;

    public Oval(Point2D center, double r) {
        super(new VerticesList());
        this.center = center;
        this.r = r;
    }

    public Point2D getCenter() {
        return center;
    }

    public void setCenter(Point2D center) {
        this.center = center;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        graphicsContext.strokeOval(center.getX() - r, center.getY() - r, r * 2, r * 2);
    }

    public void highlightRadius(GraphicsContext graphicsContext) {
        var fill = graphicsContext.getStroke();
        graphicsContext.setStroke(Paint.valueOf("red"));
        graphicsContext.strokeOval(center.getX() - r, center.getY() - r, r * 2, r * 2);
        graphicsContext.setStroke(fill);
    }
}
