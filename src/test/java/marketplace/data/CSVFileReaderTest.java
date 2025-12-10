package marketplace.data;

import marketplace.common.TextbookListing;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

public class CSVFileReaderTest {

    @Test
    void testReadListings() throws IOException {
        List< TextbookListing> listings = CSVFileReader.readListings("src/test/java/resources/test-listings.csv");

        assertTrue(listings.size() == 2);

        TextbookListing first = listings.get(0);
        assertEquals("978-0134685991", first.getIsbn());
        assertEquals("Effective Java", first.getTitle());
        assertEquals(3, first.getEdition());
        assertEquals("Good", first.getCondition());
        assertEquals(45.00, first.getPrice(), 0.01);
        assertEquals("CS101", first.getCourseNumber());
        assertEquals("S001", first.getSellerId());
        assertTrue(first.isAcceptsBarter());
    }

    @Test
    void testReadBookMetadata() throws IOException {
        String filePath = "src/test/java/resources/test-listings.csv";

        Map<String, String[]> metadata = CSVFileReader.readBookMetadata(filePath);
        assertEquals(2, metadata.size());

        assertTrue(metadata.containsKey("978-0134685991"));
        String[] effectiveJava = metadata.get("978-0134685991");
        assertEquals("Effective Java", effectiveJava[0]);
    }

    @Test
    void testReadMarketPrices() throws IOException {
        String filePath = "src/test/java/resources/test-market-prices.csv";

        Map<String, Double> prices = CSVFileReader.readMarketPrices(filePath);

        assertEquals(3, prices.size());

        //"978-0-13-468599-1" becomes "9780134685991"
        assertEquals(65.00, prices.get("9780134685991"), 0.01);
        assertEquals(52.00, prices.get("9780134685992"), 0.01);
        assertEquals(45.00, prices.get("9780321356683"), 0.01);
    }

    @Test
    void testEmptyFile() throws IOException {
        String filePath = "src/test/java/resources/test-empty.csv";

        List<TextbookListing> listings = CSVFileReader.readListings(filePath);
        Map<String, Double> prices = CSVFileReader.readMarketPrices(filePath);
        Map<String, String[]> meta = CSVFileReader.readBookMetadata(filePath);

        assertNotNull(listings);
        assertTrue(listings.isEmpty());
        assertTrue(prices.isEmpty());
        assertTrue(meta.isEmpty());
    }

    @Test
    void testReadListings_MalformedRow() throws IOException {
        String filePath = "src/test/java/resources/test-malformed.csv";

        List<TextbookListing> listings = CSVFileReader.readListings(filePath);

        //skipped malformed line
        assertTrue(listings.size() == 2);
    }


}
