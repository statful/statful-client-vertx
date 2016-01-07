package com.telemetron.metric;

import com.google.common.collect.Lists;
import com.telemetron.client.Aggregation;
import com.telemetron.client.AggregationFreq;
import com.telemetron.client.TelemetronMetricsOptions;


/**
 * Representation of an HttpClient Datapoint. Holds the that of a metric and builds
 */
public final class HttpClientDataPoint implements DataPoint {

    /**
     * constant to transform milliseconds in unix timestamp
     */
    private static final long TIMESTAMP_DIVIDER = 1000L;

    /**
     * Telemetron options to be used when building the metric line
     */
    private final TelemetronMetricsOptions options;

    /**
     * Name of the operation that you are tracking
     */
    private final String name;

    /**
     * Representation of the http verb request
     */
    private final String verb;

    /**
     * Duration of the request
     */
    private final long duration;

    /**
     * Http code to be added as tag
     */
    private final int responseCode;

    /**
     * @param options      Telemetron options to be used when building the metric line
     * @param name         Name of the operation that you are tracking
     * @param httpVerb     Representation of the http verb request
     * @param duration     Duration of the request
     * @param responseCode Http code to be added as tag
     */
    public HttpClientDataPoint(final TelemetronMetricsOptions options, final String name, final String httpVerb, final long duration, final int responseCode) {

        this.options = options;
        this.name = name;
        this.verb = httpVerb;
        this.duration = duration;
        this.responseCode = responseCode;
    }

    @Override
    public String toMetricLine() {

        return new MetricLineBuilder()
                .withPrefix(this.options.getPrefix())
                .withNamespace(this.options.getNamespace())
                .withMetricName("timer")
                .withTag("request", this.name)
                .withTag("verb", this.verb)
                .withTag("statusCode", String.valueOf(this.responseCode))
                .withValue(String.valueOf(this.duration))
                .withTimestamp(getUnixTimeStamp())
                .withAggregations(Lists.newArrayList(Aggregation.P95))
                .withAggregationFrequency(AggregationFreq.FREQ_10)
                .build();
    }

    /**
     * @return unix timestamp value
     */
    private long getUnixTimeStamp() {
        return System.currentTimeMillis() / TIMESTAMP_DIVIDER;
    }
}
