package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Tarjan {

    public static SccResult run(Graph g, Metrics metrics) {
        int n = g.n();
        int[] index = new int[n];
        int[] low = new int[n];
        int[] next = new int[n];
        boolean[] onStack = new boolean[n];
        int[] componentOf = new int[n];
        Arrays.fill(index, -1);

        Deque<Integer> active = new ArrayDeque<>();
        Deque<Integer> callStack = new ArrayDeque<>();
        int counter = 0;
        int count = 0;

        for (int s = 0; s < n; s++) {
            if (index[s] != -1) {
                continue;
            }
            index[s] = counter;
            low[s] = counter;
            counter++;
            active.push(s);
            onStack[s] = true;
            callStack.push(s);
            metrics.updateDepth(callStack.size());

            while (!callStack.isEmpty()) {
                int u = callStack.peek();
                List<Edge> list = g.neighbors(u);
                if (next[u] < list.size()) {
                    Edge e = list.get(next[u]);
                    next[u]++;
                    metrics.edgeScans++;
                    int v = e.to;
                    if (index[v] == -1) {
                        index[v] = counter;
                        low[v] = counter;
                        counter++;
                        active.push(v);
                        onStack[v] = true;
                        callStack.push(v);
                        metrics.updateDepth(callStack.size());
                    } else if (onStack[v]) {
                        low[u] = Math.min(low[u], index[v]);
                    }
                } else {
                    callStack.pop();
                    if (!callStack.isEmpty()) {
                        int parent = callStack.peek();
                        low[parent] = Math.min(low[parent], low[u]);
                    }
                    if (low[u] == index[u]) {
                        int w;
                        do {
                            w = active.pop();
                            onStack[w] = false;
                            componentOf[w] = count;
                        } while (w != u);
                        count++;
                    }
                }
            }
        }
        return new SccResult(count, componentOf);
    }
}
