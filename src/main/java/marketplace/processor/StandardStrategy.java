package marketplace.processor;

import marketplace.common.FairnessStrategy;

public class StandardStrategy implements FairnessStrategy {
    public boolean isFair(double s, double m) {
        return Math.abs(s - m) / m <= 0.10; // 10% rule
    }
}
