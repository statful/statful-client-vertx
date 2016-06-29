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
public class StatfulMetricsFactoryImplTest {

    private StatfulMetricsFactoryImpl victim;

    private StatfulMetricsOptions statfulMetricsOptions;

    private Vertx vertx;

    private VertxOptions vertxOptions;

    private Context context;

    @Before
    public void init() {

        this.vertx = mock(Vertx.class);
        this.vertxOptions = mock(VertxOptions.class);
        this.statfulMetricsOptions = mock(StatfulMetricsOptions.class);
        this.context = mock(Context.class);

        when(vertx.getOrCreateContext()).thenReturn(context);

        victim = new StatfulMetricsFactoryImpl();
    }

    @Test
    public void testCreationWithNonStatfulOptionsShouldBeDisabledByDefault() {

        when(vertxOptions.getMetricsOptions()).thenReturn(mock(MetricsOptions.class));

        assertFalse(victim.metrics(vertx, vertxOptions).isEnabled());
    }

    @Test
    public void testCreationWithStatfulOptionsEnabled() {

        when(statfulMetricsOptions.getTransport()).thenReturn(Transport.UDP);
        when(statfulMetricsOptions.isEnabled()).thenReturn(true);

        when(vertxOptions.getMetricsOptions()).thenReturn(statfulMetricsOptions);

        assertTrue(victim.metrics(vertx, vertxOptions).isEnabled());
    }

    @Test
    public void testCreationFromFile(TestContext context) {

        Async async = context.async();
        StatfulMetricsOptions options = new StatfulMetricsOptions();
        options.setConfigPath("config/statful.json")
                // setting enabled to false, to check that the configuration available on file is used
                .setEnabled(false);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        Vertx vertx = Vertx.vertx(vertxOptions);

        StatfulMetricsFactoryImpl victim = new StatfulMetricsFactoryImpl();

        assertTrue(victim.metrics(vertx, vertxOptions).isEnabled());

        vertx.close(close -> async.complete());
    }

    @Test
    public void testCreationFromNonExistentFile(TestContext context) {

        Async async = context.async();
        StatfulMetricsOptions options = new StatfulMetricsOptions();
        options.setConfigPath("config/statful-not-existent.json")
                // setting enabled to false, to check that the configuration available on file is used
                .setEnabled(false);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        Vertx vertx = Vertx.vertx(vertxOptions);

        StatfulMetricsFactoryImpl victim = new StatfulMetricsFactoryImpl();

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
        StatfulMetricsOptions options = new StatfulMetricsOptions();
        options.setConfigPath("config/statful-wrong.json")
                // setting enabled to false, to check that the configuration available on file is used
                .setEnabled(false);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        Vertx vertx = Vertx.vertx(vertxOptions);

        StatfulMetricsFactoryImpl victim = new StatfulMetricsFactoryImpl();

        try {
            victim.metrics(vertx, vertxOptions);
            context.assertTrue(false, "should never run, an exception should've been thrown");
        } catch (RuntimeException e) {
            vertx.close(close -> async.complete());
        }
    }

}