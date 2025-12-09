package marketplace.processor;
import marketplace.common.*;

import java.util.*;

/**
 * Business/service layer for the Textbook Marketplace app.
 * Holds the domain data and implements the core operations.
 */
public class MarketplaceService {
    private final List<TextbookListing> listings;
    private final Map<String, String[]> metadata;
    private final Map<String, Double> market;

    public MarketplaceService(List<TextbookListing> listings, Map<String, String[]> metadata, Map<String, Double> market) {
        this.listings = (listings == null) ? Collections.emptyList() : listings;
        this.metadata = (metadata == null) ? Collections.emptyMap() : metadata;
        this.market = (market == null) ? Collections.emptyMap() : market;
    }

    public int getTotalListingsCount() {
        return listings.size();
    }

    //gets the average price by course
    public List<CourseSummary> averagePriceByCourse() {
        Map<String, Double> sum = new HashMap<>();
        Map<String, Integer> cnt = new HashMap<>();
        for (TextbookListing t : listings) {
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
        return results;
    }

    public List<MarketComparison> studentVsMarketComparison() {
        Map<String, Double> sum = new HashMap<>();
        Map<String, Integer> cnt = new HashMap<>();
        for (TextbookListing t : listings) {
            String isbn = normalizeIsbn(t.getIsbn());
            sum.put(isbn, sum.getOrDefault(isbn, 0.0) + t.getPrice());
            cnt.put(isbn, cnt.getOrDefault(isbn, 0) + 1);
        }

        List<MarketComparison> out = new ArrayList<>();
        for (String isbn : sum.keySet()) {
            double avgStudent = sum.get(isbn) / cnt.get(isbn);
            Double marketAvg = market.get(isbn);
            String title = "(unknown)";
            if (metadata.containsKey(isbn)) {
                String[] m = metadata.get(isbn);
                if (m != null && m.length > 0 && !m[0].isEmpty()) title = m[0];
            }
            out.add(new MarketComparison(isbn, title, avgStudent, marketAvg, cnt.get(isbn)));
        }
        out.sort(Comparator.comparing((MarketComparison m) -> m.isbn));
        return out;
    }

    public Map<String, TextbookListing> cheapestListingByCondition() {
        Map<String, TextbookListing> best = new HashMap<>();
        for (TextbookListing t : listings) {
            String cond = (t.getCondition() == null) ? "unknown" : t.getCondition().toLowerCase();
            if (!best.containsKey(cond) || t.getPrice() < best.get(cond).getPrice()) {
                best.put(cond, t);
            }
        }
        return best;
    }

    public FairnessResult marketFairnessScore() {
        int totalComparable = 0;
        int within10 = 0;
        for (TextbookListing t : listings) {
            String isbn = normalizeIsbn(t.getIsbn());
            Double m = market.get(isbn);
            if (m == null || m == 0) continue;
            totalComparable++;
            double rel = Math.abs(t.getPrice() - m) / m;
            if (rel <= 0.10) within10++;
        }
        double score = (totalComparable == 0) ? 0.0 : ((within10 * 100.0) / totalComparable);
        return new FairnessResult(totalComparable, within10, score);
    }

    public Map<TextbookListing, List<TextbookListing>> barterCompatibilityFinder() {
        Map<String, List<TextbookListing>> byIsbn = new HashMap<>();
        Map<String, List<TextbookListing>> byCourse = new HashMap<>();
        for (TextbookListing t : listings) {
            String isbn = normalizeIsbn(t.getIsbn());
            byIsbn.computeIfAbsent(isbn, k -> new ArrayList<>()).add(t);
            String course = (t.getCourseNumber() == null) ? "" : t.getCourseNumber();
            byCourse.computeIfAbsent(course, k -> new ArrayList<>()).add(t);
        }

        Map<TextbookListing, List<TextbookListing>> result = new LinkedHashMap<>();
        for (TextbookListing t : listings) {
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

        return result;
    }

    public List<DemandEntry> demandIndex(int topN) {
        Map<String, Integer> counts = new HashMap<>();
        for (TextbookListing t : listings) {
            String isbn = normalizeIsbn(t.getIsbn());
            counts.put(isbn, counts.getOrDefault(isbn, 0) + 1);
        }

        List<Map.Entry<String,Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));

        int limit = Math.min(topN, entries.size());
        List<DemandEntry> out = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            Map.Entry<String,Integer> e = entries.get(i);
            String isbn = e.getKey();
            int c = e.getValue();
            String title = "(unknown)";
            if (metadata.containsKey(isbn)) {
                String[] m = metadata.get(isbn);
                if (m != null && m.length > 0 && !m[0].isEmpty()) title = m[0];
            }
            out.add(new DemandEntry(isbn, title, c));
        }
        return out;
    }

    private String normalizeIsbn(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[^0-9Xx]", "").toUpperCase();
    }
}
