package pl.edu.pw.mini.gk_1.shapes;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import pl.edu.pw.mini.gk_1.helpers.DrawingHelper;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.interfaces.Drawable;
import pl.edu.pw.mini.gk_1.interfaces.Movable;
import pl.edu.pw.mini.gk_1.relations.LengthRelation;
import pl.edu.pw.mini.gk_1.relations.PerpendicularRelation;

import java.util.Objects;
import java.util.Optional;

public class Edge implements Movable, Drawable {
    private static final float VERTEX_RADIUS = 2;

    private Vertex vertex1;
    private Vertex vertex2;
    private LengthRelation lengthRelation;
    private PerpendicularRelation perpendicularRelation;
    public Vertex getVertex1() {
        return vertex1;
    }

    public Vertex getVertex2() {
        return vertex2;
    }

    public void setVertex1(Vertex vertex1) {
        this.vertex1 = vertex1;
    }

    public void setVertex2(Vertex vertex2) {
        this.vertex2 = vertex2;
    }

    public Edge(Vertex vertex1, Vertex vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    @Override
    public void move(Point2D vector) {
        vertex1.move(vector);
        vertex2.move(vector);
    }

    public double length() {
        return vertex1.getPoint().distance(vertex2.getPoint());
    }

    public Point2D midPoint() {
        return vertex1.getPoint().midpoint(vertex2.getPoint());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return vertex1.equals(edge.vertex1) && vertex2.equals(edge.vertex2) ||
                vertex1.equals(edge.vertex2) && vertex2.equals(edge.vertex1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex1, vertex2);
    }

    public Optional<LengthRelation> getLengthRelation() {
        return Optional.ofNullable(lengthRelation);
    }

    public void setLengthRelation(LengthRelation lengthRelation) {
        this.lengthRelation = lengthRelation;
    }

    public void removeLengthRelation() {
        this.lengthRelation = null;
    }

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        DrawingHelper.drawLineBetweenTwoPoints(graphicsContext, vertex1.getPoint(), vertex2.getPoint(), drawingMode);
        drawVertex(graphicsContext, vertex1);
        drawVertex(graphicsContext, vertex2);
        if(lengthRelation != null) {
            Point2D textPoint = getVertex1().getPoint().midpoint(getVertex2().getPoint());
            graphicsContext.fillText(String.valueOf(lengthRelation.getLength()), textPoint.getX() + 3, textPoint.getY() - 3);
        }
        if(perpendicularRelation != null) {
            Point2D textPoint = getVertex1().getPoint().midpoint(getVertex2().getPoint());
            graphicsContext.fillText("_|", textPoint.getX() - 5, textPoint.getY() + 5);
        }
    }

    private void drawVertex(GraphicsContext graphicsContext, Vertex vertex) {
        DrawingHelper.drawCircle(graphicsContext, vertex.getPoint(), VERTEX_RADIUS, Paint.valueOf("black"));
    }

    public Optional<PerpendicularRelation> getPerpendicularRelation() {
        return Optional.ofNullable(perpendicularRelation);
    }

    public void setPerpendicularRelation(PerpendicularRelation perpendicularRelation) {
        this.perpendicularRelation = perpendicularRelation;
    }
}
