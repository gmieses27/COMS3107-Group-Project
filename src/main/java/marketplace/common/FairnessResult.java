package marketplace.common;

public class FairnessResult {
    public final int totalComparable;
    public final int within10;
    public final double percentWithin10;

    public FairnessResult(int totalComparable, int within10, double percentWithin10) {
        this.totalComparable = totalComparable;
        this.within10 = within10;
        this.percentWithin10 = percentWithin10;
    }
}
