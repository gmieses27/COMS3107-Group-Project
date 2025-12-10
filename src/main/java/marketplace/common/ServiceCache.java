package marketplace.common;

import java.util.List;
import java.util.Map;

public class ServiceCache {
    public List<CourseSummary> courseSummaries;
    public List<MarketComparison> marketComparisons;
    public Map<String, TextbookListing> cheapestListings;
    public FairnessResult fairnessResult;
    public Map<TextbookListing, List<TextbookListing>> barterMatches;
    public List<DemandEntry> fullDemandIndex;

        // Constructor (optional, fields default to null anyway)
    public ServiceCache() {}
}

