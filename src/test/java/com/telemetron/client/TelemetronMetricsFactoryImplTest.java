package com.telemetron.client;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.MetricsOptions;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelemetronMetricsFactoryImplTest {

    private TelemetronMetricsFactoryImpl victim;

    @Before
    public void init() {
        victim = new TelemetronMetricsFactoryImpl();
    }

    @Test
    public void testCreationWithNonTelemetronOptionsShouldBeDisabledByDefault() {

        Vertx vertx = mock(Vertx.class);
        VertxOptions vertxOptions = mock(VertxOptions.class);

        when(vertxOptions.getMetricsOptions()).thenReturn(mock(MetricsOptions.class));

        assertFalse(victim.metrics(vertx, vertxOptions).isEnabled());
    }

    @Test
    public void testCreationWithTelemetronOptionsEnabled() {
        Vertx vertx = mock(Vertx.class);
        VertxOptions vertxOptions = mock(VertxOptions.class);
        TelemetronMetricsOptions telemetronOptions = mock(TelemetronMetricsOptions.class);

        when(telemetronOptions.isEnabled()).thenReturn(true);
        when(vertxOptions.getMetricsOptions()).thenReturn(telemetronOptions);

        assertTrue(victim.metrics(vertx, vertxOptions).isEnabled());
    }

}