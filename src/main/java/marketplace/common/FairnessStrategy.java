package marketplace.common;

public interface FairnessStrategy {
    boolean isFair(double studentPrice, double marketPrice);
}
