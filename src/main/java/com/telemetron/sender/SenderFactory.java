package com.telemetron.sender;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.client.Transport;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Factory responsible for creating a sender instance to push metrics to Telemetron
 */
public final class SenderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderFactory.class);

    /**
     * @param vertx vertx instance to be used by the sender
     * @param options telemetron options to configure the sender
     * @return correct client instance for the configuration provided
     */
    @Nonnull
    public Sender create(@Nonnull final Vertx vertx, @Nonnull final TelemetronMetricsOptions options) {

        Objects.requireNonNull(vertx);
        Objects.requireNonNull(options);

        Transport transport = options.getTransport();
        if (Transport.UDP.equals(transport)) {
            LOGGER.info("creating udp sender");
            return new UDPSender(vertx, options);
        }
        throw new UnsupportedOperationException("currently only UDP is supported. Requested: " + options.getTransport());
    }
}
