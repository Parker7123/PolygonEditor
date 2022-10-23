package pl.edu.pw.mini.gk_1.helpers;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;

public class PointsHelper {

    private static final double EPS = 5;

    public static Point2D mouseEventToPoint2D(MouseEvent mouseEvent) {
        return new Point2D(mouseEvent.getX(), mouseEvent.getY());
    }

    public static boolean arePointsCloseEnough(Point2D p1, Point2D p2) {
        return p1.distance(p2) < EPS;
    }

    public static Point2D pointOnSameLineWithDistanceFromStart(Point2D start, Point2D end, double distance) {
        Point2D vector = end.subtract(start);
        return start.add(vector.multiply(distance / vector.distance(0d, 0d)));
    }

    public static Point2D[] intersectionPointsOfTwoCircles(Point2D p1, Point2D p2, double r1, double r2) {
        double d = p1.distance(p2);
        double d1 = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
        double h = Math.sqrt(r1 * r1 - d1 * d1);
        double x3 = p1.getX() + (d1 * (p2.getX() - p1.getX())) / d;
        double y3 = p1.getY() + (d1 * (p2.getY() - p1.getY())) / d;
        double x4_i = x3 + (h * (p2.getY() - p1.getY())) / d;
        double y4_i = y3 - (h * (p2.getY() - p1.getY())) / d;
        double x4_ii = x3 - (h * (p2.getY() - p1.getY())) / d;
        double y4_ii = y3 + (h * (p2.getY() - p1.getY())) / d;
        return new Point2D[]{new Point2D(x4_i, y4_i), new Point2D(x4_ii, y4_ii)};
    }

    public static Point2D perpendicularVector(Point2D vector) {
        return new Point2D(-vector.getY(), vector.getX());
    }

    public static Point2D closestPerpendicularVector(Point2D vector, Point2D vectorClosestTo) {
        var perpendicularVector1 = new Point2D(-vector.getY(), vector.getX());
        var perpendicularVector2 = new Point2D(vector.getY(), -vector.getX());
        if(perpendicularVector1.distance(vectorClosestTo) < perpendicularVector2.distance(vectorClosestTo)) {
            return perpendicularVector1;
        } else {
            return perpendicularVector2;
        }
    }

    public static boolean areVectorsPerpendicular(Point2D p1, Point2D p2) {
        return p1.angle(p2) == 90;
    }
}
