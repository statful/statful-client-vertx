package com.telemetron.collector;

import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;


/**
 * HttpClient metrics collector
 */
public final class HttpClientMetricsImpl implements HttpClientMetrics<HttpClientRequestMetrics, SocketAddress, SocketAddress> {

    /**
     * Internal logger, should only be used in trace / info to log everything that happens metrics collection wise
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientMetricsImpl.class);

    @Override
    public HttpClientRequestMetrics requestBegin(final SocketAddress socketMetric, final SocketAddress localAddress,
                                                 final SocketAddress remoteAddress, final HttpClientRequest request) {


        // Create client request metric
        HttpClientRequestMetrics metric = new HttpClientRequestMetrics(remoteAddress);
        metric.start();
        return metric;
    }

    @Override
    public void responseEnd(final HttpClientRequestMetrics requestMetric, final HttpClientResponse response) {

        final long responseTime = requestMetric.elapsed();

        // check response status
        final int statusCode = response.statusCode();

        LOGGER.trace("request {0}, status code {1}, duration {1}", requestMetric.getAddress().host(), statusCode, responseTime);
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
