package com.statful.client;

/**
 * Supported metric types
 */
public enum MetricType {
    /**
     * Type for gauge metrics
     */
    GAUGE("gauge"),
    /**
     * Type for counter metrics
     */
    COUNTER("counter"),
    /**
     * Type for timer metrics
     */
    TIMER("timer");

    /**
     * String representation of this metric type
     */
    private String value;

    MetricType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
