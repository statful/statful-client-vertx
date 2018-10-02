package com.statful.client;

import com.google.common.collect.Lists;
import com.statful.utils.Pair;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Vert.x Statful metrics configuration
 */
public class StatfulMetricsOptions extends MetricsOptions {

    /**
     * Default Statful host constant
     */
    private static final String DEFAULT_HOST = "api.statful.com";

    /**
     * Default port Statful port
     */
    private static final int DEFAULT_PORT = 443;

    /**
     * Default value for a secure connection (https)
     */
    private static final Boolean DEFAULT_SECURE = true;

    /**
     * Default timeout value to be used by the client to connect to Statful
     */
    private static final int DEFAULT_TIMEOUT = 2000;

    /**
     * Default value for dry run configuration
     */
    private static final Boolean DEFAULT_DRY_RUN = false;

    /**
     * Maximum value allowed for the sample rate
     */
    public static final Integer MAX_SAMPLE_RATE = 100;

    /**
     * Minimum value allowed for the sample rate
     */
    private static final Integer MIN_SAMPLE_RATE = 1;

    /**
     * Default sample rate to applied to all metrics
     */
    private static final Integer DEFAULT_SAMPLE_RATE = MAX_SAMPLE_RATE;

    /**
     * Default namespace to be applied in all metrics
     */
    private static final String DEFAULT_NAMESPACE = "application";

    /**
     * Default size of elements that the buffer can old before flushing
     */
    private static final int DEFAULT_FLUSH_SIZE = 10;

    /**
     * Default flush interval at which metrics are sent
     */
    private static final long DEFAULT_FLUSH_INTERVAL = 30000;

    /**
     * Default transport definition
     */
    private static final Transport DEFAULT_TRANSPORT = Transport.HTTP;

    /**
     * Default aggregations to be applied for Timer metrics
     */
    private static final List<Aggregation> DEFAULT_TIMER_AGGREGATIONS = Lists.newArrayList(Aggregation.AVG, Aggregation.P90, Aggregation.COUNT);

    /**
     * Default aggregations to be applied for Gauge metrics
     */
    private static final List<Aggregation> DEFAULT_GAUGE_AGGREGATIONS = Lists.newArrayList(Aggregation.LAST, Aggregation.MAX, Aggregation.AVG);

    /**
     * Default aggregations to be applied for Counter metrics
     */
    private static final List<Aggregation> DEFAULT_COUNTER_AGGREGATIONS = Lists.newArrayList(Aggregation.COUNT, Aggregation.SUM);

    /**
     * Default value for Aggregations Frequency for metrics
     */
    private static final AggregationFreq DEFAULT_FREQUENCY = AggregationFreq.FREQ_10;

    /**
     * Default value for gauge reporting in milliseconds
     */
    private static final long DEFAULT_GAUGE_REPORTING_INTERVAL = 5000;

    /**
     * Default to enable/disable all the available collectors
     */
    private static final boolean DEFAULT_METRIC_COLLECTION = false;

    /**
     * Default maximum theoretical buffer size that holds metrics in memory between flushes
     */
    private static final int DEFAULT_MAX_BUFFER_SIZE = 5000;

    /**
     * Default Http path to store metrics
     */
    private static final String DEFAULT_HTTP_METRICS_PATH = "/tel/v2.0/metrics";

    /**
     * Statful host, default value {@value #DEFAULT_HOST}
     */
    private String host = DEFAULT_HOST;

    /**
     * Optional Statful default port, default value {@value #DEFAULT_PORT}
     */
    private int port = DEFAULT_PORT;

    /**
     * Defines the transport to be used to set which type of transport will be used to push the metrics.
     */
    private Transport transport = DEFAULT_TRANSPORT;

    /**
     * Defines whether to use https or not, default value {@value #DEFAULT_SECURE}
     */
    private boolean secure = DEFAULT_SECURE;

    /**
     * Defines timeout for the client reporter (http / tcp transports), default value {@value #DEFAULT_TIMEOUT}
     */
    private int timeout = DEFAULT_TIMEOUT;

    /**
     * Application token, to be used by the http transport
     */
    private String token;

