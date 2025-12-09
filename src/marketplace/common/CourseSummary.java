package marketplace.common;

import java.util.*;

public class CourseSummary {
    public final String course;
    public final double averagePrice;
    public final int count;

    public CourseSummary(String course, double averagePrice, int count) {
        this.course = course;
        this.averagePrice = averagePrice;
        this.count = count;
    }
}
