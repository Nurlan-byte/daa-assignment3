package daa.graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GraphReader {

    public static Graph read(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(file)) {
            if (!line.isBlank()) {
                lines.add(line.trim());
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("empty file: " + file);
        }

        String[] head = lines.get(0).split("\\s+");
        int n = Integer.parseInt(head[0]);
        int m = Integer.parseInt(head[1]);
        boolean directed;
        if (head[2].equals("directed")) {
            directed = true;
        } else if (head[2].equals("undirected")) {
            directed = false;
        } else {
            throw new IllegalArgumentException("expected directed or undirected, got " + head[2]);
        }
        if (lines.size() - 1 != m) {
            throw new IllegalArgumentException("expected " + m + " edges, got " + (lines.size() - 1));
        }

        Graph g = new Graph(n, directed);
        for (int i = 1; i <= m; i++) {
            String[] p = lines.get(i).split("\\s+");
            g.addEdge(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
        }
        return g;
    }
}
