package com.telemetron.collector;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.HttpClientDataPoint;
import com.telemetron.sender.Sender;
import com.telemetron.tag.Tags;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.Metrics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Contains shared logic for http client and server metrics
 */
abstract class HttpMetrics implements Metrics {

    /**
     * Instance of metrics sender
     */
    private final Sender sender;

    /**
     * Options to be used by the metrics builder
     */
    private final TelemetronMetricsOptions options;

    /**
     * @param sender  responsible for holding the metrics and sending them
     * @param options options to latter be used by the metrics builder
     */
    HttpMetrics(@Nonnull final Sender sender, @Nonnull final TelemetronMetricsOptions options) {
        this.sender = Objects.requireNonNull(sender);
        this.options = Objects.requireNonNull(options);
    }

    /**
     * Only requests that contain a tracking tag will be tracked.
     * This method removes the tracking header from the header map.
     *
     * @param remoteAddress address of the request
     * @param headers       headers of the request.
     * @param method        http method of the request
     */
    protected HttpRequestMetrics httpRequestBegin(final SocketAddress remoteAddress, final MultiMap headers, final HttpMethod method) {
        // extract request tag to identify the metric and confirm that we want to track it
        String requestTag = headers.get(Tags.TRACK_HEADER.toString());

        HttpRequestMetrics metric = null;

        if (requestTag != null) {
            // Remove tracking header to avoid it propagating to clients
            headers.remove(Tags.TRACK_HEADER.toString());

            // Create client request metric
            metric = new HttpRequestMetrics(requestTag, remoteAddress, method);
            metric.start();
        }

        return metric;
    }

    /**
     * Handles request time measurements and builds the data point
     *
     * @param requestMetric If the request is not be tracked this will be null
     * @param statusCode    http status code
     * @param type          wether this metric is from a client or server
     */
    protected void httpRequestEnd(@Nullable final HttpRequestMetrics requestMetric, final int statusCode, final HttpClientDataPoint.Type type) {
        if (requestMetric == null) {
            return;
        }

        final long responseTime = requestMetric.elapsed();

        sender.addMetric(new HttpClientDataPoint(options, requestMetric.getRequestTag(), requestMetric.getMethod(), responseTime, statusCode, type));
    }


    /**
     * @inheritDoc
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() {

    }
}
