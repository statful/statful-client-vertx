package com.statful.client;

import com.google.common.collect.Lists;
import com.statful.collector.*;
import com.statful.sender.Sender;
import com.statful.sender.SenderFactory;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.metrics.impl.DummyVertxMetrics;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.core.spi.metrics.HttpServerMetrics;
import io.vertx.core.spi.metrics.PoolMetrics;

import java.util.List;

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
     * Holds all collectors created to lazy load the client
     */
    private final List<StatfulMetrics> collectors;

    /**
     * Constructor to be used for configuration and creation of a sender
     *
     * @param vertx                 vertx instance
     * @param statfulMetricsOptions configuration object
     */
    VertxMetricsImpl(final Vertx vertx, final StatfulMetricsOptions statfulMetricsOptions) {
        this.vertx = vertx;
        this.statfulMetricsOptions = statfulMetricsOptions;
        this.collectors = Lists.newLinkedList();
    }

    @Override
    public HttpClientMetrics<HttpRequestMetrics, SocketAddress, SocketAddress, Void, Void> createMetrics(final HttpClient client,
                                                                                                         final HttpClientOptions options) {
        HttpClientMetricsImpl httpClientMetricsImpl = new HttpClientMetricsImpl(statfulMetricsOptions);
        this.collectors.add(httpClientMetricsImpl);
        this.collectors.forEach(collector -> collector.setSender(this.getOrCreateSender()));
        return httpClientMetricsImpl;
    }

    @Override
    public HttpServerMetrics createMetrics(final HttpServer server, final SocketAddress localAddress, final HttpServerOptions options) {
        HttpServerMetricsImpl httpServerMetricsImpl = new HttpServerMetricsImpl(statfulMetricsOptions);
        this.collectors.add(httpServerMetricsImpl);
        this.collectors.forEach(collector -> collector.setSender(this.getOrCreateSender()));

        return httpServerMetricsImpl;
    }

    @Override
    public <P> PoolMetrics<?> createMetrics(final P pool, final String poolType, final String poolName, final int maxPoolSize) {

        // When creating pool metrics we cannot create the sender because vertx is not completely initialized
        // This is an hack and the reporter will need to be decoupled from the collectors
        PoolMetricsImpl poolMetrics = new PoolMetricsImpl(statfulMetricsOptions, vertx, poolType, poolName, maxPoolSize);
        this.collectors.add(poolMetrics);
        return poolMetrics;
    }

    @Override
    public boolean isEnabled() {
        return this.statfulMetricsOptions.isEnabled();
    }

    @Override
    public void eventBusInitialized(final EventBus bus) {
        this.getOrCreateSender();
    }

    private Sender getOrCreateSender() {
        if (this.sender == null) {
            this.sender = new SenderFactory().create(this.vertx, this.vertx.getOrCreateContext(), statfulMetricsOptions);
        }
        return sender;
    }

}
