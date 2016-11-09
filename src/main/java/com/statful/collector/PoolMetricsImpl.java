package com.statful.collector;

import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.PoolDataPoint;
import com.statful.sender.Sender;
import io.vertx.core.Vertx;
import io.vertx.core.spi.metrics.PoolMetrics;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.LongAdder;

/**
 * Handle Pools metrics
 */
public class PoolMetricsImpl extends StatfulMetrics implements PoolMetrics<Long> {

    /**
     * Long to store the current periodic id
     */
    private long periodicTimerId;

    /**
     * Adder to keep counter of in use connections to the pool
     */
    private LongAdder inUse = new LongAdder();
    /**
     * Adder to keep counter of queued request to obtain a connection from the pool
     */
    private LongAdder queued = new LongAdder();

    /**
     * Stores maximum size of the pool
     */
    private final String maxPoolSize;

    /**
     * Name for the pool
     */
    private final String name;

    /**
     * Vertx instance to be used to create the period to flush gauges
     */
    private final Vertx vertx;

    /**
     * @param options     options to latter be used by the metrics builder
     * @param vertx       vertx instance to kick start the collector
     * @param poolType    type of the pool
     * @param poolName    name of the pool
     * @param maxPoolSize value of the maximum pool size
     */
    public PoolMetricsImpl(@Nonnull final StatfulMetricsOptions options,
                           @Nonnull final Vertx vertx,
                           @Nonnull final String poolType,
                           @Nonnull final String poolName,
                           final int maxPoolSize) {
        super(options);
        this.vertx = vertx;
        this.maxPoolSize = String.valueOf(maxPoolSize);
        this.name = poolType + "." + poolName;
    }

    @Override
    public Long submitted() {
        this.queued.increment();
        return System.currentTimeMillis();
    }

    @Override
    public Long begin(final Long aLong) {
        this.queued.decrement();
        this.inUse.increment();
        return System.currentTimeMillis();
    }

    @Override
    public void rejected(final Long aLong) {
        this.queued.decrement();
    }

    @Override
    public void end(final Long aLong, final boolean succeeded) {
        this.inUse.decrement();
    }

    @Override
    public boolean isEnabled() {
        return this.getOptions().isEnablePoolMetrics();
    }

    @Override
    public void close() {
        this.vertx.cancelTimer(this.periodicTimerId);
    }

    @Override
    public void setSender(@Nonnull final Sender sender) {
        if (!this.hasSender() && this.isEnabled()) {
            super.setSender(sender);
            this.initReporter();
        }
    }

    private void initReporter() {
        this.periodicTimerId = this.vertx.setPeriodic(this.getOptions().getGaugeReportingInterval(), event -> {
            this.addMetric(new PoolDataPoint(this.getOptions(), name, "inUse", String.valueOf(this.inUse.longValue())));
            this.addMetric(new PoolDataPoint(this.getOptions(), name, "queued", String.valueOf(this.queued.longValue())));
            this.addMetric(new PoolDataPoint(this.getOptions(), name, "max", this.maxPoolSize));
        });
    }
}
