package com.statful.collector;

import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.HttpClientDataPoint;
import com.statful.sender.Sender;
import com.statful.tag.Tags;
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
public final class HttpClientMetricsImpl extends StatfulMetrics implements HttpClientMetrics<HttpRequestMetrics, SocketAddress, SocketAddress, Void, Void> {

    /**
     * @param options options to latter be used by the metrics builder
     */
    public HttpClientMetricsImpl(final StatfulMetricsOptions options) {
        super(options);
    }

    /**
     * @param sender  responsible for holding the metrics and sending them
     * @param options options to latter be used by the metrics builder
     */
    public HttpClientMetricsImpl(@Nonnull final Sender sender, @Nonnull final StatfulMetricsOptions options) {
        super(sender, options);
    }

    /**
     * Only requests that contain a tracking tag will be tracked
     */
    @Override
    @Nullable
    public HttpRequestMetrics requestBegin(final Void endpointMetric,
                                           final SocketAddress socketMetric,
                                           final SocketAddress localAddress,
                                           final SocketAddress remoteAddress,
                                           final HttpClientRequest request) {

        // extract request tag to identify the metric and confirm that we want to track it
        String requestTag = request.headers().get(Tags.TRACK_HEADER.toString());

        HttpRequestMetrics metric = null;

        if (requestTag != null) {
            // Remove tracking header to avoid it propagating to clients
            request.headers().remove(Tags.TRACK_HEADER.toString());

            // Create client request metric
            metric = new HttpRequestMetrics(requestTag, remoteAddress, request.method());
            metric.start();
        }

        return metric;
    }

    @Override
    public Void createEndpoint(final String host, final int port, final int maxPoolSize) {
        return null;
    }

    @Override
    public void closeEndpoint(final String host, final int port, final Void endpointMetric) {

    }

    @Override
    public Void enqueueRequest(final Void endpointMetric) {
        return null;
    }

    @Override
    public void dequeueRequest(final Void endpointMetric, final Void taskMetric) {

    }

    @Override
    public void endpointConnected(final Void endpointMetric, final SocketAddress socketMetric) {

    }

    @Override
    public void endpointDisconnected(final Void endpointMetric, final SocketAddress socketMetric) {

    }


    @Override
    public void requestEnd(final HttpRequestMetrics requestMetric) {

    }

    @Override
    public void responseBegin(final HttpRequestMetrics requestMetric, final HttpClientResponse response) {

    }

    @Override
    public HttpRequestMetrics responsePushed(final Void endpointMetric,
                                             final SocketAddress socketMetric,
                                             final SocketAddress localAddress,
                                             final SocketAddress remoteAddress,
                                             final HttpClientRequest request) {
        return null;
    }

    @Override
    public void requestReset(final HttpRequestMetrics requestMetric) {

    }

    /**
     * Handles request time measurements and builds the data point
     *
     * @param requestMetric If the request is not be tracked this will be null
     * @param response      http client response
     */
    @Override
    public void responseEnd(@Nullable final HttpRequestMetrics requestMetric, final HttpClientResponse response) {

        if (requestMetric == null) {
            return;
        }

        final long responseTime = requestMetric.elapsed();

        super.addMetric(
                new HttpClientDataPoint(super.getOptions(), "execution", requestMetric.getRequestTag(),
                        requestMetric.getMethod(), responseTime,
                        response.statusCode(), HttpClientDataPoint.Type.CLIENT)
        );
    }

    @Override
    public SocketAddress connected(final Void endpointMetric, final SocketAddress socketMetric, final WebSocket webSocket) {
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
