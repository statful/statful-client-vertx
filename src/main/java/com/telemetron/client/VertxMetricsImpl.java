package com.telemetron.client;

import com.telemetron.collector.HttpClientMetricsImpl;
import com.telemetron.collector.HttpClientRequestMetrics;
import com.telemetron.sender.Sender;
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
     * Collectors and client configuration
     */
    private final TelemetronMetricsOptions telemetronOptions;

    /**
     * Instance of a sender to push metrics to telemtron
     */
    private final Sender sender;

    /**
     * Constructor to be used for configuration and creation of a sender
     *
     * @param sender            client to be used to push metrics to telemetron
     * @param telemetronOptions configuration object
     */
    public VertxMetricsImpl(final Sender sender, final TelemetronMetricsOptions telemetronOptions) {
        this.telemetronOptions = telemetronOptions;
        this.sender = sender;
    }

    /**
     * @inheritDoc
     */
    @Override
    public HttpClientMetrics<HttpClientRequestMetrics, SocketAddress, SocketAddress> createMetrics(final HttpClient client, final HttpClientOptions options) {
        return new HttpClientMetricsImpl(sender, telemetronOptions);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isEnabled() {
        return this.telemetronOptions.isEnabled();
    }
}
