package com.telemetron.sender;

import com.google.common.collect.Lists;
import com.telemetron.metric.DataPoint;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * To be extended by sender implementations. Contains a buffer to hold on to metrics before sending them
 */
public abstract class MetricsHolder implements Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsHolder.class);
    /**
     * Maximum theoretical buffer size to old on to metrics before sending them
     */
    private static final int MAX_BUFFER_SIZE = 5000;

    /**
     * Buffer to hold metrics
     */
    private final ArrayBlockingQueue<DataPoint> buffer;

    /**
     * Initializes the internal buffer. Implementers must call {@link #configureFlushInterval(Vertx, long, int)} to init
     * the process of sending metrics
     */
    public MetricsHolder() {
        this.buffer = new ArrayBlockingQueue<>(MAX_BUFFER_SIZE);
    }

    /**
     * Adds a metric to the buffer. If it fails to add it to the buffer tries to send it directly to the offer
     *
     * @param dataPoint metric to be stored
     */
    public final void addMetric(final DataPoint dataPoint) {
        boolean inserted = this.buffer.offer(dataPoint);
        if (!inserted) {
            LOGGER.warn("metric could not be added to buffer, discarding it {0} ", dataPoint.toMetricLine());
        }
    }

    /**
     * Methods uses vertx instance to set a periodic interval
     * @param vertx instance to create the periodic interval on
     * @param flushInterval time between flushes
     * @param flushSize number of elments to clean from the buffer
     */
    protected void configureFlushInterval(final Vertx vertx, final long flushInterval, final int flushSize) {
        vertx.setPeriodic(flushInterval, timerId -> flush(flushSize));
    }

    private void flush(final int flushSize) {
        List<DataPoint> toBeSent = Lists.newArrayListWithCapacity(flushSize);
        buffer.drainTo(toBeSent, flushSize);
        this.send(toBeSent);
    }
}
