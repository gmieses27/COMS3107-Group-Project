package marketplace.common;

public class MarketComparison {
    public final String isbn;
    public final String title;
    public final double studentAvg;
    public final Double marketAvg;
    public final int count;

    public MarketComparison(String isbn, String title, double studentAvg, Double marketAvg, int count) {
        this.isbn = isbn;
        this.title = title;
        this.studentAvg = studentAvg;
        this.marketAvg = marketAvg;
        this.count = count;
    }
}

