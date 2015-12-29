package com.telemetron.client;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.*;
import io.vertx.core.spi.metrics.*;

/**
 * VertxMetrics SPI implementation for Telemetron metrics collection
 */
public final class VertxMetricsImpl implements VertxMetrics {

    /**
     * Internal logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxMetricsImpl.class);

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
     * @param vertx vertx instance to share context
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
    public void verticleDeployed(final Verticle verticle) {
        LOGGER.info("verticle deployed {0}", verticle.getClass().getName());

    }

    /**
     * @inheritDoc
     */
    @Override
    public void verticleUndeployed(final Verticle verticle) {
        LOGGER.info("verticle undeployed {0}", verticle.getClass().getName());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void timerCreated(final long id) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public void timerEnded(final long id, final boolean cancelled) {

    }

    /**
     * @inheritDoc
     */
    @Override
    public EventBusMetrics createMetrics(final EventBus eventBus) {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public HttpServerMetrics<?, ?, ?> createMetrics(final HttpServer server, final SocketAddress localAddress, final HttpServerOptions options) {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public HttpClientMetrics<?, ?, ?> createMetrics(final HttpClient client, final HttpClientOptions options) {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TCPMetrics<?> createMetrics(final NetServer server, final SocketAddress localAddress, final NetServerOptions options) {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TCPMetrics<?> createMetrics(final NetClient client, final NetClientOptions options) {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public DatagramSocketMetrics createMetrics(final DatagramSocket socket, final DatagramSocketOptions options) {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isMetricsEnabled() {
        return this.telemetronOptions.isEnabled();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isEnabled() {
        return this.telemetronOptions.isEnabled();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() {

    }
}
