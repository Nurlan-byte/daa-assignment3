package daa;

import daa.algorithms.Kahn;
import daa.algorithms.TopoResult;
import daa.benchmark.GraphGenerator;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopoTest {

    @Test
    void validOrderOnRandomDags() {
        for (int seed = 0; seed < 50; seed++) {
            int n = 2 + seed * 3;
            Graph g = GraphGenerator.dag(n, 4 * n, seed);
            Metrics metrics = new Metrics();
            TopoResult t = Kahn.run(g, metrics);
            assertFalse(t.hasCycle);
            assertTrue(Kahn.isValidOrder(g, t.order));
            assertEquals(g.m(), metrics.edgeScans);
        }
    }

    @Test
    void validOrderOnDagFile() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "dag.txt"));
        TopoResult t = Kahn.run(g, new Metrics());
        assertFalse(t.hasCycle);
        assertTrue(Kahn.isValidOrder(g, t.order));
    }

    @Test
    void cycleIsDetected() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "cyclic.txt"));
        TopoResult t = Kahn.run(g, new Metrics());
        assertTrue(t.hasCycle);
        assertTrue(t.order.size() < g.n());
    }

    @Test
    void selfLoopIsACycle() {
        Graph g = new Graph(2, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 1, 1);
        assertTrue(Kahn.run(g, new Metrics()).hasCycle);
    }
}
