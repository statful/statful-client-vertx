package com.telemetron.collector;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.HttpClientDataPoint;
import com.telemetron.sender.Sender;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpServerMetrics;

import javax.annotation.Nonnull;


/**
 * HttpServer metrics collector
 */
public final class HttpServerMetricsImpl extends HttpMetrics implements HttpServerMetrics<HttpRequestMetrics, SocketAddress, SocketAddress> {

    /**
     * @param sender  responsible for holding the metrics and sending them
     * @param options options to latter be used by the metrics builder
     */
    public HttpServerMetricsImpl(@Nonnull final Sender sender, @Nonnull final TelemetronMetricsOptions options) {
        super(sender, options);
    }

    /**
     * @inheritDoc
     */
    @Override
    public HttpRequestMetrics requestBegin(final SocketAddress socketAddress, final HttpServerRequest request) {
        return httpRequestBegin(socketAddress, request.headers(), request.method());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void responseEnd(final HttpRequestMetrics requestMetric, final HttpServerResponse response) {
        httpRequestEnd(requestMetric, response.getStatusCode(), HttpClientDataPoint.Type.SERVER);
    }

    /**
     * @inheritDoc
     */
    @Override
    public SocketAddress upgrade(final HttpRequestMetrics requestMetric, final ServerWebSocket serverWebSocket) {
        return requestMetric.getAddress();
    }

    /**
     * @inheritDoc
     */
    @Override
    public SocketAddress connected(final SocketAddress socketMetric, final ServerWebSocket serverWebSocket) {
        return socketMetric;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void disconnected(final SocketAddress serverWebSocketMetric) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public SocketAddress connected(final SocketAddress remoteAddress, final String remoteName) {
        return remoteAddress;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void disconnected(final SocketAddress socketMetric, final SocketAddress remoteAddress) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void bytesRead(final SocketAddress socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void bytesWritten(final SocketAddress socketMetric, final SocketAddress remoteAddress, final long numberOfBytes) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void exceptionOccurred(final SocketAddress socketMetric, final SocketAddress remoteAddress, final Throwable t) {

    }
}
