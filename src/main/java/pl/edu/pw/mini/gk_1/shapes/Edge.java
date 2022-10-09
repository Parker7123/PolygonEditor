package pl.edu.pw.mini.gk_1.shapes;

import javafx.geometry.Point2D;
import pl.edu.pw.mini.gk_1.interfaces.Movable;

public class Edge implements Movable {
    private final Vertex vertex1;
    private final Vertex vertex2;

    public Vertex getVertex1() {
        return vertex1;
    }

    public Vertex getVertex2() {
        return vertex2;
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
}
