package com.statful.sender;

import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.DataPoint;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MetricsHolderTest {

    @Test
    public void testShouldNotAddMetric() {

        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.isDryrun()).thenReturn(false);
        when(options.getMaxBufferSize()).thenReturn(5000);

        Sampling sampling = mock(Sampling.class);
        when(sampling.shouldInsert()).thenReturn(false);

        DummyMetricsHolder dummy = new DummyMetricsHolder(options, sampling);
        assertFalse(dummy.addMetric(mock(DataPoint.class)));
    }

    @Test
    public void testShouldAddMetric() {

        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.isDryrun()).thenReturn(false);
        when(options.getMaxBufferSize()).thenReturn(5000);

        Sampling sampling = mock(Sampling.class);
        when(sampling.shouldInsert()).thenReturn(true);

        DummyMetricsHolder dummy = new DummyMetricsHolder(options, sampling);
        assertTrue(dummy.addMetric(mock(DataPoint.class)));
    }

    private static final class DummyMetricsHolder extends MetricsHolder {


        DummyMetricsHolder(StatfulMetricsOptions options, Sampling sampler) {
            super(options, sampler);
        }

        @Override
        public void send(@Nonnull List<DataPoint> metrics, @Nonnull Handler<AsyncResult<Void>> sentHandler) {

        }

        @Override
        public void send(@Nonnull List<DataPoint> metrics) {

        }

        @Override
        public void close(Handler<AsyncResult<Void>> handler) {

        }
    }
}