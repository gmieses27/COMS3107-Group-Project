package main;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static List<TextbookListing> listings;
    private static Map<String, String[]> metadata;
    private static Map<String, Double> market = new HashMap<>();
    private static MarketplaceService service;

    public static void main(String[] args) {
        try {
            listings = CSVFileReader.readListings("data/student_listings.csv");
        } catch (IOException e) {
            System.err.println("Failed to load student listings: " + e.getMessage());
            listings = List.of();
        }

        try {
            metadata = CSVFileReader.readBookMetadata("data/book_metadata.csv");
        } catch (IOException e) {
            System.err.println("Failed to load book metadata: " + e.getMessage());
            metadata = Map.of();
        }

        try {
            market = CSVFileReader.readMarketPrices("data/market_prices.csv");
        } catch (IOException e) {
            System.err.println("Failed to load market prices: " + e.getMessage());
            market = Map.of();
        }

        // Instantiate service layer (3-tier separation)
        service = new MarketplaceService(listings, metadata, market);
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Select an option (0 to exit): ");
                String line = scanner.nextLine().trim();
                int choice;
                try {
                    choice = Integer.parseInt(line);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input. Please enter a number between 0 and 7.");
                    continue;
                }

                switch (choice) {
                    case 0:
                        running = false;
                        System.out.println("Exiting. Goodbye.");
                        break;
                    case 1:
                        totalListingsCount();
                        break;
                    case 2:
                            averagePriceByCourse();
                        break;
                    case 3:
                        studentVsMarketComparison();
                        break;
                    case 4:
                        cheapestListingByCondition();
                        break;
                    case 5:
                        marketFairnessScore();
                        break;
                    case 6:
                        barterCompatibilityFinder();
                        break;
                    case 7:
                        demandIndex();
                        break;
                    default:
                        System.out.println("Unknown option. Please choose 0-7.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== TEXTBOOK MARKETPLACE =====");
        System.out.println("1. Total Listings Count");
        System.out.println("2. Average Asking Price by Course");
        System.out.println("3. Student vs. Market Price Comparison");
        System.out.println("4. Cheapest Listing by Condition");
        System.out.println("5. Market Fairness Score");
        System.out.println("6. Barter Compatibility Finder");
        System.out.println("7. Book Demand Index");
        System.out.println("0. Exit");
    }

    private static void totalListingsCount() {
        int count = service.getTotalListingsCount();
        System.out.println("\n>> Total Listings Count");
        System.out.println("-------------------------");
        System.out.println("Total valid textbook listings: " + count);
    }

    private static void averagePriceByCourse() {
        System.out.println("\n>> Average Asking Price by Course");
        System.out.println("---------------------------------");
        List<MarketplaceService.CourseSummary> summaries = service.averagePriceByCourse();
        if (summaries.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        for (MarketplaceService.CourseSummary s : summaries) {
            System.out.printf("%s: average=$%.2f (count=%d)%n", s.course, s.averagePrice, s.count);
        }
    }

    private static void studentVsMarketComparison() {
        System.out.println("\n>> Student vs Market Price Comparison");
        System.out.println("-------------------------------------");
        List<MarketplaceService.MarketComparison> comps = service.studentVsMarketComparison();
        if (comps.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        for (MarketplaceService.MarketComparison c : comps) {
            if (c.marketAvg == null) {
                System.out.printf("ISBN %s | %s : student-avg=$%.2f (n=%d) | market: N/A%n", c.isbn, c.title, c.studentAvg, c.count);
            } else {
                double diff = c.studentAvg - c.marketAvg;
                double pct = (c.marketAvg == 0) ? 0.0 : (diff / c.marketAvg) * 100.0;
                System.out.printf("ISBN %s | %s : student-avg=$%.2f (n=%d) | market-avg=$%.2f | diff=$%.2f (%.1f%%)%n",
                        c.isbn, c.title, c.studentAvg, c.count, c.marketAvg, diff, pct);
            }
        }
    }

    private static void cheapestListingByCondition() {
        System.out.println("\n>> Cheapest Listing By Condition");
        System.out.println("--------------------------------");
        Map<String, TextbookListing> best = service.cheapestListingByCondition();
        if (best.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        for (String cond : best.keySet()) {
            TextbookListing b = best.get(cond);
            System.out.printf("Condition: %s -> %s | $%.2f | seller=%s | course=%s%n",
                    cond, b.getTitle(), b.getPrice(), b.getSellerId(), b.getCourseNumber());
        }
    }

    private static void marketFairnessScore() {
        System.out.println("\n>> Market Fairness Score");
        System.out.println("-------------------------");
        MarketplaceService.FairnessResult res = service.marketFairnessScore();
        if (res.totalComparable == 0) {
            System.out.println("No listings with matching market data.");
            return;
        }
        System.out.printf("Listings within +/-10%% of market: %d/%d => fairness=%.1f%% %n", res.within10, res.totalComparable, res.percentWithin10);
    }

    private static void barterCompatibilityFinder() {
        System.out.println("\n>> Barter Compatibility Finder");
        System.out.println("--------------------------------");
        Map<TextbookListing, List<TextbookListing>> matches = service.barterCompatibilityFinder();
        if (matches.isEmpty()) {
            System.out.println("No barter results found (no listings willing to barter or no candidates).");
            return;
        }
        for (TextbookListing key : matches.keySet()) {
            System.out.printf("\nListing willing to barter: %s | $%.2f | seller=%s | isbn=%s\n",
                    key.getTitle(), key.getPrice(), key.getSellerId(), key.getIsbn());
            List<TextbookListing> candidates = matches.get(key);
            if (candidates.isEmpty()) {
                System.out.println("  No immediate barter matches found.");
                continue;
            }
            int shown = 0;
            for (TextbookListing c : candidates) {
                if (shown++ >= 5) break;
                System.out.printf("  Candidate: %s | $%.2f | seller=%s | condition=%s\n",
                        c.getTitle(), c.getPrice(), c.getSellerId(), c.getCondition());
            }
        }
    }

    private static void demandIndex() {
        System.out.println("\n>> Book Demand Index (by number of listings)");
        System.out.println("--------------------------------------------");
        List<MarketplaceService.DemandEntry> top = service.demandIndex(10);
        if (top.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        int idx = 1;
        for (MarketplaceService.DemandEntry e : top) {
            System.out.printf("%d) %s | %s -> listings=%d%n", idx++, e.isbn, e.title, e.listings);
        }
    }

    // Note: ISBN normalization and business logic are handled in MarketplaceService.
}

