package daa.algorithms;

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
}
