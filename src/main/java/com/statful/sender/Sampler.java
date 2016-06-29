package com.statful.sender;

import com.statful.client.StatfulMetricsOptions;

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
     * @param options StatfulMetricsOptions to extract the sample rate from
     * @param random  instance to generate numbers from
     */
    public Sampler(@Nonnull final StatfulMetricsOptions options, @Nonnull final Random random) {
        this.sampleRate = Objects.requireNonNull(options.getSampleRate());
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Calculates if a metric should be inserted or discarded based on the sampling frequency configured
     *
     * @return true if it should be inserted false if it should be discarded
     */
    public boolean shouldInsert() {
        // while this "if" could be simplified this ensures that the random calculation is only done if truly required
        return sampleRate == StatfulMetricsOptions.MAX_SAMPLE_RATE || this.random.nextInt(StatfulMetricsOptions.MAX_SAMPLE_RATE) <= this.sampleRate;
    }
}
