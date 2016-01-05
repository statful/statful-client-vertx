package com.telemetron.client;

import com.telemetron.collector.HttpClientMetricsImpl;
import com.telemetron.collector.HttpClientRequestMetrics;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.metrics.impl.DummyVertxMetrics;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;

/**
 * VertxMetrics SPI implementation for Telemetron metrics collection
 * Extending DummyVertxMetrics to avoid having to extend all methods even if we don't want to implement them
 */
public final class VertxMetricsImpl extends DummyVertxMetrics {

    /**
     * Vertx instance
     */
    private final Vertx vertx;

    /**
     * Collectors and client configuration
     */
    private final TelemetronMetricsOptions telemetronOptions;

    /**
     * Constructor to be used for configuration
     *
     * @param vertx             vertx instance to share context
     * @param telemetronOptions configuration object
     */
    public VertxMetricsImpl(final Vertx vertx, final TelemetronMetricsOptions telemetronOptions) {
        this.vertx = vertx;
        this.telemetronOptions = telemetronOptions;
    }

    /**
     * @inheritDoc
     */
    @Override
    public HttpClientMetrics<HttpClientRequestMetrics, SocketAddress, SocketAddress> createMetrics(final HttpClient client, final HttpClientOptions options) {
        return new HttpClientMetricsImpl();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isEnabled() {
        return this.telemetronOptions.isEnabled();
    }
}
