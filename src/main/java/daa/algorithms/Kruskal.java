package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static MstResult run(Graph g, Metrics metrics) {
        List<Edge> sorted = new ArrayList<>(g.edges());
        sorted.sort((a, b) -> {
            metrics.comparisons++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU dsu = new DSU(g.n(), metrics);
        List<Edge> tree = new ArrayList<>();
        long total = 0;

        for (Edge e : sorted) {
            if (tree.size() == g.n() - 1) {
                break;
            }
            metrics.edgeScans++;
            if (dsu.find(e.from) != dsu.find(e.to)) {
                dsu.union(e.from, e.to);
                tree.add(e);
                total += e.weight;
            }
        }
        return new MstResult(tree, total, dsu.components());
    }
}
