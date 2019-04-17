package com.statful.client;

import com.statful.collector.*;
import com.statful.sender.Sender;
import com.statful.sender.SenderFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.core.spi.metrics.HttpServerMetrics;
import io.vertx.core.spi.metrics.PoolMetrics;
import io.vertx.core.spi.metrics.VertxMetrics;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * VertxMetrics SPI implementation for statful metrics collection
 */
final class VertxMetricsImpl implements VertxMetrics {

    /**
     * Collectors and client configuration
     */
    private final StatfulMetricsOptions statfulMetricsOptions;

    /**
     * Instance of a sender to push metrics to statful.
     */
    private Sender sender;

    /**
     * Holds all collectors created to lazy load the client.
     * Since metrics can be created by different eventloop threads or worker threads we need handle concurrency here
     */
    private final LinkedBlockingQueue<StatfulMetrics> collectors;

    /**
     * Custom metrics consumer
     */
    private CustomMetricsConsumer customMetricsConsumer;

    /**
     * Vertx instance used for periodic timers and senders
     */
    private Vertx vertx;

    /**
     * Constructor to be used for configuration and creation of a sender
     *
     * @param statfulMetricsOptions configuration object
     */
    VertxMetricsImpl(final StatfulMetricsOptions statfulMetricsOptions) {
        this.statfulMetricsOptions = statfulMetricsOptions;
        this.collectors = new LinkedBlockingQueue<>();
    }

    @Override
    public HttpServerMetrics<HttpRequestMetrics, SocketAddress, SocketAddress> createHttpServerMetrics(final HttpServerOptions options,
                                                                                                       final SocketAddress localAddress) {
        HttpServerMetricsImpl httpServerMetrics = null;
        if (statfulMetricsOptions.isEnableHttpServerMetrics()) {
            httpServerMetrics = new HttpServerMetricsImpl(statfulMetricsOptions);
            httpServerMetrics.setSender(this.getOrCreateSender(vertx));
        }
        return httpServerMetrics;
    }

    @Override
    public HttpClientMetrics<HttpRequestMetrics, SocketAddress, SocketAddress, Void, Void> createHttpClientMetrics(final HttpClientOptions options) {
        HttpClientMetricsImpl httpClientMetrics = null;
        if (statfulMetricsOptions.isEnableHttpClientMetrics()) {
            httpClientMetrics = new HttpClientMetricsImpl(statfulMetricsOptions);
            httpClientMetrics.setSender(this.getOrCreateSender(vertx));
        }
        return httpClientMetrics;
    }

    @Override
    public PoolMetrics<?> createPoolMetrics(final String poolType, final String poolName, final int maxPoolSize) {
        PoolMetricsImpl poolMetrics = null;
        if (statfulMetricsOptions.isEnablePoolMetrics()) {
            poolMetrics = new PoolMetricsImpl(statfulMetricsOptions, poolType, poolName, maxPoolSize);
            collectors.add(poolMetrics);
        }
        return poolMetrics;
    }

    @Override
    public void vertxCreated(final Vertx createdVertx) {
        this.vertx = createdVertx;
        this.customMetricsConsumer = new CustomMetricsConsumer(createdVertx.eventBus(), this.getOrCreateSender(createdVertx), statfulMetricsOptions);
        this.collectors.forEach(collector -> {
            collector.setVertx(createdVertx);
            collector.setSender(this.getOrCreateSender(createdVertx));
        });
    }

    private Sender getOrCreateSender(final Vertx senderVertx) {
        if (this.sender == null) {
            this.sender = new SenderFactory().create(senderVertx, statfulMetricsOptions);
        }
        return sender;
    }
}
