package daa.benchmark;

import daa.graph.Graph;

import java.util.Random;

public class GraphGenerator {

    public static Graph undirected(int n, int m, long seed) {
        Random rnd = new Random(seed);
        int[] perm = shuffled(n, rnd);
        Graph g = new Graph(n, false);
        for (int i = 1; i < n; i++) {
            g.addEdge(perm[rnd.nextInt(i)], perm[i], 1 + rnd.nextInt(1000));
        }
        addRandomEdges(g, m - (n - 1), rnd, null);
        return g;
    }

    public static Graph directed(int n, int m, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(n, true);
        addRandomEdges(g, m, rnd, null);
        return g;
    }

    public static Graph dag(int n, int m, long seed) {
        Random rnd = new Random(seed);
        int[] rank = shuffled(n, rnd);
        Graph g = new Graph(n, true);
        addRandomEdges(g, m, rnd, rank);
        return g;
    }

    private static int[] shuffled(int n, Random rnd) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
        return a;
    }

    private static void addRandomEdges(Graph g, int count, Random rnd, int[] rank) {
        int n = g.n();
        for (int k = 0; k < count; k++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            if (rank != null && rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
    }
}
