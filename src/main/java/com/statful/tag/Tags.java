package com.statful.tag;

/**
 * Contains standard markers to be used to identify requests that we want to track
 */
public enum Tags {

    /**
     * Used to identify requests that you want to track
     */
    TRACK_HEADER("X-Statful");

    /**
     * Value that will be used has header
     */
    private final String value;

    Tags(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
