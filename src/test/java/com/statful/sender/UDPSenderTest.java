package com.statful.sender;

import com.google.common.collect.Lists;
import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.DataPoint;
import io.vertx.core.*;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.datagram.DatagramSocket;
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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class UDPSenderTest {

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 1239;
    private DatagramSocket receiver;

    private UDPSender victim;

    private Vertx vertx;


    /**
     * not using @junit @Before since not all tests want to the same configuration for vertx metrics.
     * Don't forget to call this in your method
     */
    public void setup(boolean isDryRun, Optional<Long> flushInterval, Optional<Integer> flushSize) {
        StatfulMetricsOptions options = new StatfulMetricsOptions();
        options.setPort(PORT).setHost(HOST).setDryrun(isDryRun).setEnablePoolMetrics(false).setMaxBufferSize(5000);

        flushInterval.ifPresent(options::setFlushInterval);
        flushSize.ifPresent(options::setFlushSize);

        vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(options));

        this.victim = new UDPSender(vertx, vertx.getOrCreateContext(), options);
        this.receiver = vertx.createDatagramSocket();
    }

    /**
     * Not using junit @after annotation since we want to wait for the servers to close
     *
     * @param async used to finish the test
     */
    private void teardown(Async async) {
        this.victim.close(victimClose -> this.receiver.close(receiverClose -> async.complete()));
    }

    @Test
    public void testNothingToSend(TestContext testContext) {

        this.setup(false, Optional.empty(), Optional.empty());

        Async async = testContext.async();

        Vertx vertx = mock(Vertx.class);
        when(vertx.setTimer(anyLong(), Matchers.any())).thenReturn(1L);

        Context context = mock(Context.class);
        Mockito.doNothing().when(context).runOnContext(Matchers.any());

        DatagramSocket datagramSocket = mock(DatagramSocket.class);
        when(vertx.createDatagramSocket()).thenReturn(datagramSocket);

        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.getFlushInterval()).thenReturn(10L);
        when(options.getFlushSize()).thenReturn(10);
        when(options.getMaxBufferSize()).thenReturn(5000);

        UDPSender sender = new UDPSender(vertx, context, options);
        sender.send(Collections.emptyList());

        verify(datagramSocket, times(0)).send(anyString(), anyInt(), anyString(), Matchers.any());

        this.teardown(async);
    }

    @Test
    public void testSend(TestContext context) throws Exception {
        this.setup(false, Optional.empty(), Optional.empty());
        Async async = context.async();

        final List<String> metricLines = Lists.newArrayList("line1", "line2");
        final List<DataPoint> dataPoints = metricLines.stream().map(DummyDataPoint::new).collect(Collectors.toList());

        // configure receiver and desired assertions
        this.receiver.listen(PORT, HOST, event -> {
            context.assertTrue(event.succeeded());
            receiver.handler(packet -> {
                context.assertNotNull(packet.data());
                // split the metric by the separator char
                final String packetData = packet.data().toString();
                final List<String> metrics = Arrays.asList(packetData.split("\n"));

                context.assertEquals(2, metrics.size());

                context.assertTrue(metrics.containsAll(metricLines));
                this.receiver.close(ignore -> this.teardown(async));
            });
            receiver.endHandler(end -> receiver.close());
            receiver.exceptionHandler(context::fail);

            victim.send(dataPoints);
        });
    }

    @Test
    public void testDryRunMetricsNotSent(TestContext context) {

        this.setup(true, Optional.of(1000L), Optional.of(1));

        final Async async = context.async();

        // configure receiver and desired assertions
        this.receiver.listen(PORT, HOST, event -> {
            context.assertTrue(event.succeeded());
            event.result().handler(packet -> context.fail("nothing should be sent since this is a dry run"));
        });

        // we will wait for 5 seconds and consider the test a success if nothing is received
        vertx.setTimer(5000, timer -> async.complete());

        final List<String> metricLines = Lists.newArrayList("line1", "line2");
        final List<DataPoint> dataPoints = metricLines.stream().map(DummyDataPoint::new).collect(Collectors.toList());

        dataPoints.forEach(victim::addMetric);
    }

    @Test
    public void testSendNoListenerSuccess(TestContext context) throws Exception {

        this.setup(true, Optional.empty(), Optional.empty());

        Async async = context.async();

        final List<String> metricLines = Lists.newArrayList("line1", "line2");

        List<DataPoint> dataPoints = metricLines.stream().map(DummyDataPoint::new).collect(Collectors.toList());
        vertx.setTimer(1000, timer -> victim.send(dataPoints, result -> {
            context.assertTrue(result.succeeded());
            teardown(async);
        }));
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