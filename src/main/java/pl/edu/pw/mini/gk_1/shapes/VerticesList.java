package pl.edu.pw.mini.gk_1.shapes;

import java.util.ArrayList;

public class VerticesList extends ArrayList<Vertex> {

    public VerticesList() {
        super();
    }

    public VerticesList(VerticesList list) {
        super(list);
    }

    public Vertex getLast() {
        return get(size() - 1);
    }

    public Vertex getFirst() {
        return get(0);
    }
}
