package com.statful.client;

import com.statful.collector.HttpClientMetricsImpl;
import com.statful.collector.HttpRequestMetrics;
import com.statful.collector.HttpServerMetricsImpl;
import com.statful.sender.Sender;
import com.statful.sender.SenderFactory;
import io.vertx.core.Vertx;
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
final class VertxMetricsImpl extends DummyVertxMetrics {

    /**
     * Collectors and client configuration
     */
    private final StatfulMetricsOptions statfulMetricsOptions;

    /**
     * Instance of a sender to push metrics to statful.
     */
    private Sender sender;

    /**
     * Vertx instance to be used to create the sender
     */
    private final Vertx vertx;

    /**
     * Constructor to be used for configuration and creation of a sender
     *
     * @param vertx                 vertx instance
     * @param statfulMetricsOptions configuration object
     */
    VertxMetricsImpl(final Vertx vertx, final StatfulMetricsOptions statfulMetricsOptions) {
        this.vertx = vertx;
        this.statfulMetricsOptions = statfulMetricsOptions;
    }

    @Override
    public HttpClientMetrics<HttpRequestMetrics, SocketAddress, SocketAddress, Void, Void> createMetrics(final HttpClient client,
                                                                                                         final HttpClientOptions options) {
        return new HttpClientMetricsImpl(this.getOrCreateSender(), statfulMetricsOptions);
    }

    @Override
    public HttpServerMetrics createMetrics(final HttpServer server, final SocketAddress localAddress, final HttpServerOptions options) {
        return new HttpServerMetricsImpl(this.getOrCreateSender(), statfulMetricsOptions);
    }

    @Override
    public boolean isEnabled() {
        return this.statfulMetricsOptions.isEnabled();
    }

    private Sender getOrCreateSender() {
        if (this.sender == null) {
            this.sender = new SenderFactory().create(this.vertx, this.vertx.getOrCreateContext(), statfulMetricsOptions);
        }
        return sender;
    }

}
