package com.telemetron.client;

/**
 * Possible transport methods for sending metrics
 */
public enum Transport {
    /**
     * Value for UDP based transports
     */
    UDP,
    /**
     * Value for TCP based transports
     */
    TCP,
    /**
     * Value for HTTP based transports
     */
    HTTP
}
