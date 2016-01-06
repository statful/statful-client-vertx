package com.telemetron.sender;

import com.telemetron.metric.DataPoint;
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
}
