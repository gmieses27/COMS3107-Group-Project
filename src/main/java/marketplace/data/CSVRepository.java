package marketplace.data;

import marketplace.common.TextbookListing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CSVRepository {

    //implementing singelton design pattern

    private static CSVRepository repo = null;

    private List<TextbookListing> listings;
    private Map<String, String[]> metadata;
    private Map<String, Double> market;

    private CSVRepository(String listPath, String metaPath, String marketPath){
        //runs loading in parallel using threads
        ExecutorService es = Executors.newFixedThreadPool(3);

        //runs the following in parallel using futures and

        Future<List<TextbookListing>> fListings = es.submit(() -> loadListings(listPath));
        Future<Map<String, String[]>> fMetadata = es.submit(() -> loadMetadata(metaPath));
        Future<Map<String, Double>> fMarket = es.submit(() -> loadMarket(marketPath));

        try{
            this.listings = fListings.get();
            this.metadata = fMetadata.get();
            this.market = fMarket.get();
        }
        catch(Exception e){
            throw new RuntimeException("CRITICAL: Failed to initialize data repository", e);
        }
        finally {
            es.shutdown();
        }
    }

    public static CSVRepository getInstance(){
        if(repo == null){
            //defensive programming
            throw new IllegalStateException("Repository not initialized! Call configure() first.");
        }
        return repo;
    }

    public static void configure(String listingPath, String metaPath, String marketPath) {
        if (repo == null) {
            repo = new CSVRepository(listingPath, metaPath, marketPath);
        }
    }

    private List<TextbookListing> loadListings(String path) {
        try {
            return CSVFileReader.readListings(path);
        } catch (IOException e) {
            System.err.println("Error loading listings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, String[]> loadMetadata(String path) {
        try {
            return CSVFileReader.readBookMetadata(path);
        } catch (IOException e) {
            System.err.println("Error loading metadata: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Double> loadMarket(String path) {
        try {
            return CSVFileReader.readMarketPrices(path);
        } catch (IOException e) {
            System.err.println("Error loading market prices: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public List<TextbookListing> getListings() {
        return listings;
    }

    public Map<String, Double> getMarket() {
        return market;
    }

    public Map<String, String[]> getMetadata() {
        return metadata;
    }
}
