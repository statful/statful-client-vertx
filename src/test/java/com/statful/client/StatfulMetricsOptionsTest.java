package com.statful.client;

import com.google.common.collect.Lists;
import com.statful.utils.Pair;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class StatfulMetricsOptionsTest {

    private StatfulMetricsOptions victim;

    @Before
    public void setup() {
        victim = new StatfulMetricsOptions();
    }

    @Test
    public void testDefaultHost() {
        // check default host
        assertEquals("api.statful.com", victim.getHost());
    }

    @Test
    public void testSetHost() {
        victim.setHost("sampleHost");
        assertEquals("sampleHost", victim.getHost());
    }

    @Test
    public void testDefaultSetPort() {
        // check default host
        assertEquals(443, victim.getPort());
    }

    @Test
    public void testSetPort() {
        victim.setPort(1111);
        // check default host
        assertEquals(1111, victim.getPort());
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testSetNullTransport() {
        victim.setTransport(null);
    }

    @Test
    public void testMandatorySetDefaultTransport() {
        assertEquals(Transport.HTTP, victim.getTransport());
    }

    @Test
    public void testSetTransport() {
        victim.setTransport(Transport.HTTP);
        assertEquals(Transport.HTTP, victim.getTransport());
    }

    @Test
    public void testDefaultSecure() {
        assertTrue(victim.isSecure());
    }

    @Test
    public void testSetSecure() {
        victim.setSecure(true);
        assertTrue(victim.isSecure());
    }

    @Test
    public void testDefaultTimeout() {
        assertEquals(2000, victim.getTimeout());
    }

    @Test
    public void testSetTimeout() {
        assertEquals(100, victim.setTimeout(100).getTimeout());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultToken() {
        victim.getToken();
    }

    @Test
    public void testSetToken() {
        assertEquals("token", victim.setToken("token").getToken());
    }

    @Test
    public void testDefaultApp() {
        assertFalse(victim.getApp().isPresent());
    }

    @Test
    public void testSetApp() {
        assertTrue(victim.setApp("app").getApp().filter(value -> value.equals("app")).isPresent());
    }

    @Test
    public void testDefaultDryRun() {
        assertFalse(victim.isDryrun());
    }

    @Test
    public void testSetDryRun() {
        assertTrue(victim.setDryrun(true).isDryrun());
    }

    @Test
    public void testDefaultTagList() {
        assertEquals(Collections.<Pair<String, String>>emptyList(), victim.getTags());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetTags() {
        List<Pair<String, String>> tags = Lists.newArrayList(new Pair<>("tag1", "tag1Value"), new Pair<>("tag2", "tag2Value"), new Pair<>("tag1", "tag1Value"));
        victim.setTags(tags);
        assertTrue(tags.containsAll(victim.getTags()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSetShallowCopyTags() {
        List<Pair<String, String>> tags = Lists.newArrayList(new Pair<>("tag1", "tag1Value"), new Pair<>("tag2", "tag2Value"), new Pair<>("tag1", "tag1Value"));
        victim.setTags(tags);
        Pair<String, String> pair = new Pair<>("shouldNotExist", "shouldNotExist");
        tags.add(pair);
        assertFalse(victim.getTags().contains(pair));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSampleRateInvalidLowerBound() {
        victim.setSampleRate(-10);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetSampleRateInvalidUpperBound() {
        victim.setSampleRate(101);
    }

    @Test
    public void testSetSampleRate() {
        assertEquals(100, victim.setSampleRate(100).getSampleRate());
    }

    @Test
    public void testDefaultNameSpace() {
        assertEquals("application", victim.getNamespace());
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testSetNullNamespace() {
        victim.setNamespace(null);
    }

    @Test
    public void testSetNamespace() {
        assertEquals("app", victim.setNamespace("app").getNamespace());
    }

    @Test
    public void testDefaultFlushSize() {
        assertEquals(10, victim.getFlushSize());
    }

    @Test
    public void testSetFlushSize() {
        assertEquals(15, victim.setFlushSize(15).getFlushSize());
    }

    @Test
    public void testSetEnablePoolMetrics() {
        assertTrue(victim.setEnablePoolMetrics(true).isEnablePoolMetrics());
    }

    @Test
    public void testSetEnableHttpClientMetrics() {
        assertTrue(victim.setEnableHttpClientMetrics(true).isEnableHttpClientMetrics());
    }

    @Test
    public void testSetEnableHttpServerMetrics() {
        assertFalse(victim.setEnableHttpServerMetrics(false).isEnableHttpServerMetrics());
    }

    @Test
    public void testDefaultBiggerThenZeroBufferSize() {
        assertTrue(victim.getMaxBufferSize() > 0);
    }

    @Test
    public void testSetMaxBufferSize() {
        int newSize = victim.getMaxBufferSize() + 1;
        victim.setMaxBufferSize(newSize);
        assertEquals(newSize, victim.getMaxBufferSize());
    }

    @Test
    public void testDefaultTimerAggregations() {
        List<Aggregation> expected = Lists.newArrayList(Aggregation.AVG, Aggregation.P90, Aggregation.COUNT);

        assertEquals(3, victim.getTimerAggregations().size());
        assertTrue(victim.getTimerAggregations().containsAll(expected));
    }

    @Test
    public void testSetTimerAggregations() {
        victim.setTimerAggregations(Lists.newArrayList(Aggregation.LAST));

        assertEquals(1, victim.getTimerAggregations().size());
        assertTrue(victim.getTimerAggregations().contains(Aggregation.LAST));
    }

    @Test
    public void testDefaultGaugeAggregations() {
        List<Aggregation> expected = Lists.newArrayList(Aggregation.LAST, Aggregation.MAX, Aggregation.AVG);

        assertEquals(3, victim.getGaugeAggregations().size());
        assertTrue(victim.getGaugeAggregations().containsAll(expected));
    }

    @Test
    public void testSetGaugeAggregations() {
        victim.setGaugeAggregations(Lists.newArrayList(Aggregation.LAST));

        assertEquals(1, victim.getGaugeAggregations().size());
        assertTrue(victim.getGaugeAggregations().contains(Aggregation.LAST));
    }

    @Test
    public void testDefaultCounterAggregations() {
        List<Aggregation> expected = Lists.newArrayList(Aggregation.COUNT, Aggregation.SUM);

        assertEquals(2, victim.getCounterAggregations().size());
        assertTrue(victim.getCounterAggregations().containsAll(expected));
    }

    @Test
    public void testSetCounterAggregations() {
        victim.setCounterAggregations(Lists.newArrayList(Aggregation.LAST));

        assertEquals(1, victim.getCounterAggregations().size());
        assertTrue(victim.getCounterAggregations().contains(Aggregation.LAST));
    }
    
    @Test
    public void testDefaultTimerAggregationFrequency() {
        assertEquals(AggregationFreq.FREQ_10, victim.getTimerFrequency());
    }
    
    @Test
    public void testSetTimerAggregationFrequency() {
        victim.setTimerFrequency(AggregationFreq.FREQ_120);
        
        assertEquals(AggregationFreq.FREQ_120, victim.getTimerFrequency());
    }

    @Test
    public void testDefaultGaugeAggregationFrequency() {
        assertEquals(AggregationFreq.FREQ_10, victim.getGaugeFrequency());
    }

    @Test
    public void testSetGaugeAggregationFrequency() {
        victim.setGaugeFrequency(AggregationFreq.FREQ_120);

        assertEquals(AggregationFreq.FREQ_120, victim.getGaugeFrequency());
    }
    
    @Test
    public void testDefaultCounterAggregationFrequency() {
        assertEquals(AggregationFreq.FREQ_10, victim.getCounterFrequency());
    }

    @Test
    public void testSetCounterAggregationFrequency() {
        victim.setCounterFrequency(AggregationFreq.FREQ_120);

        assertEquals(AggregationFreq.FREQ_120, victim.getCounterFrequency());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCopyCtor() {
        victim.setApp("app").setDryrun(true).setFlushSize(10).setHost("host").setNamespace("namespace")
                .setPort(9999).setSampleRate(10).setSecure(true)
                .setTimeout(1000).setToken("token").setTransport(Transport.UDP).setEnabled(true)
                .setTags(Lists.newArrayList(new Pair<>("tag", "value")));

        StatfulMetricsOptions copy = new StatfulMetricsOptions(victim);
        assertEquals(victim.getApp(), copy.getApp());
        assertEquals(victim.isDryrun(), copy.isDryrun());
        assertEquals(victim.getFlushSize(), copy.getFlushSize());
        assertEquals(victim.getNamespace(), copy.getNamespace());
        assertEquals(victim.getPort(), copy.getPort());
        assertEquals(victim.getSampleRate(), copy.getSampleRate());
        assertEquals(victim.isSecure(), copy.isSecure());
        assertTrue(victim.getTags().containsAll(copy.getTags()));
        assertEquals(victim.getTimeout(), copy.getTimeout());
        assertEquals(victim.getToken(), copy.getToken());
        assertEquals(victim.getTransport(), copy.getTransport());
        assertEquals(victim.isEnabled(), copy.isEnabled());
    }

    @Test
    public void testJsonObjectConstructor() {

        JsonObject configuration = new JsonObject()
                .put("host", "host")
                .put("port", 1111)
                .put("transport", Transport.HTTP.toString())
                .put("secure", false)
                .put("timeout", 100)
                .put("token", "token")
                .put("app", "tests")
                .put("dryrun", true)
                .put("tags", Lists.newArrayList(new JsonObject().put("tag", "tag1").put("value", "value1"),
                        new JsonObject().put("tag", "tag2").put("value", "value2")))
                .put("sampleRate", 100)
                .put("namespace", "ns")
                .put("flushSize", 1)
                .put("flushInterval", 10)
                .put("timerAggregations", Lists.newArrayList(Aggregation.AVG.toString(), Aggregation.COUNT.toString()))
                .put("timerFrequency", AggregationFreq.FREQ_10.toString())
                .put("collectors", new JsonObject().put("pool", true).put("httpClient", true).put("httpServer", false));

        victim = new StatfulMetricsOptions(configuration);
        assertEquals(victim.getHost(), "host");
        assertEquals(victim.getPort(), 1111);
        assertEquals(victim.getTransport(), Transport.HTTP);
        assertFalse(victim.isSecure());
        assertEquals(victim.getTimeout(), 100);
        assertEquals(victim.getToken(), "token");
        assertTrue(victim.getApp().filter(value -> value.equals("tests")).isPresent());
        assertTrue(victim.isDryrun());

        List<Pair<String, String>> tags = Lists.newArrayList();
        tags.add(new Pair<>("tag1", "value1"));
        tags.add(new Pair<>("tag2", "value2"));
        assertTrue(victim.getTags().containsAll(tags));

        assertEquals(victim.getSampleRate(), 100);
        assertEquals(victim.getNamespace(), "ns");
        assertEquals(victim.getFlushSize(), 1);
        assertEquals(victim.getFlushInterval(), 10);
        assertTrue(victim.getTimerAggregations().containsAll(Lists.newArrayList(Aggregation.AVG, Aggregation.COUNT)));
        assertEquals(victim.getTimerFrequency(), AggregationFreq.FREQ_10);
        assertTrue(victim.isEnablePoolMetrics());
        assertTrue(victim.isEnableHttpClientMetrics());
        assertFalse(victim.isEnableHttpServerMetrics());
    }
}