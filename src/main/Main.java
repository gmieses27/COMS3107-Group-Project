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
        System.out.println("\n===== TEXTBOOK MARKETPLACE - MIDPOINT MENU =====");
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
        int count = (listings == null) ? 0 : listings.size();
        System.out.println("\n>> Total Listings Count");
        System.out.println("-------------------------");
        System.out.println("Total valid textbook listings: " + count);
    }

    private static void averagePriceByCourse() {
        System.out.println("\nImplementation TODO");
        //TODO
    }

    private static void studentVsMarketComparison() {
        System.out.println("\nImplementation TODO");
        //TODO
    }

    private static void cheapestListingByCondition() {
        System.out.println("\nImplementation TODO");
        //TODO
    }

    private static void marketFairnessScore() {
        System.out.println("\nImplementation TODO");
        //TODO
    }

    private static void barterCompatibilityFinder() {
        System.out.println("\nImplementation TODO");
        //TODO
    }

    private static void demandIndex() {
        System.out.println("\nImplementation TODO");
        //TODO
    }
}

