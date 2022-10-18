package pl.edu.pw.mini.gk_1.shapes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VerticesList extends LinkedList<Vertex> {

    public VerticesList() {
        super();
    }

    public VerticesList(VerticesList list) {
        super(list);
    }

    public VerticesList(List<Vertex> list) {
        super(list);
    }
}
