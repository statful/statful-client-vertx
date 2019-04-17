package com.statful.client;

import com.google.common.base.Strings;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.impl.FileResolver;
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
 * Factory that provides a StatfulMetrics instance.
 */
public class StatfulMetricsFactoryImpl implements VertxMetricsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatfulMetricsFactoryImpl.class);

    @Override
    public final VertxMetrics metrics(@Nonnull final VertxOptions options) {
        final MetricsOptions metricsOptions = options.getMetricsOptions();

        final StatfulMetricsOptions statfulMetricsOptions;
        if (metricsOptions instanceof StatfulMetricsOptions) {
            statfulMetricsOptions = (StatfulMetricsOptions) metricsOptions;
        } else {
            statfulMetricsOptions = new StatfulMetricsOptions();
        }

        // check if there is a configuration path set, if so, load configuration from file
        final StatfulMetricsOptions effective;
        String configPath = statfulMetricsOptions.getConfigPath();
        if (Strings.isNullOrEmpty(configPath)) {
            effective = statfulMetricsOptions;
        } else {
            effective = buildFromFile(configPath);
        }

        return new VertxMetricsImpl(effective);
    }

    /**
     * Loads configuration from a file
     *
     * @throws RuntimeException if the file is invalid or non existent
     */
    private StatfulMetricsOptions buildFromFile(final String configPath) {

        FileResolver fileResolver = new FileResolver();
        File file = fileResolver.resolveFile(configPath);

        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\A");
            String metricsConfigString = scanner.next();
            return new StatfulMetricsOptions(new JsonObject(metricsConfigString));
        } catch (IOException | DecodeException exception) {
            LOGGER.error("Error while reading metrics config file", exception);
            throw new RuntimeException("wrong configuration provided");
        }
    }

    @Override
    public final MetricsOptions newOptions() {
        return new StatfulMetricsOptions();
    }
}
