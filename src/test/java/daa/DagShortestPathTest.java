package daa;

import daa.algorithms.DagShortestPath;
import daa.algorithms.PathResult;
import daa.graph.Edge;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DagShortestPathTest {

    static long[] bellmanFord(Graph g) {
        long inf = Long.MAX_VALUE;
        long[] dist = new long[g.n()];
        Arrays.fill(dist, inf);
        for (int v = 0; v < g.n(); v++) {
            if (g.inDegree(v) == 0) {
                dist[v] = 0;
            }
        }
        for (int i = 0; i < g.n() - 1; i++) {
            for (Edge e : g.edges()) {
                if (dist[e.from] != inf && dist[e.from] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[e.from] + e.weight;
                }
            }
        }
        return dist;
    }

    static Graph randomDag(int n, int m, Random rnd) {
        Graph g = new Graph(n, true);
        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b) {
                continue;
            }
            g.addEdge(Math.min(a, b), Math.max(a, b), rnd.nextInt(41) - 20);
        }
        return g;
    }

    @Test
    void matchesBellmanFordWithNegativeWeights() {
        Random rnd = new Random(5);
        for (int test = 0; test < 100; test++) {
            int n = 1 + rnd.nextInt(40);
            Graph g = randomDag(n, rnd.nextInt(4 * n + 1), rnd);
            Metrics metrics = new Metrics();
            PathResult r = DagShortestPath.run(g, metrics);
            assertArrayEquals(bellmanFord(g), r.dist);
            assertEquals(g.m(), metrics.edgeScans);
        }
    }

    @Test
    void restoredPathHasTheRightLength() {
        Random rnd = new Random(9);
        for (int test = 0; test < 50; test++) {
            int n = 2 + rnd.nextInt(30);
            Graph g = randomDag(n, 3 * n, rnd);
            PathResult r = DagShortestPath.run(g, new Metrics());
            for (int v = 0; v < n; v++) {
                List<Integer> path = r.pathTo(v);
                assertEquals(v, (int) path.get(path.size() - 1));
                assertEquals(0, g.inDegree(path.get(0)));
                long length = 0;
                for (int i = 0; i + 1 < path.size(); i++) {
                    length += cheapestEdge(g, path.get(i), path.get(i + 1));
                }
                assertEquals(r.dist[v], length);
            }
        }
    }

    static long cheapestEdge(Graph g, int u, int v) {
        long best = Long.MAX_VALUE;
        for (Edge e : g.neighbors(u)) {
            if (e.to == v) {
                best = Math.min(best, e.weight);
            }
        }
        return best;
    }

    @Test
    void dagFile() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "dag.txt"));
        PathResult r = DagShortestPath.run(g, new Metrics());
        assertArrayEquals(new long[]{0, 5, 3, 10, 7, 5, 0}, r.dist);
        assertEquals(List.of(0, 2, 5), r.pathTo(5));
    }

    @Test
    void cyclicGraphIsRejected() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "cyclic.txt"));
        assertThrows(IllegalArgumentException.class, () -> DagShortestPath.run(g, new Metrics()));
    }
}
