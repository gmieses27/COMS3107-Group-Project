package marketplace.common;

public class DemandEntry {
    public final String isbn;
    public final String title;
    public final int listings;

    public DemandEntry(String isbn, String title, int listings) {
        this.isbn = isbn;
        this.title = title;
        this.listings = listings;
    }
}
