package com.statful.client;

import com.statful.sender.Sender;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.vertx.testtools.VertxAssert.assertNotNull;


public class VertxMetricsImplTest {

    private StatfulMetricsOptions statfulMetricsOptions;

    private Context context;

    private Vertx vertx;

    @Before
    public void setup() {
        this.vertx = mock(Vertx.class);
        this.context = mock(Context.class);
        when(this.vertx.getOrCreateContext()).thenReturn(this.context);
        this.statfulMetricsOptions = mock(StatfulMetricsOptions.class);
        when(statfulMetricsOptions.getTransport()).thenReturn(Transport.UDP);
        when(statfulMetricsOptions.getMaxBufferSize()).thenReturn(5000);
    }


    @Test
    public void testHttpClientMetricCreation() {
        VertxMetricsImpl victim = new VertxMetricsImpl(vertx, statfulMetricsOptions);
        HttpClientMetrics createdMetrics = victim.createMetrics(mock(HttpClient.class), mock(HttpClientOptions.class));
        assertNotNull(createdMetrics);
    }
}