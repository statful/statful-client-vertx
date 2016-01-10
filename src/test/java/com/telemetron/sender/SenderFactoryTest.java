package com.telemetron.sender;

import com.telemetron.client.TelemetronMetricsOptions;
import com.telemetron.client.Transport;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SenderFactoryTest {

    private SenderFactory victim;

    private Context context;
    private Vertx vertx;

    @Before
    public void setup() {
        context = mock(Context.class);
        vertx = mock(Vertx.class);

        victim = new SenderFactory();
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNullVertx() {
        victim.create(null, null, null);
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNullContext() {
        victim.create(vertx, null, null);
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void testNullOptions() {
        victim.create(vertx, context, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedTransport() {
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setTransport(Transport.HTTP);

        victim.create(vertx, context, options);
    }

    @Test
    public void testSenderCreationTransport() {
        TelemetronMetricsOptions options = new TelemetronMetricsOptions();
        options.setTransport(Transport.UDP);

        assertTrue(victim.create(vertx, context, options) instanceof UDPSender);
    }
}