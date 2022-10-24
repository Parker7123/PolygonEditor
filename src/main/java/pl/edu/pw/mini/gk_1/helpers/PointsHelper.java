package pl.edu.pw.mini.gk_1.helpers;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class PointsHelper {

    private static final double EPS = 5;

    public static Point2D mouseEventToPoint2D(MouseEvent mouseEvent) {
        return new Point2D(mouseEvent.getX(), mouseEvent.getY());
    }

    public static boolean arePointsCloseEnough(Point2D p1, Point2D p2) {
        return p1.distance(p2) < EPS;
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

}
