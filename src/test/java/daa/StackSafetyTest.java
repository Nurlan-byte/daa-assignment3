package daa;

import daa.algorithms.Kosaraju;
import daa.algorithms.SccResult;
import daa.algorithms.Tarjan;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackSafetyTest {

    static Graph path(int n) {
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) {
            g.addEdge(i, i + 1, 1);
        }
        return g;
    }

    @Test
    void kosarajuOnPathOf100000Vertices() {
        Metrics metrics = new Metrics();
        SccResult scc = Kosaraju.run(path(100_000), metrics);
        assertEquals(100_000, scc.count);
        assertEquals(100_000, metrics.maxDepth);
    }

    @Test
    void kosarajuOnCycleOf100000Vertices() {
        Graph g = path(100_000);
        g.addEdge(99_999, 0, 1);
        assertEquals(1, Kosaraju.run(g, new Metrics()).count);
    }

    @Test
    void tarjanOnPathOf100000Vertices() {
        assertEquals(100_000, Tarjan.run(path(100_000), new Metrics()).count);
    }
}
