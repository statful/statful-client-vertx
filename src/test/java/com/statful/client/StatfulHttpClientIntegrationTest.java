package com.statful.client;

import com.google.common.collect.Lists;
import com.statful.utils.Pair;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RunWith(VertxUnitRunner.class)
public class StatfulHttpClientIntegrationTest extends IntegrationTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatfulHttpClientIntegrationTest.class);

    private static final String HOST = "0.0.0.0";
    private static final int HTTP_SENDER_PORT = 1236;
    private HttpServer httpMetricsReceiver;
    private Vertx vertx;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {

        List<Pair<String, String>> matchReplace = Lists.newArrayList();
        matchReplace.add(new Pair<>("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "_uuid_"));

        StatfulMetricsOptions options = new StatfulMetricsOptions();
        options.setPort(HTTP_SENDER_PORT)
                .setHost(HOST)
                .setTransport(Transport.HTTP)
                .setFlushInterval(1000)
                .setFlushSize(20)
                .setPrefix("testing")
                .setEnabled(true)
                .setEnablePoolMetrics(false)
                .setTags(Lists.newArrayList(new Pair<>("global", "value"), new Pair<>("global1", "value1")))
                .setHttpServerMatchAndReplacePatterns(matchReplace)
                .setHttpServerIgnorePaths(Lists.newArrayList(".*ignore.*"));

        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);

        this.vertx = Objects.requireNonNull(Vertx.vertx(vertxOptions));
        this.httpMetricsReceiver = this.vertx.createHttpServer();

        this.setUpHttpReceiver(vertx);
    }

    /**
     * Not using junit @after annotation since we want to wait for the servers to close
     *
     * @param async used to finish the test
     */
    private void teardown(Async async, TestContext context, Throwable throwable) {
        this.teardownHttpReceiver(aVoid -> httpMetricsReceiver.close(anVoid -> {
            if (nonNull(throwable)) {
                context.fail(throwable);
            } else {
                async.complete();
            }
        }));
    }

    @Test
    public void testHttpTimerClientMetrics(TestContext context) {
        testTimerMetric(this.vertx, context, "type=client", Optional.empty());
    }

    @Test
    public void testHttpServerTimerMetrics(TestContext context) {
        Vertx metricsDisabled = Vertx.vertx();
        testTimerMetric(metricsDisabled, context, "type=server", Optional.empty());
    }

    @Test
    public void testHttpServerTimerMetricsWithIgnoreEntry(TestContext context) {
        Vertx metricsDisabled = Vertx.vertx();
        testTimerMetric(metricsDisabled, context, "type=server", Optional.of("/should/ignore/"));
    }

    @Test
    public void testCustomMetric(TestContext context) {
        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> {
            packet.bodyHandler(body -> {
                String metric = body.toString();
                context.assertTrue(metric.contains("customMetricName"));
                context.assertTrue(metric.contains("value1"));
                context.assertTrue(metric.contains("tagName=tagValue"));
                teardown(async, context, null);
            });
        }).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        Pair<String, String> tag = new Pair<>("tagName", "tagValue");
        List<Pair<String, String>> tags = Lists.newArrayList();
        tags.add(tag);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withAggregations(Lists.newArrayList(Aggregation.AVG))
                .withFrequency(AggregationFreq.FREQ_10)
                .withMetricType(MetricType.TIMER)
                .withTags(tags)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    private void testTimerMetric(Vertx vertx, TestContext context, String tagMatcher, Optional<String> toIgnore) {
        Async async = context.async();

        final List<String> requests = Lists.newArrayList("X-1-X", "X-2-X", "X-3-X", "X-4-X", "X-5-X");
        final List<String> requestsWithIgnore = Lists.newArrayList(requests);

        toIgnore.ifPresent(requestsWithIgnore::add);

        this.httpMetricsReceiver.requestHandler(packet -> {
            // log metric value

            packet.bodyHandler(body -> {
                String metric = body.toString();

                LOGGER.info(metric);

                // if there is something that should've been ignored, confirm that a metric is not reported
                toIgnore.ifPresent(entry -> context.assertFalse(metric.contains(entry)));

                // check if the desired tag exists and if the defined global tags are being set in all metrics
                if (metric.contains(tagMatcher) && metric.contains("global=value") && metric.contains("global1=value1")) {
                    List<String> toRemove = requests.stream().filter(metric::contains).collect(Collectors.toList());
                    requests.removeAll(toRemove);

                    if (requests.isEmpty()) {
                        teardown(async, context, null);
                    }
                }
            });
        }).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        configureHttpReceiver(vertx, context, requestsWithIgnore);
    }
}
