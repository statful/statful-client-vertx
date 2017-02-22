package com.statful.sender;

import com.statful.client.StatfulMetricsOptions;
import com.statful.client.Transport;
import io.vertx.core.Context;
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
     * @param vertx vertx instance to be used by the sender
     * @param context of execution to run operations that need vertx initialized
     * @param options statful options to configure the sender
     * @return correct client instance for the configuration provided
     */
    @Nonnull
    public Sender create(@Nonnull final Vertx vertx, final Context context,  @Nonnull final StatfulMetricsOptions options) {

        Objects.requireNonNull(vertx);
        Objects.requireNonNull(context);
        Objects.requireNonNull(options);

        Transport transport = options.getTransport();
        if (Transport.UDP.equals(transport)) {
            LOGGER.info("creating udp sender");
            return new UDPSender(vertx, context, options);
        } else if (Transport.HTTP.equals(transport)) {
            LOGGER.info("creating http sender");
            return new HttpSender(vertx, context, options);
        }
        throw new UnsupportedOperationException("currently only UDP and HTTP are supported. Requested: " + options.getTransport());
    }
}
