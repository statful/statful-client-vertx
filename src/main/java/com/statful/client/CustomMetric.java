package com.statful.client;

import com.statful.metric.DataPoint;
import com.statful.metric.MetricLineBuilder;
import com.statful.utils.Pair;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Custom Metric object to be sent on the event bus
 */
public class CustomMetric implements DataPoint {

    /**
     * Statful options to be used when building the metric line
     */
    private StatfulMetricsOptions options;

    /**
     * Metric name to collect
     */
    private final String metricName;

    /**
     * Value to collect
     */
    private final long value;

    /**
     * List of tags to apply to the request
     */
    private final List<Pair<String, String>> tags;

    /**
     * Type of the metric to apply
     */
    private final MetricType metricType;

    /**
     * List of aggregations to apply to the metric
     */
    private final List<Aggregation> aggregations;

    /**
     * Aggregation frequency to apply to the metric
     */
    private final AggregationFreq frequency;

    /**
     * Timestamp of metric creation
     */
    private final long unixTimeStamp;

    /**
     * Copy constructor
     *
     * @param customMetric original metric do duplicate
     */
    public CustomMetric(final CustomMetric customMetric) {
        this.options = customMetric.getOptions();
        this.metricName = customMetric.getMetricName();
        this.value = customMetric.getValue();
        this.tags = customMetric.getTags();
        this.metricType = customMetric.getMetricType().orElse(null);
        this.aggregations = customMetric.getAggregations();
        this.frequency = customMetric.getFrequency().orElse(null);
        this.unixTimeStamp = customMetric.getUnixTimeStamp();
    }

    /**
     * Builder based constructor
     *
     * @param builder builder with source data
     */
    public CustomMetric(final Builder builder) {
        this.metricName = Objects.requireNonNull(builder.metricName, "MetricName cannot be null");
        this.value = builder.value;
        this.tags = builder.tags;
        this.metricType = builder.metricType;
        this.aggregations = builder.aggregations;
        this.frequency = builder.frequency;
        this.unixTimeStamp = this.calculateUnixTimestamp();
    }

    @Override
    public String toMetricLine() {
        final MetricLineBuilder metricLineBuilder = new MetricLineBuilder()
                .withNamespace(this.options.getNamespace())
                .withMetricName(this.metricName)
                .withValue(String.valueOf(this.value))
                .withTimestamp(this.unixTimeStamp)
                .withSampleRate(this.options.getSampleRate());

        if (!getAggregations().isEmpty()) {
            // Add optional aggregations
            metricLineBuilder.withAggregations(getAggregations());
        } else {
            // Add global aggregations
            metricLineBuilder.withAggregations(supplyGlobalAggregations());
        }

        // Add optional/global aggregation frequency
        metricLineBuilder.withAggregationFrequency(getFrequency().orElseGet(this::supplyGlobalAggregationFrequency));

        // Add optional metric type
        getMetricType().ifPresent(metricLineBuilder::withMetricType);

        // Add optional application
        this.options.getApp().ifPresent(metricLineBuilder::withApp);

        // Add list of tags
        getTags().forEach(pair -> metricLineBuilder.withTag(pair.getLeft(), pair.getRight()));

        // Add global list of tags
        this.options.getTags().forEach(pair -> metricLineBuilder.withTag(pair.getLeft(), pair.getRight()));

        return metricLineBuilder.build();
    }

    private List<Aggregation> supplyGlobalAggregations() {
        if (!getMetricType().isPresent()) {
            return Collections.emptyList();
        } else {
            switch (getMetricType().get()) {
                case GAUGE:
                    return this.options.getGaugeAggregations();
                case COUNTER:
                    return this.options.getCounterAggregations();
                case TIMER:
                    return this.options.getTimerAggregations();
                default:
                    throw new IllegalArgumentException("Invalid metric type provided.");
            }
        }
    }

