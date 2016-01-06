package com.telemetron.collector;


import com.telemetron.tag.Tags;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HttpClientMetricsImplTest {


    private HttpClientMetricsImpl victim;

    @Before
    public void setup() {
        victim = new HttpClientMetricsImpl();
    }

    @Test
    public void testUntaggedRequest() {

        SocketAddress socketMetric = mock(SocketAddress.class);
        SocketAddress localAddress = mock(SocketAddress.class);
        SocketAddress remoteAddress = mock(SocketAddress.class);
        HttpClientRequest request = mock(HttpClientRequest.class);
        MultiMap headers = mock(MultiMap.class);

        when(headers.get(eq(Tags.TRACK_HEADER.toString()))).thenReturn(null);
        when(request.headers()).thenReturn(headers);

        HttpClientRequestMetrics metrics = victim.requestBegin(socketMetric, localAddress, remoteAddress, request);

        assertNull(metrics);
    }

    @Test
    public void testRequestBegin() {

        SocketAddress socketMetric = mock(SocketAddress.class);
        SocketAddress localAddress = mock(SocketAddress.class);
        SocketAddress remoteAddress = mock(SocketAddress.class);
        HttpClientRequest request = mock(HttpClientRequest.class);
        MultiMap headers = mock(MultiMap.class);

        when(headers.get(eq(Tags.TRACK_HEADER.toString()))).thenReturn("tag");
        when(request.headers()).thenReturn(headers);

        HttpClientRequestMetrics metrics = victim.requestBegin(socketMetric, localAddress, remoteAddress, request);

        assertNotNull(metrics);
        assertEquals(remoteAddress, metrics.getAddress());
    }

    @Test
    public void testRequestEnd() throws InterruptedException {

        SocketAddress remoteAddress = mock(SocketAddress.class);
        HttpClientResponse response = mock(HttpClientResponse.class);
        HttpClientRequestMetrics metrics = new HttpClientRequestMetrics("tag", remoteAddress);
        when(remoteAddress.host()).thenReturn("host");
        victim.responseEnd(metrics, response);

        verify(remoteAddress, times(1)).host();
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertTrue(victim.isEnabled());
    }
}