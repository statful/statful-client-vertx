package com.telemetron.sender;

import com.google.common.collect.Lists;
import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.metric.DataPoint;
import com.telemetron.sender.UDPSender;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(VertxUnitRunner.class)
public class UDPSenderTest {

    private DatagramSocket receiver;

    private UDPSender victim;

    @Before
    public void setUp() throws Exception {
        Vertx vertx = Vertx.vertx();

        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setPort(1234).setHost("0.0.0.0");

        this.victim = new UDPSender(vertx, options);
        this.receiver = vertx.createDatagramSocket();
    }

    @Test
    public void testSend(TestContext context) throws Exception {
        Async async = context.async();

        final List<String> metricLines = Lists.newArrayList("line1", "line2");
        final List<DataPoint> dataPoints = metricLines.stream().map(DummyDataPoint::new).collect(Collectors.toList());

        // configure receiver and desired assertions
        this.receiver.listen(1234, "0.0.0.0", event -> {
            context.assertTrue(event.succeeded());
            receiver.handler(packet -> {
                context.assertNotNull(packet.data());
                // split the metric by the separator char
                final String packetData = packet.data().toString();
                final List<String> metrics = Arrays.asList(packetData.split("\n"));

                context.assertEquals(2, metrics.size());

                context.assertTrue(metrics.containsAll(metricLines));

                async.complete();
            });
            receiver.endHandler(end -> receiver.close());
            receiver.exceptionHandler(context::fail);
        });


        victim.send(dataPoints);
    }

    @Test
    public void testSendNoListenerSuccess(TestContext context) throws Exception {

        final List<String> metricLines = Lists.newArrayList("line1", "line2");

        List<DataPoint> dataPoints = metricLines.stream().map(DummyDataPoint::new).collect(Collectors.toList());
        victim.send(dataPoints, result -> {
            context.assertTrue(result.succeeded());
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