    /**
     * Optional value to map to an extra TAG defining the application
     */
    private String app;

    /**
     * Optional configuration to not send any metrics when flushing the buffer, default value {@value #DEFAULT_DRY_RUN}
     */
    private boolean dryrun = DEFAULT_DRY_RUN;

    /**
     * Tags to be applied, default value {@link Collections#emptyList()}
     */
    private List<Pair<String, String>> tags = Collections.emptyList();

    /**
     * List of aggregations to be applied on Timer metrics
     */
    private List<Aggregation> timerAggregations = DEFAULT_TIMER_AGGREGATIONS;

    /**
     * Frequency of aggregation to be applied on Timer metrics
     */
    private AggregationFreq timerFrequency = DEFAULT_FREQUENCY;

    /**
     * List of aggregations to be applied on Gauge metrics
     */
    private List<Aggregation> gaugeAggregations = DEFAULT_GAUGE_AGGREGATIONS;

    /**
     * Frequency of aggregation to be applied on Gauge metrics
     */
    private AggregationFreq gaugeFrequency = DEFAULT_FREQUENCY;

    /**
     * List of aggregations to be applied on Counter metrics
     */
    private List<Aggregation> counterAggregations = DEFAULT_COUNTER_AGGREGATIONS;

    /**
     * Frequency of aggregation to be applied on Counter metrics
     */
    private AggregationFreq counterFrequency = DEFAULT_FREQUENCY;

    /**
     * Global rate sampling. Valid range [1-100], default value {@link #DEFAULT_SAMPLE_RATE}
     */
    private int sampleRate = DEFAULT_SAMPLE_RATE;

    /**
     * Optional name space to to to be applied to the to all metrics, can be overridden in method calls,
     * default value {@value #DEFAULT_NAMESPACE}
     */
    private String namespace = DEFAULT_NAMESPACE;

    /**
     * Defined the periodicity (number of elements collected) of buffer flushes, default value {@value #DEFAULT_FLUSH_SIZE}
     */
    private int flushSize = DEFAULT_FLUSH_SIZE;

    /**
     * Defines the interval at which metrics should be flushed / sent to Statful
     */
    private long flushInterval = DEFAULT_FLUSH_INTERVAL;

    /**
     * Configures a path to read the configuration from a file
     */
    private String configPath;

    /**
     * Holds the list of patterns and replacements to be run against http server requests urls
     */
    private List<Pair<String, String>> patterns = Collections.emptyList();

    /**
     * Holds list of patterns for URLs that should be ignored and not collected.
     */
    private List<String> httpServerPathsIgnore = Collections.emptyList();

    /**
     * Time in milliseconds to report gauges values
     */
    private long gaugeReportingInterval = DEFAULT_GAUGE_REPORTING_INTERVAL;

    /**
     * Enable pool metrics collection
     */
    private boolean enablePoolMetrics = DEFAULT_METRIC_COLLECTION;

    /**
     * Enable http client metrics collection
     */
    private boolean enableHttpClientMetrics = DEFAULT_METRIC_COLLECTION;

    /**
     * Enable http server metrics collection
     */
    private boolean enableHttpServerMetrics = DEFAULT_METRIC_COLLECTION;

    /**
     * Maximum theoretical buffer size that holds metrics in memory between flushes
     */
    private int maxBufferSize = DEFAULT_MAX_BUFFER_SIZE;

    /**
     * Http path to store metrics
     */
    private String httpMetricsPath = DEFAULT_HTTP_METRICS_PATH;

    /**
     * Empty constructor that provides default values, all of which should be overridable
     */
    public StatfulMetricsOptions() {
    }

