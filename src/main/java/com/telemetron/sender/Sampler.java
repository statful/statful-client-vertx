package com.telemetron.sender;

import com.telemetron.client.TelemetronMetricsOptions;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

/**
 * Provides a way to perform sampling on metrics
 */
public final class Sampler implements Sampling {

    /**
     * Sample rate to be applied
     */
    private final int sampleRate;

    /**
     * Random instance to generate integers from
     */
    private final Random random;

    /**
     * @param options TelemetronMetricsOptions to extract the sample rate from
     * @param random instance to generate numbers from
     */
    public Sampler(@Nonnull final TelemetronMetricsOptions options, @Nonnull final Random random) {
        this.sampleRate = Objects.requireNonNull(options.getSampleRate());
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Calculates if a metric should be inserted or discarded based on the sampling frequency configured
     *
     * @return true if it should be inserted false if it should be discarded
     */
    public boolean shouldInsert() {
        return sampleRate == TelemetronMetricsOptions.MAX_SAMPLE_RATE || this.random.nextInt(TelemetronMetricsOptions.MAX_SAMPLE_RATE) <= this.sampleRate;
    }
}
