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
}
