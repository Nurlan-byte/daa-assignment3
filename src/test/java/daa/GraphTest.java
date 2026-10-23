package daa;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.MstResult;
import daa.algorithms.Prim;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphTest {

    @Test
    void readsUndirectedFile() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "small_undirected.txt"));
        assertEquals(6, g.n());
        assertEquals(10, g.m());
        assertFalse(g.isDirected());
        assertEquals(4, g.neighbors(1).size());
    }

    @Test
    void readsDirectedFile() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "cyclic.txt"));
        assertEquals(8, g.n());
        assertEquals(10, g.m());
        assertTrue(g.isDirected());
        assertEquals(2, g.inDegree(3));
    }

    @Test
    void rejectsBadVertex() {
        Graph g = new Graph(3, true);
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 3, 1));
    }

    @Test
    void selfLoopsAndParallelEdgesDoNotBreakMst() throws IOException {
        Graph g = GraphReader.read(Path.of("data", "small_undirected.txt"));
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());
        assertEquals(14, k.totalWeight);
        assertEquals(14, p.totalWeight);
        assertEquals(5, k.edges.size());
        assertEquals(1, k.components);
    }

    @Test
    void isolatedVerticesAndEmptyGraph() {
        Graph g = new Graph(4, true);
        assertEquals(4, Kosaraju.run(g, new Metrics()).count);
        assertEquals(4, Kahn.run(g, new Metrics()).order.size());
        assertEquals(0, DagShortestPath.run(g, new Metrics()).sumOfDistances());

        Graph empty = new Graph(0, false);
        assertEquals(0, Kruskal.run(empty, new Metrics()).totalWeight);
        assertEquals(0, Prim.run(empty, new Metrics()).components);
    }

    @Test
    void reverseFlipsEveryEdge() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 7);
        Graph r = g.reverse();
        assertEquals(1, r.neighbors(1).size());
        assertEquals(0, r.neighbors(1).get(0).to);
        assertEquals(1, r.neighbors(2).get(0).to);
    }
}
