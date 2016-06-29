package com.statful.sender;

/**
 * To be implemented by classes that calculate a sampling rate
 */
public interface Sampling {
    /**
     * Calculates if a metric should be inserted or discarded based on the sampling frequency configured
     *
     * @return true if it should be inserted false if it should be discarded
     */
    boolean shouldInsert();
}
