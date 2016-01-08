package com.telemetron.collector;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.HttpClientDataPoint;
import com.telemetron.sender.Sender;
import com.telemetron.tag.Tags;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


/**
 * HttpClient metrics collector
 */
public final class HttpClientMetricsImpl implements HttpClientMetrics<HttpClientRequestMetrics, SocketAddress, SocketAddress> {

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
    public HttpClientMetricsImpl(@Nonnull final Sender sender, @Nonnull final TelemetronMetricsOptions options) {
        this.sender = Objects.requireNonNull(sender);
        this.options = Objects.requireNonNull(options);
    }

    /**
     * Only requests that contain a tracking tag will be tracked
     *
     * @inheritDoc
     */
    @Override
    @Nullable
    public HttpClientRequestMetrics requestBegin(final SocketAddress socketMetric, final SocketAddress localAddress,
                                                 final SocketAddress remoteAddress, final HttpClientRequest request) {

        // extract request tag to identify the metric and confirm that we want to track it
        String requestTag = request.headers().get(Tags.TRACK_HEADER.toString());

        HttpClientRequestMetrics metric = null;

        if (requestTag != null) {
            request.headers().remove(Tags.TRACK_HEADER.toString());
            // Create client request metric

            metric = new HttpClientRequestMetrics(requestTag, remoteAddress, request.method());
            metric.start();
        }

        return metric;
    }

    /**
     * Handles request time measurements and builds the data point
     *
     * @param requestMetric If the request is not be tracked this will be null
     * @param response      http client response
     */
    @Override
    public void responseEnd(@Nullable final HttpClientRequestMetrics requestMetric, final HttpClientResponse response) {

        if (requestMetric == null) {
            return;
        }

        final long responseTime = requestMetric.elapsed();
        // check response status
        final int statusCode = response.statusCode();

        sender.addMetric(new HttpClientDataPoint(options, requestMetric.getRequestTag(), requestMetric.getMethod(), responseTime, statusCode));
    }

    @Override
    public SocketAddress connected(final SocketAddress socketMetric, final WebSocket webSocket) {
        return null;
    }

    @Override
    public void disconnected(final SocketAddress webSocketMetric) {

    }

    @Override
    public SocketAddress connected(final SocketAddress remoteAddress, final String remoteName) {
        return null;
    }

    @Override
    public void disconnected(final SocketAddress socketMetric, final SocketAddress remoteAddress) {

    }

    @Override
    public void bytesRead(final SocketAddress socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {

    }

    @Override
    public void bytesWritten(final SocketAddress socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {

    }

    @Override
    public void exceptionOccurred(final SocketAddress socketMetric, final SocketAddress remoteAddress, final Throwable t) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void close() {

    }
}
