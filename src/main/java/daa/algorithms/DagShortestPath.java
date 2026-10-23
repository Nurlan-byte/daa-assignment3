package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.Arrays;

public class DagShortestPath {

    public static PathResult run(Graph g, Metrics metrics) {
        TopoResult topo = Kahn.run(g, new Metrics());
        if (topo.hasCycle) {
            throw new IllegalArgumentException("graph has a cycle, it is not a DAG");
        }

        int n = g.n();
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(parent, -1);
        for (int v = 0; v < n; v++) {
            if (g.inDegree(v) == 0) {
                dist[v] = 0;
            }
        }

        for (int u : topo.order) {
            for (Edge e : g.neighbors(u)) {
                metrics.edgeScans++;
                metrics.comparisons++;
                if (dist[u] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[u] + e.weight;
                    parent[e.to] = u;
                }
            }
        }
        return new PathResult(dist, parent);
    }
}
