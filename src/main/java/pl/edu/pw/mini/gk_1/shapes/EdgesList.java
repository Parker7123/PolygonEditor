package pl.edu.pw.mini.gk_1.shapes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EdgesList extends ArrayList<Edge> {
    private final Map<Vertex, Edge[]> vertexEdgeMap;

    public EdgesList(VerticesList vertices) {
        if(vertices.isEmpty()) {
            vertexEdgeMap = null;
            return;
        }
        var edges = Stream.concat(
                IntStream.range(1, vertices.size()).mapToObj(i -> new Edge(vertices.get(i - 1), vertices.get(i))),
                Stream.of(new Edge(vertices.getLast(), vertices.getFirst())))
                .collect(Collectors.toList());
        this.addAll(edges);

        this.vertexEdgeMap = edges.stream().collect(Collectors.toMap(Edge::getVertex2, edge -> {
            Edge[] edgesArray = new Edge[2];
            edgesArray[0] = edge;
            return edgesArray;
        }));
        edges.forEach(edge -> vertexEdgeMap.get(edge.getVertex1())[1] = edge);
    }

    public void removeAndRepair(Vertex v) {
        Edge[] matchedEdges = edgesForStartingVertex(v);
        var prevVertex = matchedEdges[0].getVertex1();
        var nextVertex = matchedEdges[1].getVertex2();
        Edge createdEdge = new Edge(prevVertex, nextVertex);
        edgesForStartingVertex(prevVertex)[1] = createdEdge;
        edgesForStartingVertex(nextVertex)[0] = createdEdge;
        var index = indexOf(matchedEdges[0]);
        add(index, createdEdge);
        super.removeAll(List.of(matchedEdges));
    }

    public void addVertex(Vertex prevVertex, Vertex vertex) {
        Edge[] edgesForPrevVertex = edgesForStartingVertex(prevVertex);
        Edge[] edgesForNextVertex = edgesForStartingVertex(edgesForPrevVertex[1].getVertex2());
        Edge newEdge1 = new Edge(edgesForPrevVertex[1].getVertex1(), vertex);
        Edge newEdge2 = new Edge(vertex, edgesForPrevVertex[1].getVertex2());
        var index = indexOf(edgesForPrevVertex[1]);
        add(index, newEdge2);
        add(index, newEdge1);
        remove(edgesForPrevVertex[1]);
        edgesForPrevVertex[1] = newEdge1;
        edgesForNextVertex[0] = newEdge2;
        vertexEdgeMap.put(vertex, new Edge[]{newEdge1, newEdge2});
    }
    public Edge[] edgesForStartingVertex(Vertex vertex) {
        return vertexEdgeMap.get(vertex);
    }
}
