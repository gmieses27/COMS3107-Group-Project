package marketplace.processor;

import marketplace.common.FairnessStrategy;

public class LenientStrategy implements FairnessStrategy {
    public boolean isFair(double s, double m) {
        return Math.abs(s - m) / m <= 0.20;
    }
}
