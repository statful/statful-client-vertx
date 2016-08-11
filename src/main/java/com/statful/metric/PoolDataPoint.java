package com.statful.metric;

import com.statful.client.StatfulMetricsOptions;

/**
 * Representation of a ConnectionPool DataPoint
 */
public class PoolDataPoint implements DataPoint {

    /**
     * Statful options to be used when building the metric line
     */
    private final StatfulMetricsOptions options;

    /**
     * Value of the metric
     */
    private final String value;

    /**
     * time stamp of metric creation
     */
    private final long unixTimeStamp;

    /**
     * Name of the poll to be used has a tag
     */
    private final String poolName;

    /**
     * Type of the metric (max, in-use, queued)
     */
    private final String metricType;

    /**
     * Constructor for a Pool Gauge based metric, will calculate the unix timestamp of the metric on creation
     *
     * @param options    Statful options to be used when building the metric line
     * @param poolName   name of the poll to be used has a tag
     * @param metricType type of the pool
     * @param value      value of the metric
     */
    public PoolDataPoint(final StatfulMetricsOptions options,
                         final String poolName,
                         final String metricType,
                         final String value) {

        this.options = options;
        this.metricType = metricType;
        this.poolName = poolName;
        this.value = value;
        this.unixTimeStamp = this.getUnixTimeStamp();
    }

    @Override
    public String toMetricLine() {
        final MetricLineBuilder metricLineBuilder = new MetricLineBuilder()
                .withPrefix(this.options.getPrefix())
                .withNamespace(this.options.getNamespace())
                .withMetricType("gauge")
                .withMetricName("pool")
                .withValue(this.value)
                .withTimestamp(this.unixTimeStamp)
                .withAggregations(this.options.getGaugeAggregations())
                .withAggregationFrequency(this.options.getGaugeFrequency())
                .withApp(this.options.getApp())
                .withTag("name", poolName)
                .withTag("type", metricType);

        // Add global list of tags
        this.options.getTags().forEach(pair -> metricLineBuilder.withTag(pair.getLeft(), pair.getRight()));

        return metricLineBuilder.build();
    }
}
