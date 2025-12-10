package marketplace.processor;
import marketplace.common.*;
import marketplace.data.CSVRepository;

import java.util.*;

/**
 * Business/service layer for the Textbook Marketplace app.
 * Holds the domain data and implements the core operations.
 */
public class MarketplaceService {

    private final CSVRepository repo;
    private final ServiceCache cache;
    private FairnessStrategy fs = new StandardStrategy();

    public MarketplaceService(CSVRepository repo) {
        this.repo = repo;
        this.cache = new ServiceCache();
    }

    public int getTotalListingsCount() {
        return repo.getListings().size();
    }

    //gets the average price by course
    public List<CourseSummary> averagePriceByCourse() {
        //memoization
        if(cache.courseSummaries != null){
            return cache.courseSummaries;
        }
        Map<String, Double> sum = new HashMap<>();
        Map<String, Integer> cnt = new HashMap<>();
        for (TextbookListing t : repo.getListings()) {
            String course = (t.getCourseNumber() == null || t.getCourseNumber().isEmpty()) ? "<unknown>" : t.getCourseNumber();
            sum.put(course, sum.getOrDefault(course, 0.0) + t.getPrice());
            cnt.put(course, cnt.getOrDefault(course, 0) + 1);
        }

        List<CourseSummary> results = new ArrayList<>();
        for (String course : sum.keySet()) {
            double avg = sum.get(course) / cnt.get(course);
            results.add(new CourseSummary(course, avg, cnt.get(course)));
        }
        results.sort(Comparator.comparing((CourseSummary s) -> s.course));
        cache.courseSummaries = results;
        return results;
    }

    public List<MarketComparison> studentVsMarketComparison() {
        if(cache.marketComparisons != null){
            return cache.marketComparisons;
        }
        Map<String, Double> sum = new HashMap<>();
        Map<String, Integer> cnt = new HashMap<>();
        for (TextbookListing t : repo.getListings()) {
            String isbn = normalizeIsbn(t.getIsbn());
            sum.put(isbn, sum.getOrDefault(isbn, 0.0) + t.getPrice());
            cnt.put(isbn, cnt.getOrDefault(isbn, 0) + 1);
        }

        List<MarketComparison> out = new ArrayList<>();
        for (String isbn : sum.keySet()) {
            double avgStudent = sum.get(isbn) / cnt.get(isbn);
            Double marketAvg = repo.getMarket().get(isbn);
            String title = "(unknown)";
            if (repo.getMetadata().containsKey(isbn)) {
                String[] m = repo.getMetadata().get(isbn);
                if (m != null && m.length > 0 && !m[0].isEmpty()) title = m[0];
            }
            out.add(new MarketComparison(isbn, title, avgStudent, marketAvg, cnt.get(isbn)));
        }
        out.sort(Comparator.comparing((MarketComparison m) -> m.isbn));
        cache.marketComparisons = out;
        return out;
    }

    public Map<String, TextbookListing> cheapestListingByCondition() {
        if(cache.cheapestListings != null){
            return cache.cheapestListings;
        }
        Map<String, TextbookListing> best = new HashMap<>();
        for (TextbookListing t : repo.getListings()) {
            String cond = (t.getCondition() == null) ? "unknown" : t.getCondition().toLowerCase();
            if (!best.containsKey(cond) || t.getPrice() < best.get(cond).getPrice()) {
                best.put(cond, t);
            }
        }
        cache.cheapestListings = best;
        return best;
    }


    public void setStrategy(FairnessStrategy fs){
        this.fs = fs;
        cache.fairnessResult = null;
    }
    public FairnessResult marketFairnessScore() {
        if(cache.fairnessResult != null){
            return cache.fairnessResult;
        }
        int totalComparable = 0;
        int within = 0;
        for (TextbookListing t : repo.getListings()) {
            String isbn = normalizeIsbn(t.getIsbn());
            Double m = repo.getMarket().get(isbn);
            if (m == null || m == 0) continue;
            totalComparable++;
            if (fs.isFair(t.getPrice(), m)) within++;
        }
        double score = (totalComparable == 0) ? 0.0 : ((within * 100.0) / totalComparable);
        FairnessResult ans = new FairnessResult(totalComparable, within, score);
        cache.fairnessResult = ans;
        return ans;
    }


    public Map<TextbookListing, List<TextbookListing>> barterCompatibilityFinder() {
        if(cache.barterMatches != null){
            return cache.barterMatches;
        }
        Map<String, List<TextbookListing>> byIsbn = new HashMap<>();
        Map<String, List<TextbookListing>> byCourse = new HashMap<>();
        for (TextbookListing t : repo.getListings()) {
            String isbn = normalizeIsbn(t.getIsbn());
            byIsbn.computeIfAbsent(isbn, k -> new ArrayList<>()).add(t);
            String course = (t.getCourseNumber() == null) ? "" : t.getCourseNumber();
            byCourse.computeIfAbsent(course, k -> new ArrayList<>()).add(t);
        }

        Map<TextbookListing, List<TextbookListing>> result = new LinkedHashMap<>();
        for (TextbookListing t : repo.getListings()) {
            if (!t.isAcceptsBarter()) continue;
            String isbn = normalizeIsbn(t.getIsbn());
            List<TextbookListing> candidates = new ArrayList<>();
            if (byIsbn.containsKey(isbn)) {
                for (TextbookListing c : byIsbn.get(isbn)) if (!c.getSellerId().equals(t.getSellerId())) candidates.add(c);
            }
            if (candidates.isEmpty() && byCourse.containsKey(t.getCourseNumber())) {
                for (TextbookListing c : byCourse.get(t.getCourseNumber())) if (!c.getSellerId().equals(t.getSellerId())) candidates.add(c);
            }
            candidates.sort((a,b) -> Double.compare(Math.abs(a.getPrice()-t.getPrice()), Math.abs(b.getPrice()-t.getPrice())));
            result.put(t, candidates);
        }

        cache.barterMatches = result;
        return result;
    }

    public List<DemandEntry> demandIndex(int topN) {
        if(cache.fullDemandIndex == null) {
            Map<String, Integer> counts = new HashMap<>();
            for (TextbookListing t : repo.getListings()) {
                String isbn = normalizeIsbn(t.getIsbn());
                counts.put(isbn, counts.getOrDefault(isbn, 0) + 1);
            }

            List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
            entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            List<DemandEntry> out = new ArrayList<>();
            for (Map.Entry<String, Integer> e : entries) {
                String isbn = e.getKey();
                int c = e.getValue();
                String title = "(unknown)";
                if (repo.getMetadata().containsKey(isbn)) {
                    String[] m = repo.getMetadata().get(isbn);
                    if (m != null && m.length > 0 && !m[0].isEmpty()) title = m[0];
                }
                out.add(new DemandEntry(isbn, title, c));
            }
            cache.fullDemandIndex = out;
        }
        int limit = Math.min(topN, cache.fullDemandIndex.size());
        return cache.fullDemandIndex.subList(0, limit);

    }

    private String normalizeIsbn(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[^0-9Xx]", "").toUpperCase();
    }
}
