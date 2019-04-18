package com.statful.sender;

import com.statful.client.StatfulMetricsOptions;
import com.statful.client.Transport;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Factory responsible for creating a sender instance to push metrics to Statful
 */
public final class SenderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderFactory.class);

    /**
     * @param vertx Vertx instance to create the senders
     * @param options statful options to configure the sender
     * @return correct client instance for the configuration provided
     */
    @Nonnull
    public Sender create(final Vertx vertx, @Nonnull final StatfulMetricsOptions options) {
        Objects.requireNonNull(options);

        Transport transport = options.getTransport();
        if (Transport.UDP.equals(transport)) {
            LOGGER.info("creating udp sender");
            return new UDPSender(vertx, options);
        } else if (Transport.HTTP.equals(transport)) {
            LOGGER.info("creating http sender");
            return new HttpSender(vertx, options);
        }
        throw new UnsupportedOperationException("currently only UDP and HTTP are supported. Requested: " + options.getTransport());
    }
}
