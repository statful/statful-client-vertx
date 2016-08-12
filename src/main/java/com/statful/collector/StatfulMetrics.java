package com.statful.collector;

import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.DataPoint;
import com.statful.sender.Sender;
import io.vertx.core.spi.metrics.Metrics;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Contains shared logic for http client and server metrics
 */
public abstract class StatfulMetrics implements Metrics {

    /**
     * Instance of metrics sender
     */
    private Sender sender;

    /**
     * Options to be used by the metrics builder
     */
    private final StatfulMetricsOptions options;

    /**
     * @param options options to latter be used by the metrics builder
     */
    public StatfulMetrics(@Nonnull final StatfulMetricsOptions options) {
        this.options = options;
    }

    /**
     * @param sender  responsible for holding the metrics and sending them
     * @param options options to latter be used by the metrics builder
     */
    StatfulMetrics(@Nonnull final Sender sender, @Nonnull final StatfulMetricsOptions options) {
        this.sender = Objects.requireNonNull(sender);
        this.options = Objects.requireNonNull(options);
    }

    /**
     * adds a metric to the sender
     *
     * @param dataPoint to be added
     */
    protected void addMetric(final DataPoint dataPoint) {
        if (this.sender != null) {
            this.sender.addMetric(dataPoint);
        }
    }

    /**
     * @return options for building the datapoint
     */
    protected StatfulMetricsOptions getOptions() {
        return options;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void close() {

    }

    /**
     * Allows externally setting the sender instance
     * @param sender fully instantiated sender
     */
    public void setSender(@Nonnull  final Sender sender) {
        this.sender = Objects.requireNonNull(sender);
    }

    /**
     * Checks if a sender is already defined
     * @return true if it has a sender false otherwise
     */
    public boolean hasSender() {
        return this.sender != null;
    }
}
