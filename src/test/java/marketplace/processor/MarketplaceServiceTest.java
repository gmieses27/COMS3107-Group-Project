package marketplace.processor;

import marketplace.common.*;
import marketplace.data.CSVRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarketplaceServiceTest {
    //repo instance to use for test
    @Mock
    CSVRepository repo;

    private MarketplaceService service;

    @BeforeEach
    void setup() {
        // We do NOT call CSVRepository.configure()
        // We construct the service passing the MOCK
        service = new MarketplaceService(repo);
    }


    //CSVRepository repo = CSVRepository.getInstance();
    @Test
    void testConstructor_NullRepository_ThrowsException() {
        // Assert that passing null throws an IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new MarketplaceService(null);
        });

        // Optional: Check the error message
        assertEquals("Repository cannot be null", exception.getMessage());
    }

    @Test
    void testTotalListings() {
        List<TextbookListing> fakeList = new ArrayList<>();
        fakeList.add(new TextbookListing("123", "Test Book", 1, "good", 50.0, "CS101", "S1", false));

        when(repo.getListings()).thenReturn(fakeList);

        int count = service.getTotalListingsCount();

        assertEquals(1, count);
    }

    @Test
    void test_StandardStrategy_AsDefault(){
        assertTrue(service.getStrategy() instanceof StandardStrategy);
    }

    @Test
    void testSwitchStrategy(){
        service.setStrategy(new LenientStrategy());
        assertTrue(service.getStrategy() instanceof LenientStrategy);
    }



    //Logic and Math Test

    @Test
    void testCheapestListingByCondition() {
        List<TextbookListing> fakeListings = new ArrayList<>();

        fakeListings.add(new TextbookListing("111", "Expensive", 1, "Good", 50.0, "CS1", "S1", false));
        TextbookListing cheapGood = new TextbookListing("222", "Cheap", 1, "good", 10.0, "CS1", "S2", false);
        fakeListings.add(cheapGood);

        // Only one fair book, so it will win ($20.00)
        TextbookListing fairBook = new TextbookListing("333", "Fair Book", 1, "fair", 20.0, "CS1", "S3", false);
        fakeListings.add(fairBook);

        // Book with null
        TextbookListing unknownBook = new TextbookListing("444", "Mystery", 1, null, 5.0, "CS1", "S4", false);
        fakeListings.add(unknownBook);

        when(repo.getListings()).thenReturn(fakeListings);

        Map<String, TextbookListing> result = service.cheapestListingByCondition();

        assertEquals(3, result.size());

        // Check good category
        assertTrue(result.containsKey("good"));
        assertEquals(cheapGood, result.get("good"));
        assertEquals(10.0, result.get("good").getPrice());

        assertTrue(result.containsKey("fair"));
        assertEquals(fairBook, result.get("fair"));

        //Check unknown
        assertTrue(result.containsKey("unknown"));
        assertEquals(unknownBook, result.get("unknown"));
    }
    @Test
    void testAveragePriceByCourse_CalculatesCorrectly() {
        List<TextbookListing> fakeList = new ArrayList<>();

        // Expected Average = 15.0
        fakeList.add(new TextbookListing("111", "Book A", 1, "good", 10.0, "CS101", "S1", false));
        fakeList.add(new TextbookListing("222", "Book B", 1, "good", 20.0, "CS101", "S2", false));


        fakeList.add(new TextbookListing("333", "Book C", 1, "good", 30.0, "HIST200", "S3", false));

        when(repo.getListings()).thenReturn(fakeList);


        List<CourseSummary> results = service.averagePriceByCourse();

        // 2 courses
        assertEquals(2, results.size());

        // CS101 first alphabetical order
        CourseSummary cs101 = results.get(0);
        assertEquals("CS101", cs101.course);
        assertEquals(2, cs101.count);
        assertEquals(15.0, cs101.averagePrice, 0.001);

        // HIST200 second
        CourseSummary hist200 = results.get(1);
        assertEquals("HIST200", hist200.course);
        assertEquals(1, hist200.count);
        assertEquals(30.0, hist200.averagePrice, 0.001);
    }

    @Test
    void test_DemandIndex_isSorted() {
        List<TextbookListing> fakeList = new ArrayList<>();

        // ISBN "111" appears 3 times (Most Popular)
        fakeList.add(new TextbookListing("111", "Pop Book", 1, "good", 10.0, "CS1", "S1", false));
        fakeList.add(new TextbookListing("111", "Pop Book", 1, "fair", 10.0, "CS1", "S2", false));
        fakeList.add(new TextbookListing("111", "Pop Book", 1, "bad", 10.0, "CS1", "S3", false));

        // ISBN "222" appears 2 times (Medium Popularity)
        fakeList.add(new TextbookListing("222", "Avg Book", 1, "good", 20.0, "CS1", "S4", false));
        fakeList.add(new TextbookListing("222", "Avg Book", 1, "good", 20.0, "CS1", "S5", false));

        // ISBN "333" appears 1 time (Least Popular)
        fakeList.add(new TextbookListing("333", "Rare Book", 1, "good", 30.0, "CS1", "S6", false));

        when(repo.getListings()).thenReturn(fakeList);
        when(repo.getMetadata()).thenReturn(new HashMap<>());

        List<DemandEntry> results = service.demandIndex(3);

        assertEquals(3, results.size());


        assertEquals("111", results.get(0).isbn);
        assertEquals(3, results.get(0).listings);

        assertEquals("222", results.get(1).isbn);
        assertEquals(2, results.get(1).listings);


        assertEquals("333", results.get(2).isbn);
        assertEquals(1, results.get(2).listings);
    }



    @Test
    void testStudentVsMarketComparison_TitleDefaultsToUnknown() {
        List<TextbookListing> fakeListings = new ArrayList<>();
        // ISBN "999-9" will normalize to "9999"
        fakeListings.add(new TextbookListing("999-9", "Some Raw Title", 1, "good", 50.0, "CS101", "S1", false));

        // ISBN is not found in book_metadata.csv
        Map<String, String[]> emptyMetadata = new HashMap<>();

        when(repo.getListings()).thenReturn(fakeListings);
        when(repo.getMetadata()).thenReturn(emptyMetadata);
        when(repo.getMarket()).thenReturn(new HashMap<>());


        List<MarketComparison> results = service.studentVsMarketComparison();

        assertEquals(1, results.size());

        MarketComparison result = results.get(0);
        assertEquals("9999", result.isbn);

    //uses official metadata titles, falling back to unknown if missing
        assertEquals("(unknown)", result.title);
    }


    @Test
    void testStudentVsMarketComparison_IntegrationLogic() {
        List<TextbookListing> fakeListings = new ArrayList<>();

        fakeListings.add(new TextbookListing("978-1", "Raw Title 1", 1, "good", 10.0, "CS101", "S1", false));
        fakeListings.add(new TextbookListing("978-1", "Raw Title 1", 1, "fair", 30.0, "CS101", "S2", false));


        Map<String, Double> fakeMarket = new HashMap<>();
        fakeMarket.put("9781", 100.00);

        Map<String, String[]> fakeMeta = new HashMap<>();
        fakeMeta.put("9781", new String[]{"Java Programming", "Author Name"});

        when(repo.getListings()).thenReturn(fakeListings);
        when(repo.getMarket()).thenReturn(fakeMarket);
        when(repo.getMetadata()).thenReturn(fakeMeta);

        List<MarketComparison> results = service.studentVsMarketComparison();

        assertEquals(1, results.size());

        // "9781" < "9782" (Sorting check)
        MarketComparison bookA = results.get(0);
        assertEquals("9781", bookA.isbn);
        assertEquals("Java Programming", bookA.title);
        assertEquals(20.0, bookA.studentAvg, 0.001);
        assertEquals(100.0, bookA.marketAvg, 0.001);
        assertEquals(2, bookA.count);
    }

    @Test
    void testMarketFairnessScore_StandardStrategy_Default() {
        List<TextbookListing> fakeListings = new ArrayList<>();

       //PASS
        fakeListings.add(new TextbookListing("111", "Fair Book", 1, "good", 105.0, "CS1", "S1", false));

        //FAIL
        fakeListings.add(new TextbookListing("222", "Unfair Book", 1, "good", 115.0, "CS1", "S2", false));


        Map<String, Double> fakeMarket = new HashMap<>();
        fakeMarket.put("111", 100.0);
        fakeMarket.put("222", 100.0);

        when(repo.getListings()).thenReturn(fakeListings);
        when(repo.getMarket()).thenReturn(fakeMarket);

        FairnessResult result = service.marketFairnessScore();

        // Total 2 books. 1 passed (Book A)
        assertEquals(2, result.totalComparable);
        assertEquals(1, result.within10);
        assertEquals(50.0, result.percentWithin10, 0.001);
    }

    @Test
    void testMarketFairnessScore_SwitchToLenientStrategy() {
        List<TextbookListing> fakeListings = new ArrayList<>();

        fakeListings.add(new TextbookListing("111", "Fair Book", 1, "good", 105.0, "CS1", "S1", false));

        fakeListings.add(new TextbookListing("222", "Borderline Book", 1, "good", 115.0, "CS1", "S2", false));

        Map<String, Double> fakeMarket = new HashMap<>();
        fakeMarket.put("111", 100.0);
        fakeMarket.put("222", 100.0);

        when(repo.getListings()).thenReturn(fakeListings);
        when(repo.getMarket()).thenReturn(fakeMarket);

        service.setStrategy(new LenientStrategy());

        FairnessResult result = service.marketFairnessScore();


        // 15% is valid under Lenient (20%).
        assertEquals(2, result.totalComparable);
        assertEquals(2, result.within10);
        assertEquals(100.0, result.percentWithin10, 0.001);
    }

    @Test
    void testBarter_IsbnMatchPriority_AndSorting() {
        List<TextbookListing> fakeList = new ArrayList<>();

        //Seller S1 wants to barter Book A ($50.00)
        TextbookListing target = new TextbookListing("111", "Book A", 1, "good", 50.0, "CS101", "S1", true);
        fakeList.add(target);

        //Perfect ISBN match, Price difference $2.00
        fakeList.add(new TextbookListing("111", "Book A", 1, "fair", 52.0, "CS101", "S2", true));

        // Perfect ISBN match, Price difference $10.00 (Should be sorted AFTER candidate 1)
        fakeList.add(new TextbookListing("111", "Book A", 1, "new", 60.0, "CS101", "S3", true));

        // Same Course, Different ISBN (Should be IGNORED because ISBN matches exist)
        fakeList.add(new TextbookListing("999", "Other Book", 1, "good", 50.0, "CS101", "S4", true));

        //Same Seller (S1) (Should be IGNORED - no self-barter)
        fakeList.add(new TextbookListing("111", "Book A", 1, "bad", 40.0, "CS101", "S1", true));

        when(repo.getListings()).thenReturn(fakeList);

        Map<TextbookListing, List<TextbookListing>> results = service.barterCompatibilityFinder();

        assertTrue(results.containsKey(target));
        List<TextbookListing> matches = results.get(target);

        //Should only have 2 matches non-idnetical isbn ignored
        assertEquals(2, matches.size());

        // Sorting (S2 is $52 [diff 2], S3 is $60 [diff 10]. S2 comes first.)
        assertEquals("S2", matches.get(0).getSellerId());
        assertEquals("S3", matches.get(1).getSellerId());

        //(S1 should not appear in their own list)
        boolean selfMatchFound = matches.stream().anyMatch(m -> m.getSellerId().equals("S1"));
        assertFalse(selfMatchFound);
    }

    @Test
    void testBarter_IgnoresNonBarterListings() {
        List<TextbookListing> fakeList = new ArrayList<>();

        // no barter
        TextbookListing shySeller = new TextbookListing("111", "Book A", 1, "good", 50.0, "CS1", "S1", false);
        fakeList.add(shySeller);

        //Accepts barter (Potential match)
        fakeList.add(new TextbookListing("111", "Book A", 1, "good", 50.0, "CS1", "S2", true));

        when(repo.getListings()).thenReturn(fakeList);

        Map<TextbookListing, List<TextbookListing>> results = service.barterCompatibilityFinder();

        //shySeller not in key because no barter
        assertFalse(results.containsKey(shySeller), "Listings with barter=false should not generate match lists");
    }

    @Test
    void testBarter_FallbackToCourseMatch() {
        List<TextbookListing> fakeList = new ArrayList<>();

        TextbookListing target = new TextbookListing("111", "Unique Book", 1, "good", 30.0, "HIST200", "S1", true);
        fakeList.add(target);

        // CANDIDATE: Different ISBN ("222"), SAME Course ("HIST200")
        // Since no one else has ISBN "111", this Course match SHOULD be valid.
        TextbookListing courseMatch = new TextbookListing("222", "Other History Book", 1, "good", 35.0, "HIST200", "S2", true);
        fakeList.add(courseMatch);

        when(repo.getListings()).thenReturn(fakeList);

        Map<TextbookListing, List<TextbookListing>> results = service.barterCompatibilityFinder();

        List<TextbookListing> matches = results.get(target);
        assertEquals(1, matches.size());
        assertEquals("S2", matches.get(0).getSellerId());
    }



}

