package marketplace.ui;


import marketplace.common.*;
// Assuming FairnessResult is still inside Service or moved to common
// If you moved FairnessResult to common, import marketplace.common.FairnessResult;

import java.util.List;
import java.util.Map;

public class MarketplacePrinter {

    public void printWelcome() {
        System.out.println("\n===== TEXTBOOK MARKETPLACE =====");
    }

    public void printMenu() {
        System.out.println("\n1. Total Listings Count");
        System.out.println("2. Average Asking Price by Course");
        System.out.println("3. Student vs. Market Price Comparison");
        System.out.println("4. Cheapest Listing by Condition");
        System.out.println("5. Market Fairness Score");
        System.out.println("6. Barter Compatibility Finder");
        System.out.println("7. Book Demand Index");
        System.out.println("8. Switch Fairness Mode");
        System.out.println("0. Exit");
    }

    public void printTotalCount(int count) {
        System.out.println("\n>> Total Listings Count");
        System.out.println("-------------------------");
        System.out.println("Total valid textbook listings: " + count);
    }

    public void printCourseSummaries(List<CourseSummary> summaries) {
        System.out.println("\n>> Average Asking Price by Course");
        System.out.println("---------------------------------");
        if (summaries.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        for (CourseSummary s : summaries) {
            System.out.printf("%s: average=$%.2f (count=%d)%n", s.course, s.averagePrice, s.count);
        }
    }

    public void printMarketComparison(List<MarketComparison> comps) {
        System.out.println("\n>> Student vs Market Price Comparison");
        System.out.println("-------------------------------------");
        if (comps.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        for (MarketComparison c : comps) {
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

    public void printCheapestByCondition(Map<String, TextbookListing> best) {
        System.out.println("\n>> Cheapest Listing By Condition");
        System.out.println("--------------------------------");
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

    public void printFairnessScore(FairnessResult res) {
        System.out.println("\n>> Market Fairness Score");
        System.out.println("-------------------------");
        if (res.totalComparable == 0) {
            System.out.println("No listings with matching market data.");
            return;
        }
        System.out.printf("Listings within +/-%% of market: %d/%d => fairness=%.1f%% %n", res.within10, res.totalComparable, res.percentWithin10);
    }

    public void printBarterMatches(Map<TextbookListing, List<TextbookListing>> matches) {
        System.out.println("\n>> Barter Compatibility Finder");
        System.out.println("--------------------------------");
        if (matches.isEmpty()) {
            System.out.println("No barter results found.");
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

    public void printDemandIndex(List<DemandEntry> top) {
        System.out.println("\n>> Book Demand Index (by number of listings)");
        System.out.println("--------------------------------------------");
        if (top.isEmpty()) {
            System.out.println("No listings available.");
            return;
        }
        int idx = 1;
        for (DemandEntry e : top) {
            System.out.printf("%d) %s | %s -> listings=%d%n", idx++, e.isbn, e.title, e.listings);
        }
    }

    public void printFairnessOptions() {
        System.out.println("\n--- Configure Fairness Strategy ---");
        System.out.println("1) Standard (Within 10% of market price)");
        System.out.println("2) Lenient (Within 20% of market price)");
        System.out.print("Select an option: ");
    }

    public void printStrategyUpdate(String strategyName) {
        System.out.println(">> Success: Fairness strategy updated to '" + strategyName);
    }
    public void printError(String msg) {
        System.err.println("Error: " + msg);
    }
}
