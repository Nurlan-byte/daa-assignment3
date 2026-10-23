package daa.benchmark;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.algorithms.Tarjan;
import daa.algorithms.TopoResult;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Benchmark {
    static final long SEED = 42;
    static final int RUNS = 5;
    static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    static final int[] DENSITIES = {4, 20};

    static final long[][] REFERENCE = {
            {1000, 4000, 147970, 27, 396274},
            {1000, 20000, 30363, 1, 252411},
            {3162, 12648, 468911, 137, 1271911},
            {3162, 63240, 97589, 1, 723157},
            {10000, 40000, 1486139, 373, 3941828},
            {10000, 200000, 309550, 1, 2381342},
            {31623, 126492, 4715515, 1188, 12551891},
            {31623, 632460, 968517, 1, 7376612},
            {100000, 400000, 15012094, 3923, 40096539},
            {100000, 2000000, 3056681, 1, 23512087},
    };

    static class Row {
        String algorithm;
        String graph;
        int n;
        int m;
        double timeMs;
        Metrics metrics;
        long result;

        String toCsv() {
            return String.format(Locale.ROOT, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                    algorithm, graph, n, m, timeMs, metrics.dsuOps, metrics.edgeScans,
                    metrics.comparisons, metrics.maxDepth, result);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Row> rows = new ArrayList<>();
        List<Row> tarjanRows = new ArrayList<>();
        int refIndex = 0;

        for (int n : SIZES) {
            for (int k : DENSITIES) {
                int m = k * n;
                String name = (k == 4 ? "sparse_" : "dense_") + n;
                long[] ref = REFERENCE[refIndex++];
                System.out.println("== " + name + " (n=" + n + ", m=" + m + ")");

                Graph undirected = GraphGenerator.undirected(n, m, SEED);
                Row kruskal = measure("Kruskal", name, undirected, mt -> Kruskal.run(undirected, mt).totalWeight);
                Row prim = measure("Prim", name, undirected, mt -> Prim.run(undirected, mt).totalWeight);
                check(kruskal.result == prim.result, "Kruskal and Prim weights differ on " + name);
                check(kruskal.result == ref[2], "MST weight " + kruskal.result + " != reference " + ref[2]);
                System.out.println("  MST weight      " + kruskal.result + "  (reference " + ref[2] + ") OK");

                Graph directed = GraphGenerator.directed(n, m, SEED);
                Row kosaraju = measure("Kosaraju", name, directed, mt -> (long) Kosaraju.run(directed, mt).count);
                check(kosaraju.result == ref[3], "SCC count " + kosaraju.result + " != reference " + ref[3]);
                check(kosaraju.metrics.edgeScans == 2L * m, "Kosaraju must scan 2m edges");
                System.out.println("  SCC count       " + kosaraju.result + "  (reference " + ref[3] + ") OK");
                Row tarjan = measure("Tarjan", name, directed, mt -> (long) Tarjan.run(directed, mt).count);
                check(tarjan.result == kosaraju.result, "Tarjan and Kosaraju differ on " + name);
                tarjanRows.add(kosaraju);
                tarjanRows.add(tarjan);

                Graph dag = GraphGenerator.dag(n, m, SEED);
                Row kahn = measure("Kahn", name, dag, mt -> (long) Kahn.run(dag, mt).order.size());
                TopoResult topo = Kahn.run(dag, new Metrics());
                check(!topo.hasCycle && Kahn.isValidOrder(dag, topo.order), "invalid topological order on " + name);
                check(kahn.metrics.edgeScans == m, "Kahn must scan m edges");
                Row paths = measure("DagShortestPath", name, dag, mt -> DagShortestPath.run(dag, mt).sumOfDistances());
                check(paths.result == ref[4], "DAG distance sum " + paths.result + " != reference " + ref[4]);
                check(paths.metrics.edgeScans == m, "DAG relaxation must scan m edges");
                System.out.println("  DAG dist sum    " + paths.result + "  (reference " + ref[4] + ") OK");

                rows.add(kruskal);
                rows.add(prim);
                rows.add(kosaraju);
                rows.add(kahn);
                rows.add(paths);
            }
        }

        write(Path.of("results", "results.csv"), rows);
        write(Path.of("results", "tarjan_vs_kosaraju.csv"), tarjanRows);
        System.out.println("All reference values reproduced. " + rows.size() + " rows written to results/results.csv");
    }

    static Row measure(String algorithm, String graphName, Graph g, Function<Metrics, Long> algo) {
        double[] times = new double[RUNS];
        Row row = new Row();
        for (int r = 0; r < RUNS; r++) {
            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            long result = algo.apply(metrics);
            times[r] = (System.nanoTime() - start) / 1_000_000.0;
            if (r > 0) {
                check(result == row.result, algorithm + " gave different results between runs");
            }
            row.metrics = metrics;
            row.result = result;
        }
        Arrays.sort(times);
        row.algorithm = algorithm;
        row.graph = graphName;
        row.n = g.n();
        row.m = g.m();
        row.timeMs = times[RUNS / 2];
        return row;
    }

    static void check(boolean ok, String message) {
        if (!ok) {
            throw new IllegalStateException("CHECK FAILED: " + message);
        }
    }

    static void write(Path file, List<Row> rows) throws IOException {
        Files.createDirectories(file.getParent());
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            for (Row row : rows) {
                out.println(row.toCsv());
            }
        }
    }
}
