package com.telemetron.collector;


import io.vertx.core.net.SocketAddress;

/**
 * HTTP request monitor class.
 */
public final class HttpClientRequestMetrics {

    /**
     * Tag to be applied identifying the request
     */
    private final String requestTag;
    /**
     * Request address target
     */
    private final SocketAddress address;

    /**
     * System timestamp of request start
     */
    private long start;

    /**
     * @param requestTag    String with the tag to be applied to identify this request
     * @param remoteAddress target remote address
     */
    public HttpClientRequestMetrics(final String requestTag, final SocketAddress remoteAddress) {
        this.requestTag = requestTag;
        this.address = remoteAddress;
    }

    /**
     * initializes the timer
     */
    public void start() {
        this.start = System.nanoTime();
    }

    /**
     * @return long with the time from start up until now
     */
    public long elapsed() {
        return System.nanoTime() - start;
    }

    /**
     * @return address of the http request
     */
    public SocketAddress getAddress() {
        return address;
    }
}
