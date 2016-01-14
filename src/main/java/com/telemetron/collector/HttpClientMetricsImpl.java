package com.telemetron.collector;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.HttpClientDataPoint;
import com.telemetron.sender.Sender;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * HttpClient metrics collector
 */
public final class HttpClientMetricsImpl extends HttpMetrics implements HttpClientMetrics<HttpRequestMetrics, SocketAddress, SocketAddress> {

    /**
     * @param sender  responsible for holding the metrics and sending them
     * @param options options to latter be used by the metrics builder
     */
    public HttpClientMetricsImpl(@Nonnull final Sender sender, @Nonnull final TelemetronMetricsOptions options) {
        super(sender, options);
    }

    /**
     * Only requests that contain a tracking tag will be tracked
     *
     * @inheritDoc
     */
    @Override
    @Nullable
    public HttpRequestMetrics requestBegin(final SocketAddress socketMetric, final SocketAddress localAddress,
                                           final SocketAddress remoteAddress, final HttpClientRequest request) {
        return httpRequestBegin(remoteAddress, request.headers(), request.method());
    }


    /**
     * Handles request time measurements and builds the data point
     *
     * @param requestMetric If the request is not be tracked this will be null
     * @param response      http client response
     */
    @Override
    public void responseEnd(@Nullable final HttpRequestMetrics requestMetric, final HttpClientResponse response) {

        httpRequestEnd(requestMetric, response.statusCode(), HttpClientDataPoint.Type.CLIENT);
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
}
