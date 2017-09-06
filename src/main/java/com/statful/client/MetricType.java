package com.statful.client;

import java.util.List;

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

    /**
     * Returns the default aggregation list for this metric type.
     *
     * @param statfulMetricsOptions Configured statful client metric options
     * @return List of {@link Aggregation}
     */
    public List<Aggregation> getDefaultAggregationFromOptions(final StatfulMetricsOptions statfulMetricsOptions) {
        switch (this) {
            case GAUGE:
                return statfulMetricsOptions.getGaugeAggregations();
            case COUNTER:
                return statfulMetricsOptions.getCounterAggregations();
            case TIMER:
                return statfulMetricsOptions.getTimerAggregations();
            default:
                throw new IllegalArgumentException("Invalid metric type.");
        }
    }

    /**
     * Returns the default aggregation frequency for this metric type.
     *
     * @param statfulMetricsOptions Configured statful client metric options
     * @return An {@link AggregationFreq}
     */
    public AggregationFreq getDefaultAggregationFrequencyFromOptions(final StatfulMetricsOptions statfulMetricsOptions) {
        switch (this) {
            case GAUGE:
                return statfulMetricsOptions.getGaugeFrequency();
            case COUNTER:
                return statfulMetricsOptions.getCounterFrequency();
            case TIMER:
                return statfulMetricsOptions.getTimerFrequency();
            default:
                throw new IllegalArgumentException("Invalid metric type.");
        }
    }

    @Override
    public String toString() {
        return this.value;
    }
}
