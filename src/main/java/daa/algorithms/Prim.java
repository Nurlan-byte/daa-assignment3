package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {

    public static MstResult run(Graph g, Metrics metrics) {
        boolean[] inTree = new boolean[g.n()];
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> {
            metrics.comparisons++;
            return Integer.compare(a.weight, b.weight);
        });
        List<Edge> tree = new ArrayList<>();
        long total = 0;
        if (g.n() == 0) {
            return new MstResult(tree, 0, 0);
        }

        visit(g, 0, inTree, pq, metrics);
        while (!pq.isEmpty()) {
            Edge e = pq.poll();
            if (inTree[e.to]) {
                continue;
            }
            tree.add(e);
            total += e.weight;
            visit(g, e.to, inTree, pq, metrics);
        }
        return new MstResult(tree, total, 1);
    }

    private static void visit(Graph g, int v, boolean[] inTree, PriorityQueue<Edge> pq, Metrics metrics) {
        inTree[v] = true;
        for (Edge e : g.neighbors(v)) {
            metrics.edgeScans++;
            if (!inTree[e.to]) {
                pq.add(e);
            }
        }
    }
}
