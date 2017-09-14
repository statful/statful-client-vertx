package com.statful.metric;

import com.statful.client.MetricType;
import com.statful.client.StatfulMetricsOptions;

/**
 * Representation of an HttpClient DataPoint. Holds the that of a metric and builds
 */
public abstract class HttpDataPoint implements DataPoint {

    /**
     * Identifies if the metric is from a http server or http client
     */
    public enum Type {
        /**
         * To tag server metrics
         */
        SERVER("server"),
        /**
         * To tag client metrics
         */
        CLIENT("client");

        /**
         * String identifier of the type
         */
        private final String value;

        /**
         * @param value identifier of the type
         */
        Type(final String value) {
            this.value = value;
        }
    }

    /**
     * Statful options to be used when building the metric line
     */
    private final StatfulMetricsOptions options;

    /**
     * Metric name
     */
    private final String metricName;

    /**
     * Name of the operation that you are tracking
     */
    private final String name;

    /**
     * Representation of the http verb request
     */
    private final String verb;

    /**
     * Duration of the request
     */
    private final String duration;

    /**
     * Http code to be added as tag
     */
    private final int responseCode;

    /**
     * Timestamp of metric creation
     */
    private final long unixTimeStamp;

    /**
     * Source of metric being collected (server vs client)
     */
    private final Type type;

    /**
     * constructor for a HttpClient Timer based metric, will calculate the unix timestamp of the metric on creation
     *
     * @param options      Statful options to be used when building the metric line
     * @param metricName   name of the metric
     * @param name         Name of the operation that you are tracking
     * @param httpVerb     Representation of the http verb request
     * @param duration     Duration of the request
     * @param responseCode Http code to be added as tag
     * @param type         if this metric belongs to http server or client
     */
    public HttpDataPoint(final StatfulMetricsOptions options, final String metricName, final String name,
                         final String httpVerb, final String duration, final int responseCode, final Type type) {

        this.options = options;
        this.metricName = metricName;
        this.name = name;
        this.verb = httpVerb;
        this.duration = duration;
        this.responseCode = responseCode;
        this.unixTimeStamp = this.calculateEpochTimestamp();
        this.type = type;
    }

    protected MetricLineBuilder buildMetricLine() {

        final MetricLineBuilder metricLineBuilder = new MetricLineBuilder()
                .withNamespace(this.options.getNamespace())
                .withMetricType(MetricType.TIMER)
                .withMetricName(this.metricName)
                .withTag("transport", "http")
                .withTag("type", this.type.value)
                .withTag("verb", this.verb)
                .withTag("statusCode", String.valueOf(this.responseCode))
                .withValue(this.duration)
                .withTimestamp(this.unixTimeStamp)
                .withAggregations(this.options.getTimerAggregations())
                .withAggregationFrequency(this.options.getTimerFrequency())
                .withSampleRate(this.options.getSampleRate());

        // Add optional application
        this.options.getApp().ifPresent(metricLineBuilder::withApp);

        // Add global list of tags
        this.options.getTags().forEach(pair -> metricLineBuilder.withTag(pair.getLeft(), pair.getRight()));

        return metricLineBuilder;
    }

    /**
     * @return string with the name of the operation that you are tracking
     */
    public String getName() {
        return name;
    }
}
