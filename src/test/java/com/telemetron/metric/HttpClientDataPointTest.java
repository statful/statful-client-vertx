package com.telemetron.metric;

import com.google.common.collect.Lists;
import com.telemetron.client.Aggregation;
import com.telemetron.client.AggregationFreq;
import com.telemetron.client.TelemetronMetricsOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpClientDataPointTest {

    private TelemetronMetricsOptions options;

    @Before
    public void setup() {
        this.options = mock(TelemetronMetricsOptions.class);
        when(this.options.getPrefix()).thenReturn("prefix");
        when(this.options.getNamespace()).thenReturn("namespace");
        when(this.options.getTimerAggregations()).thenReturn(Lists.newArrayList(Aggregation.P95));
        when(this.options.getTimerFrequency()).thenReturn(AggregationFreq.FREQ_10);
        when(this.options.getApp()).thenReturn(Optional.empty());
    }

    @Test
    public void testMetricLine() {
        HttpClientDataPoint victim = new HttpClientDataPoint(this.options, "name", "verb", 1000, 200, HttpClientDataPoint.Type.CLIENT);
        
        // using a regex for match since the metric will include a timestamp that we don't really want to test here
        final String expected = "prefix\\.namespace\\.timer,request=name,verb=verb,transport=http,type=client,statusCode=200 1000 \\d.* p95,FREQ_10";
        final String actual = victim.toMetricLine();
        
        Matcher matcher = Pattern.compile(expected).matcher(victim.toMetricLine());
        assertTrue("\nexpected: " + expected + "\nactual: " + actual + "\n", matcher.matches());
    }
}