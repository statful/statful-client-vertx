package com.telemetron.sender;

import com.google.common.collect.Lists;
import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.DataPoint;
import io.vertx.core.*;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class UDPSenderTest {

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 1239;
    private DatagramSocket receiver;

    private UDPSender victim;

    @Before
    public void setUp() throws Exception {
        Vertx vertx = Vertx.vertx();

        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setPort(PORT).setHost(HOST);

        this.victim = new UDPSender(vertx, vertx.getOrCreateContext(), options);
        this.receiver = vertx.createDatagramSocket();
    }

    /**
     * Not using junit @after annotation since we want to wait for the servers to close
     *
     * @param async used to finish the test
     */
    private void teardown(Async async) {
        this.victim.close(event -> async.complete());
    }

    @Test
    public void testNothingToSend(TestContext testContext) {

        Async async = testContext.async();

        Vertx vertx = mock(Vertx.class);
        when(vertx.setTimer(anyLong(), Matchers.<Handler<Long>>any())).thenReturn(1L);

        Context context = mock(Context.class);
        Mockito.doNothing().when(context).runOnContext(Matchers.<Handler<Void>>any());

        DatagramSocket datagramSocket = mock(DatagramSocket.class);
        when(vertx.createDatagramSocket()).thenReturn(datagramSocket);

        TelemetronMetricsOptions options = mock(TelemetronMetricsOptions.class);
        when(options.getFlushInterval()).thenReturn(10L);
        when(options.getFlushSize()).thenReturn(10);

        UDPSender sender = new UDPSender(vertx, context, options);
        sender.send(Collections.emptyList());

        verify(datagramSocket, times(0)).send(anyString(), anyInt(),anyString(), Matchers.<Handler<AsyncResult<DatagramSocket>>>any());

        this.teardown(async);
    }

    @Test
    public void testSend(TestContext context) throws Exception {
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
        });

        victim.send(dataPoints);
    }

    @Test
    public void testSendNoListenerSuccess(TestContext context) throws Exception {

        Async async = context.async();

        final List<String> metricLines = Lists.newArrayList("line1", "line2");

        List<DataPoint> dataPoints = metricLines.stream().map(DummyDataPoint::new).collect(Collectors.toList());
        victim.send(dataPoints, result -> {
            context.assertTrue(result.succeeded());
            teardown(async);
        });
    }

    private static final class DummyDataPoint implements DataPoint {

        private final String line;

        public DummyDataPoint(String line) {
            this.line = line;
        }

        @Override
        public String toMetricLine() {
            return this.line;
        }
    }
}