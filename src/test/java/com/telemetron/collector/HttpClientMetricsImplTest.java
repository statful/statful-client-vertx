package com.telemetron.collector;


import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HttpClientMetricsImplTest {


    private HttpClientMetricsImpl victim;

    @Before
    public void setup() {
        victim = new HttpClientMetricsImpl();
    }

    @Test
    public void testRequestBegin() {

        SocketAddress socketMetric = mock(SocketAddress.class);
        SocketAddress localAddress = mock(SocketAddress.class);
        SocketAddress remoteAddress = mock(SocketAddress.class);
        HttpClientRequest request = mock(HttpClientRequest.class);

        HttpClientRequestMetrics metrics = victim.requestBegin(socketMetric, localAddress, remoteAddress, request);

        assertEquals(remoteAddress, metrics.getAddress());
    }

    @Test
    public void testRequestEnd() throws InterruptedException {

        SocketAddress remoteAddress = mock(SocketAddress.class);
        HttpClientResponse response = mock(HttpClientResponse.class);
        HttpClientRequestMetrics metrics = new HttpClientRequestMetrics(remoteAddress);
        when(remoteAddress.host()).thenReturn("host");
        victim.responseEnd(metrics, response);

        verify(remoteAddress, times(1)).host();
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertTrue(victim.isEnabled());
    }
}