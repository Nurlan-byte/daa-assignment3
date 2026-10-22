package daa.algorithms;

public class SccResult {
    public final int count;
    public final int[] componentOf;

    public SccResult(int count, int[] componentOf) {
        this.count = count;
        this.componentOf = componentOf;
    }
}
