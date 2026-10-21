package daa;

import daa.algorithms.DSU;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DSUTest {

    @Test
    void agreesWithNaiveLabels() {
        Random rnd = new Random(7);
        for (int test = 0; test < 50; test++) {
            int n = 1 + rnd.nextInt(100);
            DSU dsu = new DSU(n, new Metrics());
            int[] label = new int[n];
            for (int i = 0; i < n; i++) {
                label[i] = i;
            }

            for (int step = 0; step < 2 * n; step++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                boolean expected = label[a] != label[b];
                assertEquals(expected, dsu.union(a, b));
                int old = label[a];
                for (int i = 0; i < n; i++) {
                    if (label[i] == old) {
                        label[i] = label[b];
                    }
                }

                int x = rnd.nextInt(n);
                int y = rnd.nextInt(n);
                assertEquals(label[x] == label[y], dsu.find(x) == dsu.find(y));
            }

            Set<Integer> sets = new HashSet<>();
            for (int l : label) {
                sets.add(l);
            }
            assertEquals(sets.size(), dsu.components());
        }
    }

    @Test
    void countsFindsAndSuccessfulUnions() {
        Metrics metrics = new Metrics();
        DSU dsu = new DSU(4, metrics);
        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(1, 0));
        dsu.find(0);
        dsu.find(3);
        assertEquals(3, metrics.dsuOps);
        assertEquals(3, dsu.components());
    }
}
