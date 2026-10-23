package daa;

import daa.algorithms.CriticalPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.SccResult;
import daa.algorithms.Tarjan;
import daa.benchmark.GraphGenerator;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BonusTest {

    @Test
    void tarjanFindsSameComponentsAsKosaraju() {
        Random rnd = new Random(11);
        for (int seed = 0; seed < 60; seed++) {
            int n = 1 + rnd.nextInt(200);
            Graph g = GraphGenerator.directed(n, n == 1 ? 0 : rnd.nextInt(3 * n), seed);
            Metrics tm = new Metrics();
            SccResult t = Tarjan.run(g, tm);
            SccResult k = Kosaraju.run(g, new Metrics());
            assertEquals(k.count, t.count);
            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    assertEquals(k.componentOf[u] == k.componentOf[v], t.componentOf[u] == t.componentOf[v]);
                }
            }
            assertEquals(g.m(), tm.edgeScans);
        }
    }

    @Test
    void condensationIsAlwaysADag() {
        for (int seed = 0; seed < 30; seed++) {
            Graph g = GraphGenerator.directed(100, 150, seed);
            Graph c = CriticalPath.condensation(g, Kosaraju.run(g, new Metrics()));
            assertFalse(Kahn.run(c, new Metrics()).hasCycle);
        }
    }

    @Test
    void criticalPathOnCyclicFile() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "cyclic.txt"));
        CriticalPath cp = CriticalPath.run(g, new Metrics());
        assertEquals(4, cp.condensation.n());
        assertEquals(10, cp.length);
        assertEquals(4, cp.path.size());
    }

    @Test
    void criticalPathOnSmallSchedule() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 3);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 3, 4);
        g.addEdge(2, 3, 1);
        CriticalPath cp = CriticalPath.run(g, new Metrics());
        assertEquals(7, cp.length);
        assertEquals(3, cp.path.size());
    }
}
