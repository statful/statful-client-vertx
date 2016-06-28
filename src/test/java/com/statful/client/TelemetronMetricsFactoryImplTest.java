package com.statful.client;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(VertxUnitRunner.class)
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

    @Test
    public void testCreationFromFile(TestContext context) {

        Async async = context.async();
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setConfigPath("config/telemetron.json")
                // setting enabled to false, to check that the configuration available on file is used
                .setEnabled(false);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        Vertx vertx = Vertx.vertx(vertxOptions);

        TelemetronMetricsFactoryImpl victim = new TelemetronMetricsFactoryImpl();

        assertTrue(victim.metrics(vertx, vertxOptions).isEnabled());

        vertx.close(close -> async.complete());
    }

    @Test
    public void testCreationFromNonExistentFile(TestContext context) {

        Async async = context.async();
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setConfigPath("config/telemetron-not-existent.json")
                // setting enabled to false, to check that the configuration available on file is used
                .setEnabled(false);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        Vertx vertx = Vertx.vertx(vertxOptions);

        TelemetronMetricsFactoryImpl victim = new TelemetronMetricsFactoryImpl();

        try {
            victim.metrics(vertx, vertxOptions);
            context.assertTrue(false, "should never run, an exception should've been thrown");
        } catch (RuntimeException e) {
            vertx.close(close -> async.complete());
        }
    }

    @Test
    public void testCreationFromBadFile(TestContext context) {

        Async async = context.async();
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setConfigPath("config/telemetron-wrong.json")
                // setting enabled to false, to check that the configuration available on file is used
                .setEnabled(false);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        Vertx vertx = Vertx.vertx(vertxOptions);

        TelemetronMetricsFactoryImpl victim = new TelemetronMetricsFactoryImpl();

        try {
            victim.metrics(vertx, vertxOptions);
            context.assertTrue(false, "should never run, an exception should've been thrown");
        } catch (RuntimeException e) {
            vertx.close(close -> async.complete());
        }
    }

}