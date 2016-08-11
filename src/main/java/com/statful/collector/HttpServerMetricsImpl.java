package com.statful.collector;

import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.HttpClientDataPoint;
import com.statful.metric.HttpServerDataPoint;
import com.statful.sender.Sender;
import com.statful.utils.Pair;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpServerMetrics;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * HttpServer metrics collector
 */
public final class HttpServerMetricsImpl extends StatfulMetrics implements HttpServerMetrics<HttpRequestMetrics, SocketAddress, SocketAddress> {

    /**
     * Holds compiled regex and replacement patterns for urls
     */
    private final List<Pair<Pattern, String>> replacements;

    /**
     * Holds compiled regex for urls that should not be tracked
     */
    private final List<Pattern> ignore;

    /**
     * @param options options to latter be used by the metrics builder
     */
    public HttpServerMetricsImpl(@Nonnull final StatfulMetricsOptions options) {

        super(options);

        this.replacements = options.getPatterns().stream()
                .map(entry -> new Pair<>(Pattern.compile(entry.getLeft()), entry.getRight()))
                .collect(Collectors.toList());

        this.ignore = options.getHttpServerPathsIgnore().stream().map(Pattern::compile).collect(Collectors.toList());
    }

    /**
     * @param sender  responsible for holding the metrics and sending them
     * @param options options to latter be used by the metrics builder
     */
    public HttpServerMetricsImpl(@Nonnull final Sender sender, @Nonnull final StatfulMetricsOptions options) {
        super(sender, options);

        this.replacements = options.getPatterns().stream()
                .map(entry -> new Pair<>(Pattern.compile(entry.getLeft()), entry.getRight()))
                .collect(Collectors.toList());

        this.ignore = options.getHttpServerPathsIgnore().stream().map(Pattern::compile).collect(Collectors.toList());
    }

    @Override
    public HttpRequestMetrics requestBegin(final SocketAddress socketAddress, final HttpServerRequest request) {

        // run the ignore list to check if a url should be tracked or not
        final String requestPath = request.path();

        final HttpRequestMetrics metric;

        boolean shouldIgnore = this.ignore.stream().anyMatch(pattern -> pattern.matcher(requestPath).matches());
        if (shouldIgnore) {
            metric = null;
        } else {
            String path = request.path();
            // run the list of patterns to normalize the url and avoid creating to many tags, since the url will be used to identify as a TAG
            for (Pair<Pattern, String> entry : this.replacements) {
                path = entry.getLeft().matcher(path).replaceAll(entry.getRight());
            }

            // Create client request metric
            metric = new HttpRequestMetrics(path, socketAddress, request.method());
            metric.start();
        }

        return metric;
    }

    @Override
    public void responseEnd(final HttpRequestMetrics requestMetric, final HttpServerResponse response) {
        if (requestMetric == null) {
            return;
        }

        final long responseTime = requestMetric.elapsed();

        super.addMetric(
                new HttpServerDataPoint(getOptions(), "execution", requestMetric.getRequestTag(), requestMetric.getMethod(), responseTime,
                        response.getStatusCode(), HttpClientDataPoint.Type.SERVER)
        );
    }

    @Override
    public SocketAddress upgrade(final HttpRequestMetrics requestMetric, final ServerWebSocket serverWebSocket) {
        return requestMetric.getAddress();
    }

    @Override
    public SocketAddress connected(final SocketAddress socketMetric, final ServerWebSocket serverWebSocket) {
        return socketMetric;
    }

    @Override
    public void disconnected(final SocketAddress serverWebSocketMetric) {

    }

    @Override
    public SocketAddress connected(final SocketAddress remoteAddress, final String remoteName) {
        return remoteAddress;
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
    public void requestReset(final HttpRequestMetrics requestMetric) {

    }

    @Override
    public HttpRequestMetrics responsePushed(final SocketAddress socketMetric, final HttpMethod method, final String uri, final HttpServerResponse response) {
        return null;
    }
}
