package com.statful.collector;

/**
 * shared logic between timer metrics classes
 */
public abstract class TimerMetric {
    /**
     * Tag to be applied identifying the request
     */
    private final String requestTag;
    /**
     * System timestamp of request start
     */
    private long start;

    /**
     * @param requestTag identifier for this metric
     */
    public TimerMetric(final String requestTag) {
        this.requestTag = requestTag;
    }

    /**
     * initializes the timer
     */
    public void start() {
        this.start = System.currentTimeMillis();
    }

    /**
     * @return long with the time from start up until now
     */
    public long elapsed() {
        return System.currentTimeMillis() - start;
    }

    /**
     * @return String with tag value to be sent to statful
     */
    public String getRequestTag() {
        return requestTag;
    }
}
