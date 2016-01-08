package com.telemetron.client;

import com.telemetron.sender.Sender;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.vertx.testtools.VertxAssert.assertNotNull;


public class VertxMetricsImplTest {

    private TelemetronMetricsOptions telemetronMetricsOptions;

    private Sender sender;

    @Before
    public void setup() {

        this.sender = mock(Sender.class);
        this.telemetronMetricsOptions = mock(TelemetronMetricsOptions.class);
    }


    @Test
    public void testHttpClientMetricCreation() {
        VertxMetricsImpl victim = new VertxMetricsImpl(sender, telemetronMetricsOptions);
        HttpClientMetrics createdMetrics = victim.createMetrics(mock(HttpClient.class), mock(HttpClientOptions.class));
        assertNotNull(createdMetrics);
    }
}