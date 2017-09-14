package com.statful.metric;

import com.statful.client.StatfulMetricsOptions;

/**
 * Representation of an HttpServer DataPoint. Holds the that of a metric and builds
 */
public final class HttpServerDataPoint extends HttpDataPoint {

    /**
     * @param options      Statful options to be used when building the metric line
     * @param metricName   name of the metric
     * @param name         Name of the operation that you are tracking
     * @param httpVerb     Representation of the http verb request
     * @param duration     Duration of the request
     * @param responseCode Http code to be added as tag
     * @param type         if this metric belongs to http server or client
     */
    public HttpServerDataPoint(final StatfulMetricsOptions options, final String metricName, final String name, final String httpVerb,
                               final String duration, final int responseCode, final Type type) {
        super(options, metricName, name, httpVerb, duration, responseCode, type);
    }

    @Override
    public String toMetricLine() {
        return super.buildMetricLine()
                .withTag("route", this.getName())
                .build();
    }

}
