package daa;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.MstResult;
import daa.algorithms.PathResult;
import daa.algorithms.Prim;
import daa.algorithms.SccResult;
import daa.algorithms.TopoResult;
import daa.graph.Graph;
import daa.graph.GraphReader;
import daa.metrics.Metrics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("usage: Main <graph file>");
            return;
        }
        Graph g = GraphReader.read(Path.of(args[0]));
        System.out.println(args[0] + ": n=" + g.n() + ", m=" + g.m() + (g.isDirected() ? ", directed" : ", undirected"));

        if (!g.isDirected()) {
            MstResult k = Kruskal.run(g, new Metrics());
            MstResult p = Prim.run(g, new Metrics());
            System.out.println("Kruskal: weight=" + k.totalWeight + ", edges=" + k.edges + ", components=" + k.components);
            System.out.println("Prim:    weight=" + p.totalWeight + ", edges=" + p.edges + ", components=" + p.components);
            return;
        }

        SccResult scc = Kosaraju.run(g, new Metrics());
        System.out.println("Kosaraju: " + scc.count + " components, componentOf=" + Arrays.toString(scc.componentOf));

        TopoResult topo = Kahn.run(g, new Metrics());
        if (topo.hasCycle) {
            System.out.println("Kahn: cycle detected, only " + topo.order.size() + " of " + g.n() + " vertices were ordered");
            return;
        }
        System.out.println("Kahn: order=" + topo.order);

        PathResult paths = DagShortestPath.run(g, new Metrics());
        for (int v = 0; v < g.n(); v++) {
            System.out.println("  dist[" + v + "]=" + paths.dist[v] + "  path=" + paths.pathTo(v));
        }
    }
}
