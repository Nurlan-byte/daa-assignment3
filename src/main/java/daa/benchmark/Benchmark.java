package daa.benchmark;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
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

        for (int n : SIZES) {
            for (int k : DENSITIES) {
                int m = k * n;
                String name = (k == 4 ? "sparse_" : "dense_") + n;
                System.out.println("== " + name + " (n=" + n + ", m=" + m + ")");

                Graph undirected = GraphGenerator.undirected(n, m, SEED);
                Row kruskal = measure("Kruskal", name, undirected, mt -> Kruskal.run(undirected, mt).totalWeight);
                Row prim = measure("Prim", name, undirected, mt -> Prim.run(undirected, mt).totalWeight);
                check(kruskal.result == prim.result, "Kruskal and Prim weights differ on " + name);
                System.out.println("  MST weight      " + kruskal.result);

                Graph directed = GraphGenerator.directed(n, m, SEED);
                Row kosaraju = measure("Kosaraju", name, directed, mt -> (long) Kosaraju.run(directed, mt).count);
                System.out.println("  SCC count       " + kosaraju.result);

                Graph dag = GraphGenerator.dag(n, m, SEED);
                Row kahn = measure("Kahn", name, dag, mt -> (long) Kahn.run(dag, mt).order.size());
                TopoResult topo = Kahn.run(dag, new Metrics());
                check(!topo.hasCycle && Kahn.isValidOrder(dag, topo.order), "invalid topological order on " + name);
                Row paths = measure("DagShortestPath", name, dag, mt -> DagShortestPath.run(dag, mt).sumOfDistances());
                System.out.println("  DAG dist sum    " + paths.result);

                rows.add(kruskal);
                rows.add(prim);
                rows.add(kosaraju);
                rows.add(kahn);
                rows.add(paths);
            }
        }

        write(Path.of("results", "results.csv"), rows);
        System.out.println("Done. " + rows.size() + " rows written to results/results.csv");
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
