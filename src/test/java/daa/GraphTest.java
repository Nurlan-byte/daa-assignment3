package daa;

import daa.graph.Graph;
import daa.graph.GraphReader;
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
