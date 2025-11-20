package main;

public class TextbookListing {
    private String isbn;
    private String title;
    private int edition;
    private String condition;
    private double price;
    private String courseNumber;
    private String sellerId;
    private boolean acceptsBarter;

    public TextbookListing(String isbn, String title, int edition, String condition,
                          double price, String courseNumber, String sellerId, boolean acceptsBarter) {
        this.isbn = isbn;
        this.title = title;
        this.edition = edition;
        this.condition = condition;
        this.price = price;
        this.courseNumber = courseNumber;
        this.sellerId = sellerId;
        this.acceptsBarter = acceptsBarter;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public int getEdition() { return edition; }
    public String getCondition() { return condition; }
    public double getPrice() { return price; }
    public String getCourseNumber() { return courseNumber; }
    public String getSellerId() { return sellerId; }
    public boolean isAcceptsBarter() { return acceptsBarter; }

    @Override
    public String toString() {
        return String.format("Listing{isbn='%s', title='%s', price=$%.2f, condition='%s'}",
                isbn, title, price, condition);
    }
}
