package daa;

import daa.algorithms.Kruskal;
import daa.algorithms.MstResult;
import daa.algorithms.Prim;
import daa.benchmark.GraphGenerator;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MstTest {

    @Test
    void kruskalAndPrimAgreeOnRandomGraphs() {
        Random rnd = new Random(1);
        for (int seed = 0; seed < 100; seed++) {
            int n = 2 + rnd.nextInt(60);
            int m = (n - 1) + rnd.nextInt(3 * n);
            Graph g = GraphGenerator.undirected(n, m, seed);

            MstResult k = Kruskal.run(g, new Metrics());
            MstResult p = Prim.run(g, new Metrics());
            assertEquals(k.totalWeight, p.totalWeight);
            assertEquals(n - 1, k.edges.size());
            assertEquals(n - 1, p.edges.size());
            assertEquals(1, k.components);
            assertEquals(1, p.components);
        }
    }

    @Test
    void disconnectedGraphGivesForest() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "disconnected.txt"));
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());
        assertEquals(3, k.components);
        assertEquals(3, p.components);
        assertEquals(6, k.totalWeight);
        assertEquals(6, p.totalWeight);
        assertEquals(4, k.edges.size());
        assertEquals(4, p.edges.size());
    }
}
