package com.telemetron.sender;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.client.Transport;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SenderFactoryTest {

    private SenderFactory victim;

    @Before
    public void setup() {
        victim = new SenderFactory();
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNullVertx() {
        SenderFactory factory = new SenderFactory();
        factory.create(null, null);
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNullOptions() {
        Vertx vertx = mock(Vertx.class);
        SenderFactory factory = new SenderFactory();
        factory.create(vertx, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedTransport() {
        Vertx vertx = mock(Vertx.class);
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setTransport(Transport.HTTP);

        victim.create(vertx,options);
    }

    @Test
    public void testSenderCreationTransport() {
        Vertx vertx = mock(Vertx.class);
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setTransport(Transport.UDP);

        assertTrue(victim.create(vertx,options) instanceof UDPSender);
    }
}