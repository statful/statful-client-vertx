package com.telemetron.client;

import io.vertx.core.Context;
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

    private TelemetronMetricsOptions telemetronOptions;

    private Vertx vertx;

    private VertxOptions vertxOptions;

    private Context context;

    @Before
    public void init() {

        this.vertx = mock(Vertx.class);
        this.vertxOptions = mock(VertxOptions.class);
        this.telemetronOptions = mock(TelemetronMetricsOptions.class);
        this.context = mock(Context.class);

        when(vertx.getOrCreateContext()).thenReturn(context);

        victim = new TelemetronMetricsFactoryImpl();
    }

    @Test
    public void testCreationWithNonTelemetronOptionsShouldBeDisabledByDefault() {

        when(vertxOptions.getMetricsOptions()).thenReturn(mock(MetricsOptions.class));

        assertFalse(victim.metrics(vertx, vertxOptions).isEnabled());
    }

    @Test
    public void testCreationWithTelemetronOptionsEnabled() {

        when(telemetronOptions.getTransport()).thenReturn(Transport.UDP);
        when(telemetronOptions.isEnabled()).thenReturn(true);

        when(vertxOptions.getMetricsOptions()).thenReturn(telemetronOptions);

        assertTrue(victim.metrics(vertx, vertxOptions).isEnabled());
    }

}