package pl.edu.pw.mini.gk_1.helpers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class DrawingHelper {

    public static void drawLineBetweenTwoPoints(GraphicsContext graphicsContext, Point2D p1, Point2D p2, DrawingMode drawingMode) {
        switch (drawingMode) {
            case NORMAL:
                graphicsContext.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                break;
            case BRESENHAM:
                drawLineBetweenTwoPointsBresenham(graphicsContext, p1, p2, Color.BLACK);
        }
    }

    public static void drawLineBetweenTwoPoints(GraphicsContext graphicsContext, Point2D p1, Point2D p2, Paint paint, DrawingMode drawingMode) {
        switch (drawingMode) {
            case NORMAL:
                var fill = graphicsContext.getStroke();
                graphicsContext.setStroke(paint);
                graphicsContext.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                graphicsContext.setStroke(fill);
                break;
            case BRESENHAM:
                drawLineBetweenTwoPointsBresenham(graphicsContext, p1, p2, Color.valueOf(paint.toString()));
        }
    }

    public static void drawCircle(GraphicsContext graphicsContext, Point2D point, float radius, Paint paint) {
        var fill = graphicsContext.getFill();
        graphicsContext.setFill(paint);
        graphicsContext.fillOval(point.getX() - radius, point.getY() -  radius, radius * 2, radius * 2);
        graphicsContext.setFill(fill);
    }

    public static void drawLineBetweenTwoPointsBresenham(GraphicsContext graphicsContext, Point2D p1, Point2D p2, Color color)
    {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int x1 = (int) p1.getX();
        int y1 = (int) p1.getY();
        int x2 = (int) p2.getX();
        int y2 = (int) p2.getY();

        // zmienne pomocnicze
        int d, dx, dy, ai, bi, xi, yi;
        int x = x1, y = y1;
        // ustalenie kierunku rysowania
        if (x1 < x2)
        {
            xi = 1;
            dx = x2 - x1;
        }
        else
        {
            xi = -1;
            dx = x1 - x2;
        }
        // ustalenie kierunku rysowania
        if (y1 < y2)
        {
            yi = 1;
            dy = y2 - y1;
        }
        else
        {
            yi = -1;
            dy = y1 - y2;
        }
        // pierwszy piksel
        pixelWriter.setColor(x, y, color);
        // oś wiodąca OX
        if (dx > dy)
        {
            ai = (dy - dx) * 2;
            bi = dy * 2;
            d = bi - dx;
            // pętla po kolejnych x
            while (x != x2)
            {
                // test współczynnika
                if (d >= 0)
                {
                    x += xi;
                    y += yi;
                    d += ai;
                }
                else
                {
                    d += bi;
                    x += xi;
                }
                pixelWriter.setColor(x, y, color);
            }
        }
        // oś wiodąca OY
        else
        {
            ai = ( dx - dy ) * 2;
            bi = dx * 2;
            d = bi - dy;
            // pętla po kolejnych y
            while (y != y2)
            {
                // test współczynnika
                if (d >= 0)
                {
                    x += xi;
                    y += yi;
                    d += ai;
                }
                else
                {
                    d += bi;
                    y += yi;
                }
                pixelWriter.setColor(x, y, color);
            }
        }
    }
}
