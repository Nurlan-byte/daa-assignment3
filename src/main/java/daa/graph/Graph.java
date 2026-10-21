package daa.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;
    private final int[] inDegree;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
        this.inDegree = new int[n];
    }

    public void addEdge(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n) {
            throw new IllegalArgumentException("vertex out of range: " + u + " " + v);
        }
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj.get(u).add(e);
        if (directed) {
            inDegree[v]++;
        } else if (u != v) {
            adj.get(v).add(new Edge(v, u, w));
        }
    }

    public int n() {
        return n;
    }

    public int m() {
        return edges.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> neighbors(int u) {
        return adj.get(u);
    }

    public List<Edge> edges() {
        return edges;
    }

    public int inDegree(int v) {
        return inDegree[v];
    }

    public Graph reverse() {
        Graph r = new Graph(n, directed);
        for (Edge e : edges) {
            r.addEdge(e.to, e.from, e.weight);
        }
        return r;
    }
}
