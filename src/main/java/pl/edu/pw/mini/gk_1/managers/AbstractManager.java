package pl.edu.pw.mini.gk_1.managers;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.helpers.PointsHelper;
import pl.edu.pw.mini.gk_1.shapes.*;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractManager {
    protected final GraphicsContext graphicsContext;
    protected final List<Polygon> polygons;
    protected DrawingMode drawingMode = DrawingMode.NORMAL;

    protected AbstractManager(GraphicsContext graphicsContext, List<Polygon> polygons) {
        this.graphicsContext = graphicsContext;
        this.polygons = polygons;
    }

    public void setDrawingMode(DrawingMode drawingMode) {
        this.drawingMode = drawingMode;
    }

    public void drawPolygons() {
        polygons.forEach(polygon -> polygon.draw(graphicsContext, drawingMode));
    }

    public abstract void onMouseClick(MouseEvent event);

    public abstract void onMouseMoved(MouseEvent event);

    public void forEachVertexInPolygons(BiConsumer<Polygon, Vertex> consumer) {
        polygons.stream().flatMap(
                polygon -> polygon.getVertices().stream().map(
                        vertex -> new PolygonVertexPair(polygon, vertex)
                )
        ).forEach(pair -> consumer.accept(pair.getPolygon(), pair.getVertex()));
    }

    public Optional<PolygonVertexPair> firstMatchedVertexInPolygons(Predicate<Vertex> predicate) {
        return polygons.stream().flatMap(
                polygon -> polygon.getVertices().stream().map(
                        vertex -> new PolygonVertexPair(polygon, vertex)
                )
        ).filter(pair -> predicate.test(pair.getVertex())).findFirst();
    }

    public void onFirstMatchedVertexInPolygons(Predicate<Vertex> predicate, BiConsumer<Polygon, Vertex> consumer) {
        var polygonVertexPair = firstMatchedVertexInPolygons(predicate);
        polygonVertexPair.ifPresent(pair -> consumer.accept(pair.getPolygon(), pair.getVertex()));
    }

    public Optional<PolygonVertexPair> firstVertexCloseEnough(Point2D point) {
        return firstMatchedVertexInPolygons(vertex -> PointsHelper.arePointsCloseEnough(point, vertex.getPoint()));
    }

    public Optional<PolygonEdgePair> firstEdgeCloseEnough(Point2D point) {
        return polygons.stream().flatMap(
                polygon -> Stream.concat(IntStream.range(1, polygon.getVertices().size())
                                .mapToObj(i -> {
                                    var edge = new Edge(polygon.getVertices().get(i - 1), polygon.getVertices().get(i));
                                    return new PolygonEdgePair(polygon, edge);
                                }),
                        Stream.of(new PolygonEdgePair(polygon,
                                new Edge(polygon.getVertices().getLast(),
                                        polygon.getVertices().getFirst())))
                ).filter(pair -> {
                            var edge = pair.getEdge();
                            var cumulativeDistance = point.distance(edge.getVertex1().getPoint()) +
                                    point.distance(edge.getVertex2().getPoint());
                            return cumulativeDistance >= edge.length() && cumulativeDistance < edge.length() + 0.5;
                        }
                )).findFirst();
    }
}
