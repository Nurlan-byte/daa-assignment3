package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.List;

public class CriticalPath {
    public final Graph condensation;
    public final long length;
    public final List<Integer> path;

    private CriticalPath(Graph condensation, long length, List<Integer> path) {
        this.condensation = condensation;
        this.length = length;
        this.path = path;
    }

    public static Graph condensation(Graph g, SccResult scc) {
        Graph c = new Graph(scc.count, true);
        for (Edge e : g.edges()) {
            int a = scc.componentOf[e.from];
            int b = scc.componentOf[e.to];
            if (a != b) {
                c.addEdge(a, b, e.weight);
            }
        }
        return c;
    }

    public static CriticalPath run(Graph g, Metrics metrics) {
        SccResult scc = Kosaraju.run(g, new Metrics());
        Graph c = condensation(g, scc);

        Graph negated = new Graph(c.n(), true);
        for (Edge e : c.edges()) {
            negated.addEdge(e.from, e.to, -e.weight);
        }
        PathResult shortest = DagShortestPath.run(negated, metrics);

        int end = 0;
        for (int v = 1; v < c.n(); v++) {
            if (shortest.dist[v] < shortest.dist[end]) {
                end = v;
            }
        }
        long length = c.n() == 0 ? 0 : -shortest.dist[end];
        return new CriticalPath(c, length, shortest.pathTo(end));
    }
}
