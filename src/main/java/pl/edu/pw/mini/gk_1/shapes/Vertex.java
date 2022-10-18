package pl.edu.pw.mini.gk_1.shapes;

import javafx.geometry.Point2D;
import pl.edu.pw.mini.gk_1.interfaces.Movable;

import java.util.Objects;

public class Vertex implements Movable {
    private Point2D point;

    public Vertex(double x, double y) {
        point = new Point2D(x, y);
    }

    public Vertex(Point2D point) {
        this.point = point;
    }
    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D point) {
        this.point = point;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    @Override
    public void move(Point2D vector) {
        point = point.add(vector);
    }
}
