package daa.algorithms;

import daa.metrics.Metrics;

public class DSU {
    private int[] parent;
    private int[] rank;
    private int components;
    private final Metrics metrics;

    public DSU(int n, Metrics metrics) {
        this.metrics = metrics;
        makeSet(n);
    }

    public void makeSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        components = n;
    }

    public int find(int x) {
        metrics.dsuOps++;
        return root(x);
    }

    private int root(int x) {
        if (parent[x] != x) {
            parent[x] = root(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rx = root(x);
        int ry = root(y);
        if (rx == ry) {
            return false;
        }
        metrics.dsuOps++;
        if (rank[rx] < rank[ry]) {
            parent[rx] = ry;
        } else if (rank[rx] > rank[ry]) {
            parent[ry] = rx;
        } else {
            parent[ry] = rx;
            rank[rx]++;
        }
        components--;
        return true;
    }

    public int components() {
        return components;
    }
}
