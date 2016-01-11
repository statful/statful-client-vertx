package com.telemetron.client;

import com.google.common.collect.Lists;
import com.telemetron.tag.Tags;
import io.netty.util.internal.ThreadLocalRandom;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(VertxUnitRunner.class)
public class TelemetronClientIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetronClientIntegrationTest.class);

    private static final String HOST = "0.0.0.0";
    private static final int UDP_PORT = 1234;
    private static final int HTTP_PORT = 1235;

    private DatagramSocket metricsReceiver;
    private HttpServer httpReceiver;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {

        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setPort(UDP_PORT)
                .setHost(HOST)
                .setTransport(Transport.UDP)
                .setFlushInterval(1000)
                .setFlushSize(20)
                .setPrefix("testing")
                .setEnabled(true);

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
        this.vertx = Vertx.vertx(vertxOptions);
        this.metricsReceiver = vertx.createDatagramSocket();
        this.httpReceiver = vertx.createHttpServer();

        LOGGER.error("TelemetronClientIntegrationTest");
    }

    @Test
    public void testHttpTimerClientMetrics(TestContext context) throws Exception {

        Async asnyc = context.async();

        final List<String> requests = Lists.newArrayList("X-1-X", "X-2-X", "X-3-X", "X-4-X", "X-5-X");

        this.metricsReceiver.listen(UDP_PORT, HOST, event -> {
            if (event.failed()) {
                context.fail("a metric failed to be received");
            }

            this.metricsReceiver.handler(packet -> {
                // log metric value
                String metric = packet.data().toString();
                LOGGER.info(metric);

                List<String> toRemove = requests.stream().filter(metric::contains).collect(Collectors.toList());
                requests.removeAll(toRemove);

                if (requests.isEmpty()) {
                    asnyc.complete();
                }
            });
        });

        this.setupHttpServer();

        this.makeHttpRequests(context, requests);
    }

    private void makeHttpRequests(TestContext context, List<String> requests) {
        requests.forEach(requestValue -> {
            // confirm that all requests have a 200 status code
            HttpClientRequest request1 = vertx.createHttpClient().get(HTTP_PORT, HOST, "/" + requestValue, event -> context.assertTrue(event.statusCode() == 200));
            request1.headers().add(Tags.TRACK_HEADER.toString(), requestValue);
            request1.end();
        });
    }

    private void setupHttpServer() {
        this.httpReceiver.requestHandler(request -> {
            // create a delay to simulate a lengthy api call
            long delay = ThreadLocalRandom.current().nextInt(200, 1000 + 1);
            vertx.setTimer(delay, event -> request.response().end("hey!"));
        }).listen(HTTP_PORT, HOST);
    }
}
