package com.statful.sender;

import com.google.common.collect.Lists;
import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.DataPoint;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * To be extended by sender implementations. Contains a buffer to hold on to metrics before sending them
 */
abstract class MetricsHolder implements Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsHolder.class);

    /**
     * Buffer to hold metrics
     */
    private final ArrayBlockingQueue<DataPoint> buffer;

    /**
     * If set to true will only log the metrics instead of sending them
     */
    private final boolean dryrun;

    /**
     * Sampler to be used to decide if a metric should be added or not
     */
    private Sampling sampler;

    /**
     * Initializes the internal buffer. Implementers must call {@link #configureFlushInterval(Vertx, long, int)} to init
     * the process of sending metrics
     *
     * @param options Statful configuration to decide if metrics should be sent or not
     * @param sampler instance to be used to decide if a metric should be collected or not
     */
    MetricsHolder(@Nonnull final StatfulMetricsOptions options, @Nonnull final Sampling sampler) {
        this.dryrun = Objects.requireNonNull(options).isDryrun();

        this.sampler = Objects.requireNonNull(sampler);

        this.buffer = new ArrayBlockingQueue<>(options.getMaxBufferSize());
    }

    /**
     * Adds a metric to the buffer. If it fails to add it to the buffer tries to send it directly to the offer
     *
     * @param dataPoint metric to be stored
     * @return true if the metric was inserted false otherwise
     */
    public final boolean addMetric(final DataPoint dataPoint) {

        if (!this.sampler.shouldInsert()) {
            return false;
        }

        boolean inserted = this.buffer.offer(dataPoint);
        if (!inserted) {
            LOGGER.warn("metric could not be added to buffer, discarding it {} ", dataPoint.toMetricLine());
        }
        return inserted;
    }

    /**
     * Methods uses vertx instance to set a periodic interval
     *
     * @param vertx         instance to create the periodic interval on
     * @param flushInterval time between flushes
     * @param flushSize     number of elements to clean from the buffer
     */
    void configureFlushInterval(final Vertx vertx, final long flushInterval, final int flushSize) {
        vertx.setPeriodic(flushInterval, timerId -> flush(flushSize));
    }

    private void flush(final int flushSize) {
        List<DataPoint> toBeSent = Lists.newArrayListWithCapacity(flushSize);
        buffer.drainTo(toBeSent, flushSize);

        if (dryrun) {
            final String toSendMetrics = toBeSent.stream().map(DataPoint::toMetricLine).collect(Collectors.joining("\n"));
            LOGGER.debug("dryrun: {}", toSendMetrics);
        } else {
            this.send(toBeSent);
        }
    }

    /**
     * Collects a list of metrics into a single String separated by line breaks
     * @param metrics a list of datapoints to be bundled
     * @return An empty optional if the list is null or empty, or an optional containing the bundled metrics
     */
    Optional<String> bundleMetrics(final List<DataPoint> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(metrics.stream().map(DataPoint::toMetricLine).collect(Collectors.joining("\n")));
    }
}
