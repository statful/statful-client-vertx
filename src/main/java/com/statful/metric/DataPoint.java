package com.statful.metric;

/**
 * Should be implemented by all classes that provide metric data
 */
public interface DataPoint {

    /**
     * constant to transform milliseconds in unix timestamp
     */
    long TIMESTAMP_DIVIDER = 1000L;

    /**
     * Should return a metric line ready to be sent to statful
     * <b>Implementations should not add line break</b>
     *
     * @return String with the metric value
     */
    String toMetricLine();

    /**
     * Calculates current unix timestamp
     * @return long with value
     */
    default long getUnixTimeStamp() {
        return System.currentTimeMillis() / TIMESTAMP_DIVIDER;
    }
}
