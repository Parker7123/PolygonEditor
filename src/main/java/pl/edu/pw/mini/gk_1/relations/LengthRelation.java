package pl.edu.pw.mini.gk_1.relations;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.interfaces.Drawable;
import pl.edu.pw.mini.gk_1.shapes.Edge;

public class LengthRelation extends Relation implements Drawable {

    private double length;

    public LengthRelation(Edge edge, double length) {
        super(edge);
        this.length = length;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        Point2D textPoint = getEdge().getVertex1().getPoint().midpoint(getEdge().getVertex2().getPoint());
        graphicsContext.fillText(String.valueOf(length), textPoint.getX() + 3, textPoint.getY() - 3);
    }
}
