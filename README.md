# Textbook Marketplace Application

## Three Data Sources
1. **Student Listings CSV** - Student textbook submissions
2. **Market Price Data CSV** - External pricing reference
3. **Book Metadata CSV** - Validated book information

## Five Core Operations
1. Total Listings Count
2. Average Price by Course
3. Student vs. Market Price Comparison (Multi-source)
4. Cheapest by Condition (Multi-source)
5. Market Fairness Score (Multi-source)

## Project Structure
```
data/
├── student_listings.csv
├── market_prices.csv
└── book_metadata.csv

src/main/java/com/textbookmarketplace/
├── Main.java
├── model/
│   └── TextbookListing.java
└── service/
    └── CSVFileReader.java
```