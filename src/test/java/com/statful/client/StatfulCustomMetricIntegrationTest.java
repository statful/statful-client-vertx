package com.statful.client;

import com.google.common.collect.Lists;
import com.statful.utils.Pair;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@RunWith(VertxUnitRunner.class)
public class StatfulCustomMetricIntegrationTest extends IntegrationTestCase {

    private static final String HOST = "0.0.0.0";
    private static final int HTTP_SENDER_PORT = 1236;
    private HttpServer httpMetricsReceiver;
    private Vertx vertx;

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
    public void testCustomMetric(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithIntValue(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        setupHttpMetricsReceiverExpectationsForCustomMetricWithValue(context, async);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(12345)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithLongValue(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        setupHttpMetricsReceiverExpectationsForCustomMetricWithValue(context, async);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(12345L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithDoubleValue(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        setupHttpMetricsReceiverExpectationsForCustomMetricWithValue(context, async);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(12345D)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithFloatValue(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        setupHttpMetricsReceiverExpectationsForCustomMetricWithValue(context, async);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(12345F)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithStringValue(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        setupHttpMetricsReceiverExpectationsForCustomMetricWithValue(context, async);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue("12345")
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithNamespace(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();
        options.setNamespace("customNamespace");

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customNamespace"));
            context.assertTrue(metric.contains("customMetricName"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithGlobalAppTag(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();
        options.setApp("customApp");

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("app=customApp"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithCustomTagsAndGlobalTags(TestContext context) throws Exception {
        List<Pair<String, String>> globalTags = new ArrayList<>(1);
        globalTags.add(new Pair<>("globalTagName", "globalTagValue"));

        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();
        options.setTags(globalTags);

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("globalTagName=globalTagValue"));
            context.assertTrue(metric.contains("tagName=tagValue"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        Pair<String, String> tag = new Pair<>("tagName", "tagValue");
        List<Pair<String, String>> tags = Lists.newArrayList();
        tags.add(tag);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withTags(tags)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithCustomTags(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertFalse(metric.contains("globalTagName=globalTagValue"));
            context.assertTrue(metric.contains("tagName=tagValue"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        Pair<String, String> tag = new Pair<>("tagName", "tagValue");
        List<Pair<String, String>> tags = Lists.newArrayList();
        tags.add(tag);

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withTags(tags)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithGlobalTags(TestContext context) throws Exception {
        List<Pair<String, String>> globalTags = new ArrayList<>(1);
        globalTags.add(new Pair<>("globalTagName", "globalTagValue"));

        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();
        options.setTags(globalTags);

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("globalTagName=globalTagValue"));
            context.assertFalse(metric.contains("tagName=tagValue"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithGlobalDefaultTimerAggregations(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("avg,p90,count,10"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withMetricType(MetricType.TIMER)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithoutAnyAggregations(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();
        options.setTimerAggregations(Collections.emptyList());

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertFalse(metric.contains("avg,p90,count,10"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withMetricType(MetricType.TIMER)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithoutCustomAggregationsAndDefaultFrequency(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("max,10"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withMetricType(MetricType.TIMER)
                .withAggregations(Lists.newArrayList(Aggregation.MAX))
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithCustomAggregationsAndCustomFrequency(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("max,120"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withMetricType(MetricType.TIMER)
                .withAggregations(Lists.newArrayList(Aggregation.MAX))
                .withFrequency(AggregationFreq.FREQ_120)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithDefaultTimerAggregationsAndCustomFrequency(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("avg,p90,count,120"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withMetricType(MetricType.TIMER)
                .withFrequency(AggregationFreq.FREQ_120)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    @Test
    public void testCustomMetricWithCustomTimestamp(TestContext context) throws Exception {
        StatfulMetricsOptions options = getCommonStatfulMetricsOptions();

        setupVertxTestContext(options);

        Async async = context.async();

        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("123456789"));

            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });

        CustomMetric metric = new CustomMetric.Builder()
                .withMetricName("customMetricName")
                .withTimestamp(123456789L)
                .withValue(1L)
                .build();

        this.vertx.eventBus().send(CustomMetricsConsumer.ADDRESS, metric);
    }

    private StatfulMetricsOptions getCommonStatfulMetricsOptions() {
        StatfulMetricsOptions options = new StatfulMetricsOptions();
        options.setPort(HTTP_SENDER_PORT)
                .setHost(HOST)
                .setTransport(Transport.HTTP)
                .setFlushInterval(1000)
                .setFlushSize(20)
                .setSecure(false)
                .setToken("a token")
                .setEnabled(true)
                .setEnablePoolMetrics(false)
                .setEnableHttpClientMetrics(false)
                .setEnableHttpServerMetrics(false);
        return options;
    }

    private void setupVertxTestContext(StatfulMetricsOptions options) throws Exception {
        VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);

        this.vertx = Objects.requireNonNull(Vertx.vertx(vertxOptions));
        this.httpMetricsReceiver = this.vertx.createHttpServer();

        this.setUpHttpReceiver(vertx);
    }

    private void setupHttpMetricsReceiverExpectationsForCustomMetricWithValue(TestContext context, Async async) {
        this.httpMetricsReceiver.requestHandler(packet -> packet.bodyHandler(body -> {
            String metric = body.toString();
            context.assertTrue(metric.contains("customMetricName"));
            context.assertTrue(metric.contains("12345"));
            teardown(async, context, null);
        })).listen(HTTP_SENDER_PORT, HOST, event -> {
            if (event.failed()) {
                teardown(async, context, event.cause());
            }
        });
    }
}
