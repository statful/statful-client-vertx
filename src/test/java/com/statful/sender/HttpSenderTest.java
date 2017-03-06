package com.statful.sender;

import com.google.common.collect.Lists;
import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.DataPoint;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class HttpSenderTest {

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 1239;
    private HttpServer server;
    private HttpSender victim;
    private Vertx vertx;

    /**
     * not using @junit @Before since not all tests want to the same configuration for vertx metrics.
     * Don't forget to call this in your method
     */
    public void setup(boolean isDryRun, Long flushInterval, Integer flushSize) {
        StatfulMetricsOptions options = new StatfulMetricsOptions()
                .setPort(PORT)
                .setHost(HOST)
                .setDryrun(isDryRun)
                .setEnablePoolMetrics(false)
                .setMaxBufferSize(5000);

        Optional.ofNullable(flushInterval).ifPresent(options::setFlushInterval);
        Optional.ofNullable(flushSize).ifPresent(options::setFlushSize);

        this.vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(options));

        this.victim = new HttpSender(vertx, vertx.getOrCreateContext(), options);
        this.server = vertx.createHttpServer();
    }



    /**
     * Not using junit @after annotation since we want to wait for the servers to close
     *
     * @param async used to finish the test
     */
    private void teardown(Async async) {
        this.victim.close(victimClose -> this.server.close(serverClose -> async.complete()));
    }


    @Test
    public void testNothingToSend(TestContext testContext) {
        this.setup(false, null, null);

        Async async = testContext.async();

        Vertx vertx = mock(Vertx.class);
        when(vertx.setTimer(anyLong(), Matchers.any())).thenReturn(1L);

        Context context = mock(Context.class);
        Mockito.doNothing().when(context).runOnContext(Matchers.any());

        HttpClient client = mock(HttpClient.class);
        when(vertx.createHttpClient(any(HttpClientOptions.class))).thenReturn(client);

        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.getFlushInterval()).thenReturn(10L);
        when(options.getFlushSize()).thenReturn(10);
        when(options.getMaxBufferSize()).thenReturn(5000);

        HttpSender sender = new HttpSender(vertx, context, options);
        sender.send(Collections.emptyList());

        verify(client, times(0)).request(any(HttpMethod.class), anyInt(), anyString(), Matchers.any());

        this.teardown(async);
    }

    @Test
    public void testSend(TestContext context) throws Exception {
        this.setup(false, null, null);
        Async async = context.async();

        final List<String> metricLines = Lists.newArrayList("line1", "line2");
        final List<DataPoint> dataPoints = metricLines.stream().map(HttpSenderTest.DummyDataPoint::new).collect(Collectors.toList());


        server.requestHandler(request -> {
            context.assertEquals("http://0.0.0.0:1239/tel/v2.0/metrics", request.absoluteURI());

            request.bodyHandler(body -> {
                context.assertNotNull(body);
                final String requestBody = body.toString();
                final List<String> metrics = Arrays.asList(requestBody.split("\n"));

                context.assertEquals(2, metrics.size());
                context.assertTrue(metrics.containsAll(metricLines));
                this.teardown(async);
            });
        });

        // configure receiver and desired assertions
        this.server.listen(PORT, HOST, event -> {
            context.assertTrue(event.succeeded());

            victim.send(dataPoints);
        });
    }

    @Test
    public void testSendFailure(TestContext context) throws Exception {
        this.setup(false, null, null);
        Async async = context.async();

        final List<String> metricLines = Lists.newArrayList("line1", "line2");
        final List<DataPoint> dataPoints = metricLines.stream().map(HttpSenderTest.DummyDataPoint::new).collect(Collectors.toList());

        server.requestHandler(request -> request.response().setStatusCode(404).end());

        // configure receiver and desired assertions
        this.server.listen(PORT, HOST, event -> {
            context.assertTrue(event.succeeded());
            victim.send(dataPoints, context.asyncAssertFailure(result -> teardown(async)));
        });
    }

    @Test
    public void testDryRunMetricsNotSent(TestContext context) {

        this.setup(true, 1000L, 1);

        final Async async = context.async();

        server.requestHandler(request -> context.fail("nothing should be sent since this is a dry run"));

        // configure receiver and desired assertions
        this.server.listen(PORT, HOST, event -> context.assertTrue(event.succeeded()));

        // we will wait for 5 seconds and consider the test a success if nothing is received
        vertx.setTimer(5000, timer -> async.complete());

        final List<String> metricLines = Lists.newArrayList("line1", "line2");
        final List<DataPoint> dataPoints = metricLines.stream().map(HttpSenderTest.DummyDataPoint::new).collect(Collectors.toList());

        dataPoints.forEach(victim::addMetric);
    }

    private static final class DummyDataPoint implements DataPoint {

        private final String line;

        DummyDataPoint(String line) {
            this.line = line;
        }

        @Override
        public String toMetricLine() {
            return this.line;
        }
    }
}
