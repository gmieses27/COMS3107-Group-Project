package main;

/**
 * Small smoke test runner that compiles & runs in CI to exercise the three-tier code.
 * It performs lightweight checks and exits non-zero if expectations are not met.
 */
public class TestRunner {
    public static void main(String[] args) {
        try {
            var listings = CSVFileReader.readListings("data/student_listings.csv");
            var metadata = CSVFileReader.readBookMetadata("data/book_metadata.csv");
            var market = CSVFileReader.readMarketPrices("data/market_prices.csv");

            MarketplaceService svc = new MarketplaceService(listings, metadata, market);

            // smoke checks based on provided sample data
            int total = svc.getTotalListingsCount();
            System.out.println("Total listings -> " + total);
            if (total != 3) {
                System.err.println("Unexpected total listings (expected 3 for sample data)");
                System.exit(2);
            }

            var courseSummaries = svc.averagePriceByCourse();
            System.out.println("Course summaries: " + courseSummaries.size());

            var comps = svc.studentVsMarketComparison();
            System.out.println("Market comparisons: " + comps.size());

            var cheapest = svc.cheapestListingByCondition();
            System.out.println("Cheapest by condition: " + cheapest.size());

            var fairness = svc.marketFairnessScore();
            System.out.println(String.format("Fairness: %d/%d within 10%% -> %.1f%%", fairness.within10, fairness.totalComparable, fairness.percentWithin10));

            var barter = svc.barterCompatibilityFinder();
            System.out.println("Barter candidates: " + barter.size());

            var demand = svc.demandIndex(10);
            System.out.println("Demand topN: " + demand.size());

            System.out.println("SMOKE TEST PASSED");
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("SMOKE TEST FAILED");
            System.exit(1);
        }
    }
}
