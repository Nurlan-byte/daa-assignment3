import csv
import math
import os

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CSV = os.path.join(ROOT, "results", "results.csv")
PLOTS = os.path.join(ROOT, "results", "plots")
ALGORITHMS = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
DENSITIES = ["sparse", "dense"]


def load():
    rows = []
    with open(CSV) as f:
        for r in csv.DictReader(f):
            for key in ["n", "m", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"]:
                r[key] = int(r[key])
            r["time_ms"] = float(r["time_ms"])
            r["density"] = r["graph"].split("_")[0]
            rows.append(r)
    return rows


def series(rows, algorithm, density):
    s = [r for r in rows if r["algorithm"] == algorithm and r["density"] == density]
    return sorted(s, key=lambda r: r["n"])


def slope(xs, ys):
    lx = [math.log(x) for x in xs]
    ly = [math.log(y) for y in ys]
    mx = sum(lx) / len(lx)
    my = sum(ly) / len(ly)
    num = sum((a - mx) * (b - my) for a, b in zip(lx, ly))
    den = sum((a - mx) ** 2 for a in lx)
    return num / den


def main():
    rows = load()
    os.makedirs(PLOTS, exist_ok=True)

    plt.figure(figsize=(8, 5))
    for a in ALGORITHMS:
        for d in DENSITIES:
            s = series(rows, a, d)
            ops = [r["edge_scans"] + r["dsu_ops"] + r["comparisons"] for r in s]
            plt.loglog([r["n"] for r in s], ops, marker="o", linestyle="-" if d == "sparse" else "--",
                       label=f"{a} ({d})")
    plt.xlabel("n (vertices)")
    plt.ylabel("operations = edge scans + DSU ops + comparisons")
    plt.title("Operations vs n (log-log)")
    plt.grid(True, which="both", alpha=0.3)
    plt.legend(fontsize=7, ncol=2)
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS, "operations_vs_n.png"), dpi=130)
    plt.close()

    plt.figure(figsize=(8, 5))
    for a in ALGORITHMS:
        for d in DENSITIES:
            s = series(rows, a, d)
            plt.semilogx([r["n"] for r in s], [r["edge_scans"] / r["m"] for r in s], marker="o",
                         linestyle="-" if d == "sparse" else "--", label=f"{a} ({d})")
    plt.xlabel("n (vertices)")
    plt.text(1100, 2.04, "Prim and Kosaraju: exactly 2.0 (each edge seen twice)", fontsize=8)
    plt.text(1100, 1.04, "Kahn and DAG shortest path: exactly 1.0", fontsize=8)
    plt.ylabel("edge scans / m")
    plt.title("Scans per edge")
    plt.grid(True, which="both", alpha=0.3)
    plt.legend(fontsize=7, ncol=2)
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS, "scans_per_edge.png"), dpi=130)
    plt.close()

    plt.figure(figsize=(8, 5))
    for d in DENSITIES:
        s = series(rows, "Kruskal", d)
        plt.semilogx([r["n"] for r in s], [r["edge_scans"] / r["m"] for r in s], marker="o", label=f"Kruskal ({d})")
    plt.xlabel("n (vertices)")
    plt.ylabel("share of edges examined = edge scans / m")
    plt.title("Share of edges Kruskal examines")
    plt.ylim(0, 1)
    plt.grid(True, which="both", alpha=0.3)
    plt.legend()
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS, "kruskal_edge_share.png"), dpi=130)
    plt.close()

    print("Slopes of edge_scans on log-log axes (vs n):")
    for a in ALGORITHMS:
        for d in DENSITIES:
            s = series(rows, a, d)
            print(f"  {a:16s} {d:6s} {slope([r['n'] for r in s], [r['edge_scans'] for r in s]):.3f}")
    print("Slope of Kruskal comparisons (sorting, E log E):")
    for d in DENSITIES:
        s = series(rows, "Kruskal", d)
        print(f"  Kruskal          {d:6s} {slope([r['n'] for r in s], [r['comparisons'] for r in s]):.3f}")


if __name__ == "__main__":
    main()
