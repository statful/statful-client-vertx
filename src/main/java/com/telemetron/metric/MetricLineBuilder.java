package com.telemetron.metric;

import com.telemetron.client.Aggregation;
import com.telemetron.client.AggregationFreq;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Telemetron metric line builder. Builds metrics lines according to Telemetron specification
 */
public final class MetricLineBuilder {

    /**
     * Optional application name to be added to the tag list
     */
    private Optional<String> app;

    /**
     * Prefix to be set in the metric
     */
    private String prefix;
    /**
     * Namespace to be set in the metric
     */
    private String namespace;
    /**
     * Metric name to be set in the metric
     */
    private String metricName;

    /**
     * Tags to be applied in the metric
     */
    private Map<String, String> tags;

    /**
     * Metric value to be sent
     */
    private String value;

    /**
     * timestamp of when the metric was collected
     */
    private long timestamp;

    /**
     * List of aggregations to be applied to the sent metric
     */
    private List<Aggregation> aggregations = new ArrayList<>();

    /**
     * Frequency of aggregation to be applied to the metric
     */
    private AggregationFreq frequency = AggregationFreq.FREQ_10;

    /**
     * builds the metric following telemetron spec
     *
     * @return String with the formatted metric
     */
    @Nonnull
    public String build() {
        final StringBuilder sb = new StringBuilder();

        sb.append(this.prefix).append(".").append(this.namespace);

        sb.append(".").append(metricName);

        // merge application to the tag list
        this.app.ifPresent(application -> this.tags.put("app", application));

        toStringTags().ifPresent(stringTag -> sb.append(",").append(stringTag));

        sb.append(" ").append(value);

        sb.append(" ").append(timestamp);

        toStringAggregations().ifPresent(stringAggregation ->
                        sb.append(" ")
                                .append(stringAggregation)
                                .append(",")
                                .append(frequency.getValue())
        );

        return sb.toString();
    }

    private Optional<String> toStringAggregations() {

        if (this.aggregations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.aggregations.stream()
                .map(Aggregation::getName).collect(Collectors.joining(",")));
    }

    private Optional<String> toStringTags() {
        if (tags == null) {
            return Optional.empty();
        }
        return Optional.of(tags.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(",")));
    }

    /**
     * @param application application name to be added to
     * @return a reference to this, so the API can be used fluently
     */
    public MetricLineBuilder withApp(@Nonnull final Optional<String> application) {
        this.app = application;
        return this;
    }

    /**
     * @param prefixToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withPrefix(@Nonnull final String prefixToAdd) {
        Objects.requireNonNull(prefixToAdd);
        this.prefix = prefixToAdd;
        return this;
    }

    /**
     * @param namespaceToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withNamespace(@Nonnull final String namespaceToAdd) {
        Objects.requireNonNull(namespaceToAdd);
        this.namespace = namespaceToAdd;
        return this;
    }

    /**
     * @param metricNametoAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withMetricName(@Nonnull final String metricNametoAdd) {
        Objects.requireNonNull(metricNametoAdd);
        this.metricName = metricNametoAdd;
        return this;
    }

    /**
     * @param tagName  of the tag to be added to the metric
     * @param tagValue of the tag to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withTag(@Nonnull final String tagName, @Nonnull final String tagValue) {
        Objects.requireNonNull(tagName);
        Objects.requireNonNull(tagValue);

        if (tags == null) {
            tags = new HashMap<>();
        }
        tags.put(tagName, tagValue);
        return this;
    }

    /**
     * @param valueToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    public MetricLineBuilder withValue(@Nonnull final String valueToAdd) {
        Objects.requireNonNull(valueToAdd);
        this.value = valueToAdd;
        return this;
    }

    /**
     * @param timestampToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    public MetricLineBuilder withTimestamp(final long timestampToAdd) {
        this.timestamp = timestampToAdd;
        return this;
    }

    /**
     * @param aggregationsToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    public MetricLineBuilder withAggregations(@Nullable final List<Aggregation> aggregationsToAdd) {
        if (aggregationsToAdd != null) {
            this.aggregations.addAll(aggregationsToAdd);
        }
        return this;
    }

    /**
     * @param frequencyToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    public MetricLineBuilder withAggregationFrequency(@Nonnull final AggregationFreq frequencyToAdd) {
        Objects.requireNonNull(frequencyToAdd);
        this.frequency = frequencyToAdd;
        return this;
    }
}
