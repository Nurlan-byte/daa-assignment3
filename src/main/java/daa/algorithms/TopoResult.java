package daa.algorithms;

import java.util.List;

public class TopoResult {
    public final List<Integer> order;
    public final boolean hasCycle;

    public TopoResult(List<Integer> order, boolean hasCycle) {
        this.order = order;
        this.hasCycle = hasCycle;
    }
}
