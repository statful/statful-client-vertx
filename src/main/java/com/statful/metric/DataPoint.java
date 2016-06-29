package com.statful.metric;

/**
 * Should be implemented by all classes that provide metric data
 */
public interface DataPoint {

    /**
     * Should return a metric line ready to be sent to statful
     * <b>Implementations should not add line break</b>
     *
     * @return String with the metric value
     */
    String toMetricLine();
}
