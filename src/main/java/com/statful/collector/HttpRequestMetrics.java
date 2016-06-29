package com.statful.collector;


import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.SocketAddress;

/**
 * HTTP request monitor class.
 */
public final class HttpRequestMetrics extends TimerMetric {

    /**
     * Request address target
     */
    private final SocketAddress address;
    /**
     * Http verb for the request
     */
    private final HttpMethod method;

    /**
     * @param requestTag    String with the tag to be applied to identify this request
     * @param remoteAddress target remote address
     * @param method        http verb for the request
     */
    public HttpRequestMetrics(final String requestTag, final SocketAddress remoteAddress, final HttpMethod method) {
        super(requestTag);
        this.address = remoteAddress;
        this.method = method;
    }

    /**
     * @return address of the http request
     */
    public SocketAddress getAddress() {
        return address;
    }

    /**
     * @return String representation of the http verb for this request
     */
    public String getMethod() {
        return method.toString();
    }
}
