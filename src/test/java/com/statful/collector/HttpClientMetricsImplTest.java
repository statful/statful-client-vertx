package com.statful.collector;


import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.HttpClientDataPoint;
import com.statful.sender.Sender;
import com.statful.tag.Tags;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.SocketAddress;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HttpClientMetricsImplTest {

    private HttpClientMetricsImpl victim;
    private Sender sender;
    private StatfulMetricsOptions statfulMetricsOptions;

    @Before
    public void setup() {
        sender = mock(Sender.class);
        statfulMetricsOptions = mock(StatfulMetricsOptions.class);
        victim = new HttpClientMetricsImpl(sender, statfulMetricsOptions);
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

        HttpRequestMetrics metrics = victim.requestBegin(socketMetric, localAddress, remoteAddress, request);

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

        HttpRequestMetrics metrics = victim.requestBegin(socketMetric, localAddress, remoteAddress, request);

        assertNotNull(metrics);
        assertEquals(remoteAddress, metrics.getAddress());
    }

    @Test
    public void testRequestEnd() throws InterruptedException {

        SocketAddress remoteAddress = mock(SocketAddress.class);
        HttpClientResponse response = mock(HttpClientResponse.class);
        HttpRequestMetrics metrics = new HttpRequestMetrics("tag", remoteAddress, HttpMethod.GET);
        when(remoteAddress.host()).thenReturn("host");
        victim.responseEnd(metrics, response);

        ArgumentCaptor<HttpClientDataPoint> captor = ArgumentCaptor.forClass(HttpClientDataPoint.class);
        verify(sender,times(1)).addMetric(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertTrue(victim.isEnabled());
    }
}