package com.statful.metric;

import com.google.common.collect.Lists;
import com.statful.client.Aggregation;
import com.statful.client.AggregationFreq;
import com.statful.client.StatfulMetricsOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpClientDataPointTest {

    private StatfulMetricsOptions options;

    @Before
    public void setup() {
        this.options = mock(StatfulMetricsOptions.class);
        when(this.options.getNamespace()).thenReturn("namespace");
        when(this.options.getTimerAggregations()).thenReturn(Lists.newArrayList(Aggregation.P95));
        when(this.options.getTimerFrequency()).thenReturn(AggregationFreq.FREQ_10);
        when(this.options.getApp()).thenReturn(Optional.empty());
        when(this.options.getSampleRate()).thenReturn(StatfulMetricsOptions.MAX_SAMPLE_RATE);
    }

    @Test
    public void testMetricLine() {

        HttpClientDataPoint victim = new HttpClientDataPoint(this.options,"execution", "name", "verb", 1000, 200, HttpClientDataPoint.Type.CLIENT);
        
        // using a regex for match since the metric will include a timestamp that we don't really want to test here
        final String expected = "namespace\\.timer\\.execution,request=name,verb=verb,transport=http,type=client,statusCode=200 1000 \\d.* p95,10 100";
        final String actual = victim.toMetricLine();
        
        Matcher matcher = Pattern.compile(expected).matcher(victim.toMetricLine());
        assertTrue("\nexpected: " + expected + "\nactual: " + actual + "\n", matcher.matches());
    }
}