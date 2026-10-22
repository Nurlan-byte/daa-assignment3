package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Kahn {

    public static TopoResult run(Graph g, Metrics metrics) {
        int n = g.n();
        int[] inDegree = new int[n];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++) {
            inDegree[v] = g.inDegree(v);
            if (inDegree[v] == 0) {
                queue.add(v);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (Edge e : g.neighbors(u)) {
                metrics.edgeScans++;
                inDegree[e.to]--;
                if (inDegree[e.to] == 0) {
                    queue.add(e.to);
                }
            }
        }
        return new TopoResult(order, order.size() < n);
    }

    public static boolean isValidOrder(Graph g, List<Integer> order) {
        if (order.size() != g.n()) {
            return false;
        }
        int[] pos = new int[g.n()];
        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }
        for (Edge e : g.edges()) {
            if (pos[e.from] >= pos[e.to]) {
                return false;
            }
        }
        return true;
    }
}
