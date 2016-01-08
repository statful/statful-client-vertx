package com.telemetron.collector;


import io.vertx.core.http.HttpMethod;
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
     * Http verb for the request
     */
    private final HttpMethod method;

    /**
     * System timestamp of request start
     */
    private long start;

    /**
     * @param requestTag    String with the tag to be applied to identify this request
     * @param remoteAddress target remote address
     * @param method        http verb for the request
     */
    public HttpClientRequestMetrics(final String requestTag, final SocketAddress remoteAddress, final HttpMethod method) {
        this.requestTag = requestTag;
        this.address = remoteAddress;
        this.method = method;
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

    /**
     * @return String with tag value to be sent to telemetron
     */
    public String getRequestTag() {
        return requestTag;
    }

    /**
     * @return String representation of the http verb for this request
     */
    public String getMethod() {
        return method.toString();
    }
}
