# DAA Assignment 3 — Graphs: DSU, MST, SCC and Shortest Paths

Yussupov Nurlan, group SE-2526. Course: Design and Analysis of Algorithms (AITU).

Scenario: City Network & Job Scheduler. One graph class, six algorithms written by hand,
operation counters inside the algorithms and a self-checking benchmark.

## What is inside

| Part | Class |
|---|---|
| Graph (adjacency list, directed and undirected) | `daa.graph.Graph`, `Edge`, `GraphReader` |
| Disjoint Set Union (union by rank + path compression) | `daa.algorithms.DSU` |
| Minimum spanning tree / forest | `daa.algorithms.Kruskal`, `Prim` |
| Strongly connected components (iterative DFS) | `daa.algorithms.Kosaraju` |
| Topological order with cycle detection | `daa.algorithms.Kahn` |
| Shortest paths in a DAG (super-source, path restore) | `daa.algorithms.DagShortestPath` |
| Bonus A: Tarjan's SCC (iterative, one pass) | `daa.algorithms.Tarjan` |
| Bonus B: condensation + critical path | `daa.algorithms.CriticalPath` |
| Counters: dsuOps, edgeScans, comparisons, maxDepth | `daa.metrics.Metrics` |
| Seeded generator, benchmark, CSV | `daa.benchmark.GraphGenerator`, `Benchmark` |

## Layout

```
src/main/java/daa/        graph, algorithms, metrics, benchmark, Main
src/test/java/daa/        JUnit 5 tests
data/                     small input graphs (connected, disconnected, cyclic, DAG)
results/results.csv       50 rows: 5 algorithms x 10 graphs
results/tarjan_vs_kosaraju.csv
results/plots/            3 PNG charts
scripts/plots.py          draws the charts and prints the slopes
REPORT.md                 the report
```

Input format: first line `n m directed|undirected`, then `m` lines `u v w`.
Vertices are `0 .. n-1`, weights are `int`.

## How to build and run

Requirements: Java 17+, Maven. Python 3 with matplotlib only for the plots.

```bash
mvn test                                  # build and run all JUnit 5 tests
mvn -q compile exec:java                  # benchmark: checks reference values, writes results/*.csv
python scripts/plots.py                   # draws results/plots/*.png and prints the slopes
mvn -q compile exec:java -Dexec.mainClass=daa.Main -Dexec.args="data/dag.txt"   # run on one file
```

The benchmark stops with `CHECK FAILED` if Kruskal and Prim give different weights, if a
topological order is invalid, if a counter does not match (Kosaraju = 2m, Kahn = m,
DAG relaxation = m) or if any reference value from the assignment is not reproduced.
All graphs use `new Random(42)`, so every number in the report is reproduced by the same command.

## Git

Branches: `main` (tag `v1.0`), `feature/dsu`, `feature/mst`, `feature/scc`, `feature/paths`,
`feature/metrics`, `feature/bonus`.

## AI-use acknowledgement

I used an AI assistant (Claude) to help with parts of the code, the tests and the wording of
the report. I checked every result against the reference values from the assignment, ran
all the tests myself, and I can explain every class and every number in the report.
