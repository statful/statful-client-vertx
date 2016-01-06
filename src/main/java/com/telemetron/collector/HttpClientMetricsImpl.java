package com.telemetron.collector;

import com.telemetron.tag.Tags;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;

import javax.annotation.Nullable;


/**
 * HttpClient metrics collector
 */
public final class HttpClientMetricsImpl implements HttpClientMetrics<HttpClientRequestMetrics, SocketAddress, SocketAddress> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientMetricsImpl.class);

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
            metric = new HttpClientRequestMetrics(requestTag, remoteAddress);
            metric.start();
        }

        return metric;
    }

    /**
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
