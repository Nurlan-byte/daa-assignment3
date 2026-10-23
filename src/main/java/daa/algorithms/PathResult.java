package daa.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathResult {
    public final long[] dist;
    public final int[] parent;

    public PathResult(long[] dist, int[] parent) {
        this.dist = dist;
        this.parent = parent;
    }

    public long sumOfDistances() {
        long sum = 0;
        for (long d : dist) {
            sum += d;
        }
        return sum;
    }

    public List<Integer> pathTo(int v) {
        List<Integer> path = new ArrayList<>();
        for (int x = v; x != -1; x = parent[x]) {
            path.add(x);
        }
        Collections.reverse(path);
        return path;
    }
}
