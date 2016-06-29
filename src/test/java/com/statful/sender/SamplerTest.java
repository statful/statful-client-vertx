package com.statful.sender;

import com.statful.client.StatfulMetricsOptions;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class SamplerTest {

    @Test
    public void testShouldNotExecuteRandomForMaxSampleRate() {
        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.getSampleRate()).thenReturn(StatfulMetricsOptions.MAX_SAMPLE_RATE);

        Random random = mock(Random.class);

        Sampler victim = new Sampler(options, random);
        assertTrue(victim.shouldInsert());
        verifyZeroInteractions(random);
    }

    @Test
    public void testShouldInsert() throws Exception {

        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.getSampleRate()).thenReturn(StatfulMetricsOptions.MAX_SAMPLE_RATE - 1);

        Random random = mock(Random.class);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        when(random.nextInt(captor.capture())).thenReturn(StatfulMetricsOptions.MAX_SAMPLE_RATE - 2);

        Sampler victim = new Sampler(options, random);
        assertTrue(victim.shouldInsert());
        assertEquals(captor.getValue(), StatfulMetricsOptions.MAX_SAMPLE_RATE);
    }

    @Test
    public void testShouldNotInsert() {
        StatfulMetricsOptions options = mock(StatfulMetricsOptions.class);
        when(options.getSampleRate()).thenReturn(StatfulMetricsOptions.MAX_SAMPLE_RATE - 10);

        Random random = mock(Random.class);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        when(random.nextInt(captor.capture())).thenReturn(StatfulMetricsOptions.MAX_SAMPLE_RATE - 5);

        Sampler victim = new Sampler(options, random);
        assertFalse(victim.shouldInsert());
        assertEquals(captor.getValue(), StatfulMetricsOptions.MAX_SAMPLE_RATE);
    }
}