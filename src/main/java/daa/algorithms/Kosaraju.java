package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Kosaraju {

    public static SccResult run(Graph g, Metrics metrics) {
        int n = g.n();

        boolean[] visited = new boolean[n];
        int[] next = new int[n];
        List<Integer> finishOrder = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if (!visited[v]) {
                dfs(g, v, visited, next, finishOrder, metrics);
            }
        }

        Graph reversed = g.reverse();
        visited = new boolean[n];
        next = new int[n];
        int[] componentOf = new int[n];
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {
            int v = finishOrder.get(i);
            if (!visited[v]) {
                List<Integer> tree = new ArrayList<>();
                dfs(reversed, v, visited, next, tree, metrics);
                for (int u : tree) {
                    componentOf[u] = count;
                }
                count++;
            }
        }
        return new SccResult(count, componentOf);
    }

    private static void dfs(Graph g, int start, boolean[] visited, int[] next,
                            List<Integer> finished, Metrics metrics) {
        Deque<Integer> stack = new ArrayDeque<>();
        visited[start] = true;
        stack.push(start);
        metrics.updateDepth(stack.size());

        while (!stack.isEmpty()) {
            int u = stack.peek();
            List<Edge> list = g.neighbors(u);
            if (next[u] < list.size()) {
                Edge e = list.get(next[u]);
                next[u]++;
                metrics.edgeScans++;
                if (!visited[e.to]) {
                    visited[e.to] = true;
                    stack.push(e.to);
                    metrics.updateDepth(stack.size());
                }
            } else {
                stack.pop();
                finished.add(u);
            }
        }
    }
}
