package com.statful.client;

import com.statful.sender.Sender;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.vertx.testtools.VertxAssert.assertNotNull;


public class VertxMetricsImplTest {

    private StatfulMetricsOptions statfulMetricsOptions;

    private Sender sender;

    @Before
    public void setup() {

        this.sender = mock(Sender.class);
        this.statfulMetricsOptions = mock(StatfulMetricsOptions.class);
    }


    @Test
    public void testHttpClientMetricCreation() {
        VertxMetricsImpl victim = new VertxMetricsImpl(sender, statfulMetricsOptions);
        HttpClientMetrics createdMetrics = victim.createMetrics(mock(HttpClient.class), mock(HttpClientOptions.class));
        assertNotNull(createdMetrics);
    }
}