    /**
     * Copy based constructor
     *
     * @param other The other {@link MetricsOptions} to copy from
     */
    public StatfulMetricsOptions(final StatfulMetricsOptions other) {
        super(other);

        this.host = other.host;
        this.port = other.port;
        this.transport = other.transport;
        this.secure = other.secure;
        this.timeout = other.timeout;
        this.token = other.token;
        this.app = other.app;
        this.dryrun = other.dryrun;
        this.tags = other.tags;
        this.sampleRate = other.sampleRate;
        this.namespace = other.namespace;
        this.flushSize = other.flushSize;
        this.flushInterval = other.flushInterval;
        this.timerAggregations = other.timerAggregations;
        this.timerFrequency = other.timerFrequency;
        this.gaugeFrequency = other.gaugeFrequency;
        this.gaugeAggregations = other.gaugeAggregations;
        this.gaugeReportingInterval = other.gaugeReportingInterval;
        this.enablePoolMetrics = other.enablePoolMetrics;
        this.enableHttpClientMetrics = other.enableHttpClientMetrics;
        this.enableHttpServerMetrics = other.enableHttpServerMetrics;
        this.maxBufferSize = other.maxBufferSize;
        this.httpMetricsPath = other.httpMetricsPath;
    }


    /**
     * Constructor to create a configuration based on a json object
     *
     * @param config {@link JsonObject} to read the configuration from
     */
    public StatfulMetricsOptions(final JsonObject config) {
        super(config);

        this.host = config.getString("host", DEFAULT_HOST);
        this.port = config.getInteger("port", DEFAULT_PORT);
        this.transport = Transport.valueOf(config.getString("transport", DEFAULT_TRANSPORT.toString()));
        this.secure = config.getBoolean("secure", DEFAULT_SECURE);
        this.timeout = config.getInteger("timeout", DEFAULT_TIMEOUT);
        this.token = config.getString("token");
        this.app = config.getString("app");
        this.dryrun = config.getBoolean("dryrun", DEFAULT_DRY_RUN);

        this.tags = config.getJsonArray("tags", new JsonArray())
                .stream()
                .map(JsonObject.class::cast)
                .map(entry -> new Pair<>(entry.getString("tag"), entry.getString("value")))
                .collect(Collectors.toList());

        this.sampleRate = config.getInteger("sampleRate", DEFAULT_SAMPLE_RATE);
        this.namespace = config.getString("namespace", DEFAULT_NAMESPACE);
        this.flushSize = config.getInteger("flushSize", DEFAULT_FLUSH_SIZE);
        this.flushInterval = config.getLong("flushInterval", DEFAULT_FLUSH_INTERVAL);

        this.timerAggregations = this.parseAggregationsConfiguration("timerAggregations", config, DEFAULT_TIMER_AGGREGATIONS);
        this.gaugeAggregations = this.parseAggregationsConfiguration("gaugeAggregations", config, DEFAULT_GAUGE_AGGREGATIONS);
        this.counterAggregations = this.parseAggregationsConfiguration("counterAggregations", config, DEFAULT_COUNTER_AGGREGATIONS);

        this.timerFrequency = AggregationFreq.valueOf(config.getString("timerFrequency", DEFAULT_FREQUENCY.toString()));
        this.gaugeFrequency = AggregationFreq.valueOf(config.getString("gaugeFrequency", DEFAULT_FREQUENCY.toString()));
        this.counterFrequency = AggregationFreq.valueOf(config.getString("counterFrequency", DEFAULT_FREQUENCY.toString()));

        this.patterns = config.getJsonArray("http-server-url-patterns", new JsonArray(Collections.emptyList())).stream()
                .map(JsonObject.class::cast)
                .map(entry -> new Pair<>(entry.getString("pattern"), entry.getString("replacement")))
                .collect(Collectors.toList());

        this.httpServerPathsIgnore = config.getJsonArray("http-server-ignore-url-patterns", new JsonArray(Collections.emptyList())).stream()
                .map(String.class::cast)
                .collect(Collectors.toList());

        this.gaugeReportingInterval = config.getLong("gauge-reporting-interval", DEFAULT_GAUGE_REPORTING_INTERVAL);

        JsonObject collectors = config.getJsonObject("collectors", new JsonObject(Collections.emptyMap()));
        this.enablePoolMetrics = collectors.getBoolean("pool", DEFAULT_METRIC_COLLECTION);
        this.enableHttpClientMetrics = collectors.getBoolean("httpClient", DEFAULT_METRIC_COLLECTION);
        this.enableHttpServerMetrics = collectors.getBoolean("httpServer", DEFAULT_METRIC_COLLECTION);

        this.maxBufferSize = config.getInteger("maxBufferSize", DEFAULT_MAX_BUFFER_SIZE);
        this.httpMetricsPath = config.getString("httpMetricsPath", DEFAULT_HTTP_METRICS_PATH);
    }

