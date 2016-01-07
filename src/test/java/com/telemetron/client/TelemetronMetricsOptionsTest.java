package com.telemetron.client;

import com.google.common.collect.Lists;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TelemetronMetricsOptionsTest {

    private TelemetronMetricsOptions victim;

    @Before
    public void setup() {
        victim = new TelemetronMetricsOptions();
    }

    @Test
    public void testDefaultHost() {
        // check default host
        assertEquals("127.0.0.1", victim.getHost());
    }

    @Test
    public void testSetHost() {
        victim.setHost("sampleHost");
        assertEquals("sampleHost", victim.getHost());
    }

    @Test
    public void testDefaultSetPort() {
        // check default host
        assertEquals(Integer.valueOf(2013), victim.getPort());
    }

    @Test
    public void testSetPort() {
        victim.setPort(1111);
        // check default host
        assertEquals(Integer.valueOf(1111), victim.getPort());
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testSetNullPrefix() {
        victim.setPrefix(null);
    }

    @Test(expected = NullPointerException.class)
    public void testMandatorySetPrefix() {
        victim.getPrefix();
    }

    @Test
    public void testSetPrefix() {
        victim.setPrefix("prefix");
        assertEquals("prefix", victim.getPrefix());
    }


    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testSetNullTransport() {
        victim.setTransport(null);
    }

    @Test
    public void testMandatorySetDefaultTransport() {
        assertEquals(Transport.UDP, victim.getTransport());
    }

    @Test
    public void testSetTransport() {
        victim.setTransport(Transport.HTTP);
        assertEquals(Transport.HTTP, victim.getTransport());
    }

    @Test
    public void testDefaultSecure() {
        assertTrue(victim.isSecure().booleanValue());
    }

    @Test
    public void testSetSecure() {
        victim.setSecure(true);
        assertTrue(victim.isSecure().booleanValue());
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
        assertEquals("app", victim.setApp("app").getApp().get());
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
        assertEquals(Collections.emptyList(), victim.getTags());
    }

    @Test
    public void testSetTags() {
        List<String> tags = Lists.newArrayList("tag1", "tag2", "tag3");
        victim.setTags(tags);
        assertTrue(tags.containsAll(victim.getTags()));
    }

    @Test
    public void testSetShallowCopyTags() {
        List<String> tags = Lists.newArrayList("tag1", "tag2", "tag3");
        victim.setTags(tags);
        tags.add("shouldNotExist");
        assertFalse(victim.getTags().contains("shouldNotExist"));
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
    public void testCopyCtor() {
        victim.setApp("app").setDryrun(true).setFlushSize(10).setHost("host").setNamespace("namespace")
                .setPort(9999).setPrefix("prefix").setSampleRate(10).setSecure(true).setTags(Lists.newArrayList("a", "b"))
                .setTimeout(1000).setToken("token").setTransport(Transport.UDP).setEnabled(true);

        TelemetronMetricsOptions copy = new TelemetronMetricsOptions(victim);
        assertEquals(victim.getApp(), copy.getApp());
        assertEquals(victim.isDryrun(), copy.isDryrun());
        assertEquals(victim.getFlushSize(), copy.getFlushSize());
        assertEquals(victim.getNamespace(), copy.getNamespace());
        assertEquals(victim.getPort(), copy.getPort());
        assertEquals(victim.getPrefix(), copy.getPrefix());
        assertEquals(victim.getSampleRate(), copy.getSampleRate());
        assertEquals(victim.isSecure(), copy.isSecure());
        assertTrue(victim.getTags().containsAll(copy.getTags()));
        assertEquals(victim.getTimeout(), copy.getTimeout());
        assertEquals(victim.getToken(), copy.getToken());
        assertEquals(victim.getTransport(), copy.getTransport());
        assertEquals(victim.isEnabled(), copy.isEnabled());
    }

    @Test(expected = RuntimeException.class)
    public void testNotImplementedJsonCtor() {
        new TelemetronMetricsOptions(new JsonObject());
    }

}