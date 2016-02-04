package com.telemetron.sender;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.DataPoint;
import io.vertx.core.*;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Responsible for the UDP transport
 */
public final class UDPSender extends MetricsHolder {

    /**
     * Logger for transport errors
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPSender.class);

    /**
     * Telemetron options to get
     */
    private final TelemetronMetricsOptions options;

    /**
     * Holds the socket to avoid recreation
     */
    private DatagramSocket socket;

    /**
     * @param vertx   vertx instance to create the socket from
     * @param context of execution to run operations that need vertx initialized
     * @param options Telemetron options to configure host and port
     */
    public UDPSender(final Vertx vertx, final Context context, final TelemetronMetricsOptions options) {
        super(options, new Sampler(options, new Random()));

        this.options = options;

        // the following code is being executed asynchronously on the same context, to make sure that vertx is properly initialized
        // so that we can open a socket and configure a interval
        context.runOnContext(aVoid -> {
            this.socket = vertx.createDatagramSocket(new DatagramSocketOptions());
            this.configureFlushInterval(vertx, this.options.getFlushInterval(), this.options.getFlushSize());
        });
    }

    @Override
    public void send(@Nonnull final List<DataPoint> metrics, @Nonnull final Handler<AsyncResult<Void>> sentHandler) {
        this.send(metrics, Optional.of(sentHandler));
    }

    @Override
    public void send(@Nonnull final List<DataPoint> metrics) {
        this.send(metrics, Optional.empty());
    }

    private void send(@Nonnull final List<DataPoint> metrics, final Optional<Handler<AsyncResult<Void>>> endHandler) {
        Objects.requireNonNull(metrics);

        if (metrics.isEmpty()) {
            // nothing to send
            return;
        }
        final String toSendMetrics = metrics.stream().map(DataPoint::toMetricLine).collect(Collectors.joining("\n"));

        socket.send(toSendMetrics, options.getPort(), options.getHost(), handler -> {
            if (handler.failed()) {
                LOGGER.warn("Failed to send metrics {0}", toSendMetrics);
                endHandler.ifPresent(callerHandler -> callerHandler.handle(Future.failedFuture(handler.cause())));
            } else {
                endHandler.ifPresent(callerHandler -> callerHandler.handle(Future.succeededFuture()));
            }
        });
    }

    @Override
    public void close(final Handler<AsyncResult<Void>> handler) {
        this.socket.close(handler);
    }
}