    private List<Aggregation> parseAggregationsConfiguration(final String key, final JsonObject config, final List<Aggregation> defaultConfig) {
        JsonArray aggregations = config.getJsonArray(key, new JsonArray(Collections.emptyList()));

        // If not aggregations config specified return the default config
        if (aggregations == null) {
            return defaultConfig;
        }

        // If aggregations config is set to an empty array return and empty aggregations list
        if (aggregations.isEmpty()) {
            return Collections.emptyList();
        }

        // If aggregations config is set to a non-empty array return a list of aggregations values
        return aggregations
                .stream()
                .map(String.class::cast)
                .map(Aggregation::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * @param host target Statful host
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setHost(@Nonnull final String host) {
        this.host = requireNonNull(host);
        return this;
    }

    /**
     * Gets defined host or the default value if none was set
     *
     * @return string with host
     */
    @Nonnull
    public String getHost() {
        return host;
    }

    /**
     * @param port target Statful port
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setPort(final int port) {
        this.port = port;
        return this;
    }

    /**
     * Get defined port or the default value if none was set
     *
     * @return int with port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param transport to be used to send metrics to Statful
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setTransport(@Nonnull final Transport transport) {
        this.transport = requireNonNull(transport);
        return this;
    }

    /**
     * Get defined transport
     *
     * @return {@link Transport} defined
     * @throws NullPointerException if transport is undefined
     */
    @Nonnull
    public Transport getTransport() {
        requireNonNull(transport, "Transport must be defined");
        return transport;
    }

    /**
     * @param secure to be used with HTTP transport and define it it should use a secure connection
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setSecure(final boolean secure) {
        this.secure = secure;
        return this;
    }

    /**
     * Get if secure transport is to be used
     *
     * @return true if secure is to be used, false otherwise
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * @param timeout defines timeout for the client reporter (http / tcp transports)
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setTimeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Get the timeout to be used for the http and tcp transports
     *
     * @return int value in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param token set application token
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setToken(@Nonnull final String token) {
        this.token = requireNonNull(token);
        return this;
    }

    /**
     * gets the token defined
     *
     * @return String token
     * @throws IllegalArgumentException if no token is configured
     */
    public String getToken() {
        return Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("No token provided."));
    }

    /**
     * @param app defines application name to add to the metrics
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setApp(@Nullable final String app) {
        this.app = app;
        return this;
    }

    /**
     * Optional value to add to the tag list
     *
     * @return {@link Optional#empty()} if nothing is defined, string value if defined
     */
    public Optional<String> getApp() {
        return Optional.ofNullable(app);
    }

    /**
     * @param dryrun set the system to not send any metrics when flushing the buffer
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setDryrun(final boolean dryrun) {
        this.dryrun = dryrun;
        return this;
    }

    /**
     * Value that defines if the metrics should be sent or not
     *
     * @return true if metrics should be not be sent false otherwise
     */
    public boolean isDryrun() {
        return dryrun;
    }

    /**
     * Performs a shallow copy of the input list
     *
     * @param tags sets a list of tags to be applied
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setTags(@Nonnull final List<Pair<String, String>> tags) {
        this.tags = new ArrayList<>(requireNonNull(tags));
        return this;
    }

    /**
     * @return List of tags to be applied
     */
    @Nonnull
    public List<Pair<String, String>> getTags() {
        return tags;
    }

    /**
     * @param sampleRate set rate sampling. Valid range [1-100]
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setSampleRate(final int sampleRate) {
        if (sampleRate < MIN_SAMPLE_RATE || sampleRate > MAX_SAMPLE_RATE) {
            throw new IllegalArgumentException("Invalid sample rate. Valid values between [1-100]. Provided:" + sampleRate);
        }
        this.sampleRate = sampleRate;
        return this;
    }

    /**
     * @return int value with the defined sample rate
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * @param namespace set namespace to be applied to the to all metrics, can be overridden in method calls
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setNamespace(@Nonnull final String namespace) {
        this.namespace = requireNonNull(namespace);
        return this;
    }

    /**
     * @return String with the namespace value to apply to metrics
     */
    @Nonnull
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param flushSize Defined the periodicity (number of elements collected) of buffer flushes, default value {@value #DEFAULT_FLUSH_SIZE}
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setFlushSize(final int flushSize) {
        this.flushSize = flushSize;
        return this;
    }

    /**
     * @return int value with the size of the flush buffer
     */
    public int getFlushSize() {
        return flushSize;
    }

    /**
     * @return int value  of the milliseconds between buffer flushes
     */
    public long getFlushInterval() {
        return flushInterval;
    }

    /**
     * @param flushInterval long value  of the milliseconds between buffer flushes
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setFlushInterval(final long flushInterval) {
        this.flushInterval = flushInterval;
        return this;
    }

    /**
     * @return a copy of the aggregations applied on timer metrics
     */
    @Nonnull
    public List<Aggregation> getTimerAggregations() {
        return Lists.newArrayList(timerAggregations);
    }

    /**
     * @param timerAggregations list of aggregations to apply
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public StatfulMetricsOptions setTimerAggregations(@Nonnull final List<Aggregation> timerAggregations) {
        this.timerAggregations = Lists.newArrayList(Objects.requireNonNull(timerAggregations));
        return this;
    }

    /**
     * @return the frequency to be applied on timer metrics
     */
    @Nonnull
    public AggregationFreq getTimerFrequency() {
        return timerFrequency;
    }

    /**
     * @param timerFrequency the frequency to apply on the aggregations
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public StatfulMetricsOptions setTimerFrequency(@Nonnull final AggregationFreq timerFrequency) {
        this.timerFrequency = Objects.requireNonNull(timerFrequency);
        return this;
    }


    /**
     * @return a copy of the aggregations applied on timer metrics
     */
    @Nonnull
    public List<Aggregation> getGaugeAggregations() {
        return Lists.newArrayList(gaugeAggregations);
    }

    /**
     * @param gaugeAggregations list of aggregations to apply
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public StatfulMetricsOptions setGaugeAggregations(@Nonnull final List<Aggregation> gaugeAggregations) {
        this.gaugeAggregations = Lists.newArrayList(Objects.requireNonNull(gaugeAggregations));
        return this;
    }

    /**
     * @return the frequency to be applied on gauge metrics
     */
    @Nonnull
    public AggregationFreq getGaugeFrequency() {
        return gaugeFrequency;
    }

    /**
     * @param gaugeFrequency the frequency to apply on the aggregations
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public StatfulMetricsOptions setGaugeFrequency(@Nonnull final AggregationFreq gaugeFrequency) {
        this.gaugeFrequency = Objects.requireNonNull(gaugeFrequency);
        return this;
    }

    /**
     * @return a copy of the aggregations applied on counter metrics
     */
    @Nonnull
    public List<Aggregation> getCounterAggregations() {
        return Lists.newArrayList(counterAggregations);
    }

    /**
     * @param counterAggregations list of aggregations to apply
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public StatfulMetricsOptions setCounterAggregations(@Nonnull final List<Aggregation> counterAggregations) {
        this.counterAggregations = Lists.newArrayList(Objects.requireNonNull(counterAggregations));
        return this;
    }

    /**
     * @return the frequency to be applied on counter metrics
     */
    @Nonnull
    public AggregationFreq getCounterFrequency() {
        return counterFrequency;
    }

    /**
     * @param counterFrequency the frequency to apply on the aggregations
     * @return a reference to this, so the API can be used fluently
     */
    @Nonnull
    public StatfulMetricsOptions setCounterFrequency(@Nonnull final AggregationFreq counterFrequency) {
        this.counterFrequency = Objects.requireNonNull(counterFrequency);
        return this;
    }

    /**
     * @return the default frequency to be applied on metrics
     */
    @Nonnull
    public static AggregationFreq getDefaultFrequency() {
        return DEFAULT_FREQUENCY;
    }

    /**
     * Allows setting enabled as a jvm argument
     *
     * @return a reference to this, so the API can be used fluently
     */
    @Override
    public StatfulMetricsOptions setEnabled(final boolean enable) {
        return (StatfulMetricsOptions) super.setEnabled(enable);
    }

    /**
     * @return path to load the configuration from
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Sets a config path to read the configuration from
     *
     * @param configPath path to be set
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setConfigPath(final String configPath) {
        this.configPath = configPath;
        return this;
    }

    /**
     * Allows to set regex that will be executed against the http server requests to replace parts of it and avoid
     * polluting the metrics system with lots of tags
     *
     * @param patternsToAdd A pair with the regex and it's replacement
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setHttpServerMatchAndReplacePatterns(@Nonnull final List<Pair<String, String>> patternsToAdd) {
        this.patterns = requireNonNull(Lists.newArrayList(patternsToAdd));
        return this;
    }

    /**
     * @return the list of patterns and replacements to apply on request urls
     */
    @Nonnull
    public List<Pair<String, String>> getPatterns() {
        return patterns;
    }

    /**
     * Allows to set regex that will make urls be ignored and not collected
     *
     * @param pathsToIgnore regular expressions to be used to ignore urls
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setHttpServerIgnorePaths(@Nonnull final List<String> pathsToIgnore) {
        this.httpServerPathsIgnore = requireNonNull(Lists.newArrayList(pathsToIgnore));
        return this;
    }

    /**
     * @return list of patterns to be ignored and not collected
     */
    public List<String> getHttpServerPathsIgnore() {
        return httpServerPathsIgnore;
    }

    /**
     * @param gaugeReportingInterval time in milliseconds to run the gauge reporting
     */
    public void setGaugeReportingInterval(final long gaugeReportingInterval) {
        this.gaugeReportingInterval = gaugeReportingInterval;
    }

    /**
     * @return gets value configured for gauge intervals
     */
    public long getGaugeReportingInterval() {
        return gaugeReportingInterval;
    }

    /**
     * @return gets value configured for enable pool metrics
     */
    public boolean isEnablePoolMetrics() {
        return enablePoolMetrics;
    }

    /**
     * Enable pool metrics collection
     * @param enablePoolMetrics flag to enable pool metric collection
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setEnablePoolMetrics(final boolean enablePoolMetrics) {
        this.enablePoolMetrics = enablePoolMetrics;
        return this;
    }

    /**
     * @return gets value configured for enable http client metrics
     */
    public boolean isEnableHttpClientMetrics() {
        return enableHttpClientMetrics;
    }

    /**
     * Enable http client metrics collection
     * @param enableHttpClientMetrics flag to enable http client metric collection
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setEnableHttpClientMetrics(final boolean enableHttpClientMetrics) {
        this.enableHttpClientMetrics = enableHttpClientMetrics;
        return this;
    }

    /**
     * @return gets value configured for enable http server metrics
     */
    public boolean isEnableHttpServerMetrics() {
        return enableHttpServerMetrics;
    }

    /**
     * Enable http server metrics collection
     * @param enableHttpServerMetrics flag to enable http server metric collection
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setEnableHttpServerMetrics(final boolean enableHttpServerMetrics) {
        this.enableHttpServerMetrics = enableHttpServerMetrics;
        return this;
    }

    /**
     * @return gets the max number of metrics to be kept in memory
     */
    public int getMaxBufferSize() {
        return this.maxBufferSize;
    }

    /**
     * Sets the max number of metrics to be kept in memory
     * @param maxBufferSize value to set
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setMaxBufferSize(final int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
        return this;
    }

    /**
     * @return gets the Http path to store metrics
     */
    public String getHttpMetricsPath() {
        return this.httpMetricsPath;
    }

    /**
     * Sets the Http path to store metrics
     * @param httpMetricsPath uri to store metrics
     * @return a reference to this, so the API can be used fluently
     */
    public StatfulMetricsOptions setHttpMetricsPath(final String httpMetricsPath) {
        this.httpMetricsPath = httpMetricsPath;
        return this;
    }
}
