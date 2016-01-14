package com.telemetron.collector;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class HttpClientRequestMetricsTest {

    private SocketAddress mockSocketAddress;

    private HttpRequestMetrics victim;

    @Before
    public void init () {
        mockSocketAddress = mock(SocketAddress.class);
        victim = new HttpRequestMetrics("tag", mockSocketAddress, HttpMethod.GET);
    }

    @Test
    public void testTimeElapsed() throws InterruptedException {
        victim.start();
        Thread.sleep(1);
        assertTrue(victim.elapsed() > 0);
    }
}