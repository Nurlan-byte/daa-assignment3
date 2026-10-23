package daa;

import daa.algorithms.Kosaraju;
import daa.algorithms.SccResult;
import daa.algorithms.Tarjan;
import daa.benchmark.GraphGenerator;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
