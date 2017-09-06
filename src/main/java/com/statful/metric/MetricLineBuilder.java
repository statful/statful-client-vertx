package com.statful.metric;

import com.google.common.base.Strings;
import com.statful.client.Aggregation;
import com.statful.client.AggregationFreq;
import com.statful.client.MetricType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Statful metric line builder. Builds metrics lines according to Statful specification
 */
public final class MetricLineBuilder {

    /**
     * Optional application name to be added to the tag list
     */
    private String app;

    /**
     * Namespace to be set in the metric
     */
    private String namespace;

    /**
     * Optional metric type to be set in the metric
     */
    private MetricType metricType;

    /**
     * Metric name to be added to the metric
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
     * Timestamp of when the metric was collected
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
     * Applied sample rate
     */
    private int sampleRate;

    /**
     * Builds the metric following Statful spec
     *
     * @return String with the formatted metric
     */
    @Nonnull
    public String build() {
        final StringBuilder sb = new StringBuilder();

        if (!Strings.isNullOrEmpty(namespace)) {
            sb.append(namespace).append(".");
        }

        if (!isNull(metricType)) {
            sb.append(metricType.toString()).append(".");
        }

        sb.append(metricName);

        // merge application to the tag list
        getApp().ifPresent(application -> {
            if (isNull(tags)) {
                tags = new HashMap<>();
                tags.put("app", application);
            }

            tags.putIfAbsent("app", application);
        });

        toStringTags().ifPresent(stringTag -> sb.append(",").append(stringTag));

        sb.append(" ").append(value);

        sb.append(" ").append(timestamp);

        toStringAggregations().ifPresent(stringAggregation ->
                sb.append(" ")
                        .append(stringAggregation)
                        .append(",")
                        .append(frequency.getValue())
        );

        sb.append(" ").append(sampleRate);

        return sb.toString();
    }

    private Optional<String> getApp() {
        return Optional.ofNullable(app);
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
    @Nonnull
    public MetricLineBuilder withApp(@Nonnull final String application) {
        this.app = Objects.requireNonNull(application);
        return this;
    }

    /**
     * @param namespaceToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withNamespace(@Nonnull final String namespaceToAdd) {
        this.namespace = Objects.requireNonNull(namespaceToAdd);
        return this;
    }

    /**
     * @param metricNameToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withMetricName(@Nonnull final String metricNameToAdd) {
        this.metricName = Objects.requireNonNull(metricNameToAdd);
        return this;
    }


    /**
     * @param metricTypeToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withMetricType(@Nonnull final MetricType metricTypeToAdd) {
        this.metricType = Objects.requireNonNull(metricTypeToAdd);
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
    @Nonnull
    public MetricLineBuilder withValue(@Nonnull final String valueToAdd) {
        this.value = Objects.requireNonNull(valueToAdd);
        return this;
    }

    /**
     * @param timestampToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withTimestamp(final long timestampToAdd) {
        this.timestamp = timestampToAdd;
        return this;
    }

    /**
     * @param aggregationsToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withAggregations(@Nonnull final List<Aggregation> aggregationsToAdd) {
        this.aggregations.addAll(Objects.requireNonNull(aggregationsToAdd));
        return this;
    }

    /**
     * @param frequencyToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withAggregationFrequency(@Nonnull final AggregationFreq frequencyToAdd) {
        this.frequency = Objects.requireNonNull(frequencyToAdd);
        return this;
    }

    /**
     * @param sampleRateToAdd to be added to the metric
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public MetricLineBuilder withSampleRate(final int sampleRateToAdd) {
        this.sampleRate = sampleRateToAdd;
        return this;
    }
}
