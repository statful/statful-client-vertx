package com.statful.sender;

import com.statful.metric.DataPoint;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Interface to be implemented by metric senders
 */
public interface Sender {
    /**
     * Sender method that gets a list of datapoints and sends them
     *
     * @param metrics     metrics to be sent. Each instance is responsible for creating the full metric line including tags
     * @param sentHandler handler called if you want to be notified after the metrics are sent.
     */
    void send(@Nonnull List<DataPoint> metrics, @Nonnull Handler<AsyncResult<Void>> sentHandler);

    /**
     * Sender method that gets a list of datapoints and sends them
     *
     * @param metrics metrics to be sent. Each instance is responsible for creating the full metric line including tags
     */
    void send(@Nonnull List<DataPoint> metrics);

    /**
     * Stores metric in a buffer to be sent
     *
     * @param dataPoint metric to be stored
     * @return true if the metric was inserted false otherwise
     */
    boolean addMetric(final DataPoint dataPoint);

    /**
     * Closes the sender
     *
     * @param handler to execute when closed
     */
    void close(final Handler<AsyncResult<Void>> handler);
}
