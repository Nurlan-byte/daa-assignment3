package daa;

import daa.algorithms.Kosaraju;
import daa.algorithms.SccResult;
import daa.benchmark.GraphGenerator;
import daa.graph.Edge;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SccTest {

    static boolean[][] reachability(Graph g) {
        int n = g.n();
        boolean[][] reach = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            reach[s][s] = true;
            queue.add(s);
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (Edge e : g.neighbors(u)) {
                    if (!reach[s][e.to]) {
                        reach[s][e.to] = true;
                        queue.add(e.to);
                    }
                }
            }
        }
        return reach;
    }

    static void assertSameAsBruteForce(Graph g, SccResult scc) {
        boolean[][] reach = reachability(g);
        int n = g.n();
        boolean[] leader = new boolean[n];
        int count = 0;
        for (int u = 0; u < n; u++) {
            boolean first = true;
            for (int v = 0; v < n; v++) {
                boolean same = reach[u][v] && reach[v][u];
                assertEquals(same, scc.componentOf[u] == scc.componentOf[v]);
                if (same && v < u) {
                    first = false;
                }
            }
            if (first) {
                count++;
            }
        }
        assertEquals(count, scc.count);
    }

    @Test
    void matchesBruteForceOnRandomGraphs() {
        Random rnd = new Random(3);
        for (int seed = 0; seed < 60; seed++) {
            int n = 1 + rnd.nextInt(200);
            int m = rnd.nextInt(3 * n);
            Graph g = GraphGenerator.directed(n, n == 1 ? 0 : m, seed);
            Metrics metrics = new Metrics();
            SccResult scc = Kosaraju.run(g, metrics);
            assertSameAsBruteForce(g, scc);
            assertEquals(2L * g.m(), metrics.edgeScans);
        }
    }

    @Test
    void smallCyclicFile() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "cyclic.txt"));
        SccResult scc = Kosaraju.run(g, new Metrics());
        assertEquals(4, scc.count);
        assertEquals(scc.componentOf[0], scc.componentOf[2]);
        assertEquals(scc.componentOf[3], scc.componentOf[5]);
    }
}
