package marketplace;


import marketplace.common.*;
import marketplace.data.*;
import marketplace.processor.*;
import marketplace.ui.*;

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

        MarketplacePrinter printer = new MarketplacePrinter();
        ConsoleMenu menu = new ConsoleMenu(service, printer);

        menu.start();
    }
    // Note: ISBN normalization and business logic are handled in MarketplaceService.
}