    private AggregationFreq supplyGlobalAggregationFrequency() {
        if (!getMetricType().isPresent()) {
            return StatfulMetricsOptions.getDefaultFrequency();
        } else {
            switch (getMetricType().get()) {
                case GAUGE:
                    return this.options.getGaugeFrequency();
                case COUNTER:
                    return this.options.getCounterFrequency();
                case TIMER:
                    return this.options.getTimerFrequency();
                default:
                    throw new IllegalArgumentException("Invalid metric type provided.");
            }
        }
    }

    private StatfulMetricsOptions getOptions() {
        return options;
    }

    private String getMetricName() {
        return metricName;
    }

    private long getValue() {
        return value;
    }

    private List<Pair<String, String>> getTags() {
        if (isNull(tags)) {
            return Collections.emptyList();
        }

        return tags;
    }

    private Optional<MetricType> getMetricType() {
        return Optional.ofNullable(metricType);
    }

    private List<Aggregation> getAggregations() {
        if (isNull(aggregations)) {
            return Collections.emptyList();
        }

        return aggregations;
    }

    private Optional<AggregationFreq> getFrequency() {
        return Optional.ofNullable(frequency);
    }

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setOptions(final StatfulMetricsOptions metricsOptions) {
        this.options = metricsOptions;
    }

    /**
     * Custom Metric object builder to be sent on the event bus
     */
    public static class Builder {

        /**
         * Metric name to collect
         */
        private String metricName;

        /**
         * Value to collect
         */
        private long value;

        /**
         * List of tags to apply to the request
         */
        private List<Pair<String, String>> tags;

        /**
         * Type of metric to send
         */
        private MetricType metricType;

        /**
         * List of aggregations to be applied
         */
        private List<Aggregation> aggregations;

        /**
         * Aggregation frequency for this metric
         */
        private AggregationFreq frequency;

        /**
         * Constructor to initialize the builder
         */
        public Builder() {
        }

        /**
         * Build method for a metric to send on the event bus
         *
         * @return A built metric
         */
        public CustomMetric build() {
            return new CustomMetric(this);
        }

        /**
         * @param name name of the metric
         * @return a reference to self
         */
        public Builder withMetricName(final String name) {
            this.metricName = name;
            return this;
        }

        /**
         * @param metricValue of the metric
         * @return a reference to self
         */
        public Builder withValue(final long metricValue) {
            this.value = metricValue;
            return this;
        }

        /**
         * @param tagsList list of tags to apply to the metric
         * @return a reference to self
         */
        public Builder withTags(final List<Pair<String, String>> tagsList) {
            this.tags = tagsList;
            return this;
        }

        /**
         * @param type type of the metric
         * @return a reference to self
         */
        public Builder withMetricType(final MetricType type) {
            this.metricType = type;
            return this;
        }

        /**
         * @param aggregationsList list of aggreations to apply to the metric
         * @return a reference to self
         */
        public Builder withAggregations(final List<Aggregation> aggregationsList) {
            this.aggregations = aggregationsList;
            return this;
        }

        /**
         * @param aggregationFrequency aggregation frequency for this metric
         * @return a reference to self
         */
        public Builder withFrequency(final AggregationFreq aggregationFrequency) {
            this.frequency = aggregationFrequency;
            return this;
        }
    }

    /**
     * Codec to allow sending messages throught the event bus
     */
    static final class CustomMetricCodec implements MessageCodec<CustomMetric, CustomMetric> {

        @Override
        public void encodeToWire(final Buffer buffer, final CustomMetric customMetric) {
            throw new UnsupportedOperationException("Currently doesn't support clustered vertx instance");
        }

        @Override
        public CustomMetric decodeFromWire(final int pos, final Buffer buffer) {
            throw new UnsupportedOperationException("Currently doesn't support clustered vertx instance");
        }

        @Override
        public CustomMetric transform(final CustomMetric customMetric) {
            return new CustomMetric(customMetric);
        }

        @Override
        public String name() {
            return this.getClass().getName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }
}
