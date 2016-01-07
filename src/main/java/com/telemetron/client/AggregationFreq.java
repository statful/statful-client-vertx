package com.telemetron.client;


/**
 * Valid aggregation frequencies
 */
public enum AggregationFreq {

    /**
     * 10 seconds aggregation
     */
    FREQ_10("10"),

    /**
     * 30 seconds aggregation
     */
    FREQ_30("30"),

    /**
     * 60 seconds aggregation
     */
    FREQ_60("60"),

    /**
     * 120 seconds aggregation
     */
    FREQ_120("120"),

    /**
     * 180 seconds aggregation
     */
    FREQ_180("180"),

    /**
     * 300 seconds aggregation
     */
    FREQ_300("300");

    /**
     * Number of seconds desired for the aggregation
     */
    private String value;


    /**
     * private constructor to force that all entries have a frequency string representation
     *
     * @param value String representation of the frequency
     */
    AggregationFreq(final String value) {
        this.value = value;
    }

    /**
     * Gets the string value of the frequency, to be used to build metric lines
     *
     * @return String with the frequency representation
     */
    public String getValue() {
        return this.value;
    }
}
