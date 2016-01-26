package com.telemetron.client;

import com.google.common.base.Strings;
import com.telemetron.sender.Sender;
import com.telemetron.sender.SenderFactory;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.FileResolver;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.VertxMetrics;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Factory that provides a TelemetronMetrics instance.
 */
public class TelemetronMetricsFactoryImpl implements VertxMetricsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetronMetricsFactoryImpl.class);

    @Override
    public final VertxMetrics metrics(@Nonnull final Vertx vertx, @Nonnull final VertxOptions options) {
        final MetricsOptions metricsOptions = options.getMetricsOptions();

        final TelemetronMetricsOptions telemetronMetricsOptions;
        if (metricsOptions instanceof TelemetronMetricsOptions) {
            telemetronMetricsOptions = (TelemetronMetricsOptions) metricsOptions;
        } else {
            telemetronMetricsOptions = new TelemetronMetricsOptions();
        }

        // check if there is a configuration path set, if so, load configuration from file
        final TelemetronMetricsOptions effective;
        String configPath = telemetronMetricsOptions.getConfigPath();
        if (Strings.isNullOrEmpty(configPath)) {
            effective = telemetronMetricsOptions;
        } else {
            effective = buildFromFile(vertx, configPath);
        }

        final Context context = vertx.getOrCreateContext();
        final Sender sender = new SenderFactory().create(vertx, context, effective);

        return new VertxMetricsImpl(sender, effective);
    }

    /**
     * Loads configuration from a file
     * @throws RuntimeException if the file is invalid or non existent
     */
    private TelemetronMetricsOptions buildFromFile(final Vertx vertx, final String configPath) {

        FileResolver fileResolver = new FileResolver(vertx);
        File file = fileResolver.resolveFile(configPath);

        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\A");
            String metricsConfigString = scanner.next();
            return new TelemetronMetricsOptions(new JsonObject(metricsConfigString));
        } catch (IOException | DecodeException exception) {
            LOGGER.error("Error while reading metrics config file", exception);
            throw new RuntimeException("wrong configuration provided");
        }
    }

    @Override
    public final MetricsOptions newOptions() {
        return new TelemetronMetricsOptions();
    }
}
