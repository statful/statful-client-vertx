package com.statful.client;

import com.statful.sender.Sender;
import io.vertx.core.eventbus.EventBus;

/**
 * Allows consumers to send custom metrics thought the event bus
 */
public final class CustomMetricsConsumer {

    /**
     * Event bus address for custom metrics
     */
    public static final String ADDRESS = "com.statful.client.custom.metrics";
    /**
     * Event bus to listen to metrics messages
     */
    private final EventBus eventBus;

    /**
     * Metrics sender
     */
    private final Sender sender;

    /**
     * Holds the options to enrich metrics latter
     */
    private StatfulMetricsOptions options;

    /**
     * @param eventBus event bus to listen the messages on
     * @param sender   client to send custom metrics to statful
     * @param options  statful metric options
     */
    public CustomMetricsConsumer(final EventBus eventBus, final Sender sender, final StatfulMetricsOptions options) {
        this.eventBus = eventBus;
        this.sender = sender;
        this.options = options;
        this.codecRegister();
        this.eventBusListener();
    }

    private void eventBusListener() {
        this.eventBus.<CustomMetric>consumer(ADDRESS, event -> {
            CustomMetric metric = event.body();
            // enrich metric with statful metrics options to allow latter building a metric line
            metric.setOptions(options);
            this.sender.addMetric(event.body());
        });
    }

    private void codecRegister() {
        this.eventBus.registerDefaultCodec(CustomMetric.class, new CustomMetric.CustomMetricCodec());
    }
}
