package com.telemetron.client;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.vertx.testtools.VertxAssert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class VertxMetricsImplTest {

    private Vertx vertx;

    private TelemetronMetricsOptions telemetronMetricsOptions;

    @Before
    public void setup() {

        telemetronMetricsOptions = new TelemetronMetricsOptions();
        telemetronMetricsOptions.setEnabled(true);

        // create and configure a configuration with enabled metrics
        VertxOptions options = new VertxOptions().setMetricsOptions(telemetronMetricsOptions);
        vertx = Vertx.vertx(options);
    }


    @Test
    public void testHttpClientMetricCreation() {
        VertxMetricsImpl victim = new VertxMetricsImpl(vertx, telemetronMetricsOptions);
        HttpClientMetrics createdMetrics = victim.createMetrics(mock(HttpClient.class), mock(HttpClientOptions.class));
        assertNotNull(createdMetrics);
    }
}