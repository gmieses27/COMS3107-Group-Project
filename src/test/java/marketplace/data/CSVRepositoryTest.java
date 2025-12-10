package marketplace.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CSVRepositoryTest {

    @BeforeEach
    void forceReset() throws Exception {
        Field instance = CSVRepository.class.getDeclaredField("repo");
        instance.setAccessible(true);
        instance.set(null, null); // Force static field to null
    }

    @TempDir
    Path tempDir;

    private String validListPath;
    private String validMetaPath;
    private String validMarketPath;

    @BeforeEach
    void setUp() throws Exception {
        // 2. Create Dummy CSVs so the repo has something to read
        // We resolve paths inside the temp directory
        Path p1 = tempDir.resolve("listings.csv");
        Files.writeString(p1, "ISBN,Title,Edition,Condition,Price,Course,Seller,Barter\n111,Book A,1,good,10.0,CS1,S1,true");
        validListPath = p1.toString();

        Path p2 = tempDir.resolve("metadata.csv");
        Files.writeString(p2, "ISBN,Title,Author\n111,Book A,John Doe");
        validMetaPath = p2.toString();

        Path p3 = tempDir.resolve("market.csv");
        Files.writeString(p3, "ISBN,AverageUsedPrice,LowestPrice,HighestPrice\n111,15.0,10.0,20.0");
        validMarketPath = p3.toString();
    }

    @Test
    void testGetInstance_WithoutConfigure_ThrowsException() {
        // Verify defensive programming
        assertThrows(IllegalStateException.class, () -> {
            CSVRepository.getInstance();
        });
    }

    @Test
    void testConfigure_LoadsDataCorrectly() {
        CSVRepository.configure(validListPath, validMetaPath, validMarketPath);

        CSVRepository repo = CSVRepository.getInstance();

        // Assertions
        assertNotNull(repo);
        // Did the thread pool actually load the file?
        assertEquals(1, repo.getListings().size(), "Should verify listing loaded from temp file");
        assertEquals("111", repo.getListings().get(0).getIsbn());
    }

    @Test
    void testSingletonProperty_ReturnsSameInstance() {
        CSVRepository.configure(validListPath, validMetaPath, validMarketPath);

        CSVRepository ref1 = CSVRepository.getInstance();
        CSVRepository ref2 = CSVRepository.getInstance();

        assertSame(ref1, ref2, "Singleton must return same memory addresss");
    }

    @Test
    void testConfigure_CalledTwice_Ignored() {
        CSVRepository.configure(validListPath, validMetaPath, validMarketPath);
        CSVRepository first = CSVRepository.getInstance();

        //use bad paths
        CSVRepository.configure("bad/path", "bad/path", "bad/path");

        // second configure since invalid paths changes nothing
        CSVRepository second = CSVRepository.getInstance();
        assertSame(first, second);
        assertEquals(1, second.getListings().size(), "Data should not be wiped by second configure call");
    }

    @Test
    void testInvalidFiles_DoesNotCrash() {
        CSVRepository.configure("missing.csv", "missing.csv", "missing.csv");

        CSVRepository repo = CSVRepository.getInstance();
        assertNotNull(repo);
        assertTrue(repo.getListings().isEmpty(), "Should return empty list, not null");
        assertTrue(repo.getMarket().isEmpty());
    }
    @Test
    void testGetMetadata(){
        CSVRepository.configure(validListPath, validMetaPath, validMarketPath);
        CSVRepository repo = CSVRepository.getInstance();

        assertTrue(repo.getMetadata().size() == 1);

        String [] arr = repo.getMetadata().get("111");
        assertTrue(arr.length == 2);
        assertTrue(arr[0].equals("Book A"));
        assertTrue(arr[1].equals("John Doe"));
    }

}
