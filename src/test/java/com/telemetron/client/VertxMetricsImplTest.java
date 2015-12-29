package com.telemetron.client;

import com.telemetron.verticles.DummyVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void testVerticleDeployMetricCollection(TestContext context) {
        Async async = context.async();

        DummyVerticle dummyVerticle = new DummyVerticle();
        vertx.deployVerticle(dummyVerticle, deploy -> {
            context.assertTrue(deploy.succeeded());
            async.complete();
        });
    }


}