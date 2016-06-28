package com.statful.client;

/**
 * Contains all supported aggregations
 */
public enum Aggregation {
    /**
     * Average aggregation
     */
    AVG("avg"),
    /**
     * Percentile 90 aggregation
     */
    P90("p90"),
    /**
     * Count aggregation
     */
    COUNT("count"),
    /**
     * Last aggregation
     */
    LAST("last"),
    /**
     * Summatory aggregation
     */
    SUM("sum"),
    /**
     * First aggregation
     */
    FIRST("first"),
    /**
     * Percentile 95 aggregation
     */
    P95("p95"),
    /**
     * Aggregate by min value
     */
    MIN("min"),
    /**
     * Aggregate by max value
     */
    MAX("max");

    /**
     * String representation of the aggregation
     */
    private final String name;

    /**
     * private constructor to force that all entries have a name
     *
     * @param name String representation of the aggregation
     */
    Aggregation(final String name) {
        this.name = name;
    }

    /**
     * Gets the string value of the aggregation, to be used to build metric lines
     *
     * @return String with the aggregation representation
     */
    public final String getName() {
        return name;
    }
}
