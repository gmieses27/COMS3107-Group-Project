package marketplace.processor;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LenientFairnessStrategyTest {
    @Test
    void test_isFairWithin20(){
        LenientStrategy strategy = new LenientStrategy();

        assertTrue(strategy.isFair(95.0, 110.00));
    }
    @Test
    void test_isFairEqual20(){
        LenientStrategy strategy = new LenientStrategy();
        assertTrue(strategy.isFair(80.0, 100.00));
    }

    @Test
    void testIsFair_Outside20Percent() {
        LenientStrategy strategy = new LenientStrategy();

        // Market: 100.0, Student: 80.0 (Diff 20%)
        assertFalse(strategy.isFair(80.0, 101.0), "80 vs 101 should be unfair (21% diff)");

        // Market: 100.0, Student: 111.0 (Diff 11%)
        assertFalse(strategy.isFair(100.0, 130.0), "100 vs 125 should br unfair");
    }
}
