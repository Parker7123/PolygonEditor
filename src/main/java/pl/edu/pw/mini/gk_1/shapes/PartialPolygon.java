package pl.edu.pw.mini.gk_1.shapes;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;

public class PartialPolygon extends AbstractPolygon {

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        super.draw(graphicsContext, drawingMode);
    }

    public Vertex getLastVertex() {
        return vertices.getLast();
    }

    public Vertex getFirstVertex() {
        return vertices.getFirst();
    }
}
