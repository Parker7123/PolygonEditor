package pl.edu.pw.mini.gk_1.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import pl.edu.pw.mini.gk_1.helpers.DrawingHelper;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.interfaces.Drawable;

import java.util.function.Consumer;

public abstract class AbstractPolygon implements Drawable {

    private static final float VERTEX_RADIUS = 2;

    protected final VerticesList vertices = new VerticesList();

    public VerticesList getVertices() {
        return new VerticesList(vertices);
    }

    @Override
    public void draw(GraphicsContext graphicsContext, DrawingMode drawingMode) {
        drawWithModifier(graphicsContext, drawingMode, edge -> {});
    }

    protected void drawWithModifier(GraphicsContext graphicsContext, DrawingMode drawingMode, Consumer<Edge> edgeModifier) {
        if (vertices.isEmpty()) return;
        var iterator = vertices.iterator();
        Vertex vertex = vertices.getFirst();
        drawVertex(graphicsContext, vertex);
        while (iterator.hasNext()) {
            var nextVertex = iterator.next();
            DrawingHelper.drawLineBetweenTwoPoints(graphicsContext, vertex.getPoint(), nextVertex.getPoint(), drawingMode);
            edgeModifier.accept(new Edge(vertex, nextVertex));
            drawVertex(graphicsContext, nextVertex);
            vertex = nextVertex;
        }
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    private void drawVertex(GraphicsContext graphicsContext, Vertex vertex) {
        DrawingHelper.drawCircle(graphicsContext, vertex.getPoint(), VERTEX_RADIUS, Paint.valueOf("black"));
    }

    public static void highlightVertex(GraphicsContext graphicsContext, Vertex vertex) {
        DrawingHelper.drawCircle(graphicsContext, vertex.getPoint(), VERTEX_RADIUS + 2, Paint.valueOf("blue"));
    }

    public static void highlightEdge(GraphicsContext graphicsContext, Edge edge, DrawingMode drawingMode) {
        DrawingHelper.drawLineBetweenTwoPoints(graphicsContext,
                edge.getVertex1().getPoint(), edge.getVertex2().getPoint(), Paint.valueOf("red"), drawingMode);
    }
}
