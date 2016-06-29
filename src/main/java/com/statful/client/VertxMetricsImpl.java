package com.statful.client;

import com.statful.collector.HttpClientMetricsImpl;
import com.statful.collector.HttpRequestMetrics;
import com.statful.collector.HttpServerMetricsImpl;
import com.statful.sender.Sender;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.metrics.impl.DummyVertxMetrics;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.core.spi.metrics.HttpServerMetrics;

/**
 * VertxMetrics SPI implementation for statful metrics collection
 * Extending DummyVertxMetrics to avoid having to extend all methods even if we don't want to implement them
 */
public final class VertxMetricsImpl extends DummyVertxMetrics {

    /**
     * Collectors and client configuration
     */
    private final StatfulMetricsOptions statfulMetricsOptions;

    /**
     * Instance of a sender to push metrics to statful
     */
    private final Sender sender;

    /**
     * Constructor to be used for configuration and creation of a sender
     *
     * @param sender            client to be used to push metrics to statful
     * @param statfulMetricsOptions configuration object
     */
    public VertxMetricsImpl(final Sender sender, final StatfulMetricsOptions statfulMetricsOptions) {
        this.statfulMetricsOptions = statfulMetricsOptions;
        this.sender = sender;
    }

    @Override
    public HttpClientMetrics<HttpRequestMetrics, SocketAddress, SocketAddress> createMetrics(final HttpClient client, final HttpClientOptions options) {
        return new HttpClientMetricsImpl(sender, statfulMetricsOptions);
    }

    @Override
    public HttpServerMetrics createMetrics(final HttpServer server, final SocketAddress localAddress, final HttpServerOptions options) {
        return new HttpServerMetricsImpl(sender, statfulMetricsOptions);
    }

    @Override
    public boolean isEnabled() {
        return this.statfulMetricsOptions.isEnabled();
    }
}
