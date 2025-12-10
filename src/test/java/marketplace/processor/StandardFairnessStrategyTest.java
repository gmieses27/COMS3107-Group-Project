package marketplace.processor;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StandardFairnessStrategyTest {

    @Test
    void test_isFairWithin10(){
        StandardStrategy strategy = new StandardStrategy();

        assertTrue(strategy.isFair(95.0, 100.00));
    }
    @Test
    void test_isFairEqual10(){
        StandardStrategy strategy = new StandardStrategy();
        assertTrue(strategy.isFair(90.0, 100.00));
    }

    @Test
    void testIsFair_Outside10Percent() {
        StandardStrategy strategy = new StandardStrategy();

        // Market: 100.0, Student: 80.0 (Diff 20%)
        assertFalse(strategy.isFair(80.0, 100.0), "80 vs 100 should be unfair (20% diff)");

        // Market: 100.0, Student: 111.0 (Diff 11%)
        assertFalse(strategy.isFair(111.0, 100.0), "111 vs 100 should be unfair (11% diff)");
    }
}
