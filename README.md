Statful Client for Vert.x
==============

[![Build Status](https://travis-ci.org/statful/statful-client-vertx.svg?branch=master)](https://travis-ci.org/statful/statful-client-vertx)

Statful client for Vert.x written in Java. This client enables pushing custom metrics as well as metrics collection for [Vert.x](http://vertx.io/) based projects and sends them to [Statful.](http://statful.com/)

This client leverages the capabilities provided by [Vert.x SPI](http://vertx.io/docs/apidocs/io/vertx/core/spi/metrics/VertxMetrics.html) to collect metrics.

Please check the project’s [pom.xml](https://github.com/statful/statful-client-vertx/blob/master/pom.xml) file to see the current Vert.x version dependency since we always aim to support the latest stable version.

## Table of Contents

* [Supported Versions](#supported-versions)
* [Requirements](#requirements)
* [Quick Start](#quick-start)
	* [Programmatically](#programmatically)
	* [Configuration File](#configuration-file)
* [Configurations](#configurations)
	* [Global Configurations](#global-configurations)
	* [Statful's Configurations Specific to Vert.x](#statfuls-configurations-specific-to-vert.x)
* [Usage](#usage)
	* [Custom Metrics](#custom-metrics)
	* [Metrics Collection](#metrics-collection)
* [Limitations](#limitations)
* [Authors](#authors)
* [License](#license)

## Supported Versions

| Statful Client version | Tested Java versions  | Tested Vert.x versions
|:---|:---|:---|
| 1.x.x | `Java 8` | `3.4.2` |

## Requirements

This client has the following requirement:

* [Vert.x](http://vertx.io/) 

## Quick Start

Add the client to the project dependencies:

    <dependency>
       <groupId>com.statful.client</groupId>
       <artifactId>client-vertx</artifactId>
      <version>${version-number}</version>
    </dependency>

Vert.x handles the creation of the Metrics Service Provider Interface ([SPI](https://vertx.io/docs/vertx-core/java/#_metrics_spi)).

From this point, you can set up the client in two ways, programmatically or by using a configuration file.

### Programmatically

Create an instance of *StatfulMetricsOptions* and set all the desired parameters. Use the configured instance as an argument for *VertxOptions* when creating the *Vertx* instance.

Set the `StatfulMetricsFactoryImpl` factory to be used by the Metrics SPI.

Example:

    StatfulMetricsOptions options = new StatfulMetricsOptions()
            .setEnabled(true)
            .setFactory(new StatfulMetricsFactoryImpl());
            
    VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
    Vertx vertx = Vertx.vertx(vertxOptions);
    
When using a custom `Launcher` that implements the `VertxLifecycleHooks` interface, it’s expected of the user to override the `beforeStartingVertx` method.

Example:

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new StatfulMetricsOptions()
                .setEnabled(true)
                .setFactory(new StatfulMetricsFactoryImpl()));
    }

### Configuration File

You need to provide two parameters when launching your application, one to enable metrics and another to set the configuration file. Enabling metrics triggers *Vertx* to init the metrics system which will then read the configuration file.

Example:

    -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.configPath=config/statful.json

The configuration file is a simple JSON document, like the sample shown below:

    {
      "host": "api.statful.com",
      "port": 443,
      "transport": "HTTP",
      "httpMetricsPath": "/tel/v2.0/metrics",
      "secure": true,
      "app": "application",
      "dryrun": false,
      "sampleRate": 100,
      "namespace": "namespace",
      "timerAggregations": [
        "AVG",
        "COUNT"
      ],
      "counterAggregations": [],
      "timerFrequency": "FREQ_10",
      "enabled": true,
      "http-server-url-patterns": [
        {
          "pattern": "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}",
          "replacement": "_uuid_"
        }
      ],
      "http-server-ignore-url-patterns": [
        "/static/.*"
      ],
      "collectors": {
        "pool": true,
        "httpClient": false,
        "httpServer": true
      }
    }

## Configurations
The following section presents detailed information on the available options for setup in the configuration parameters.

### Global Configurations

| Option | Description | Type | Default | Required |
|:---|:---|:---|:---|:---|
| _host_ | Defines the hostname to where the metrics are sent. | `string` | `api.statful.com` | **NO** |
| _port_ | Defines the port to where the metrics are sent. | `string` | `443` | **NO** |
| _secure_ | Enables or disables https. | `boolean` | `true` | **NO** |
| _timeout_ | Defines the timeout for http transport, in **milliseconds**.<br><br>Not yet supported. | `number` | `2000` | **NO** |
| _token_ | Defines the token used to match incoming data to Statful.| `string` | **none** | **YES** |
| _app_ | Defines the application's global name. When specified, it sets a global tag like `app=setValue`. | `string` | **none** | **NO** |
| _dryRun_ | Defines if metrics should be output to the logger instead of being sent to Statful (useful for testing/debugging purposes). | `boolean` | `false` | **NO** |
| _tags_ | Object for setting the global tags. | `object` | `{}` | **NO** |
| _sampleRate_ | Defines the rate sampling. <br><br>**It should be a number between [1, 100]**. | `number` | `100` | **NO** |
| _namespace_ | Defines the global namespace. A prefix could be set if the user sends metrics through Statful. | `string` | `application` | **NO** |
| _flushSize_ | Defines the maximum buffer size before performing a flush. | `number` | `10` | **NO** |
| _flushInterval_ | Defines an interval to periodically flush the buffer based on time. | `number` | `30000` | **NO** |
| _maxBufferSize_ | Defines how many metrics at max are kept in the buffer between forced flushes. | `number` | `5000` | **NO** |


### Statful's Configurations Specific to Vert.x

| Option | Description | Type | Default | Required |
|:---|:---|:---|:---|:---|
| _enabled_ | Enables or disables the client. | `boolean` | `true` | **NO** |
| _timerAggregations_ | Defines the aggregations to apply to timer-based metrics. | `string` | `[Aggregation.AVG, Aggregation.P90, Aggregation.COUNT]` | **NO** |
| _counterAggregations_ | Defines the aggregations to apply to counter-based metrics. | `string` | `[Aggregation.COUNT, Aggregation.SUM]` | **NO** |
| _counterFrequency_ | Defines the aggregation frequency for counter-based metrics. | `string` | `[AggregationFreq.FREQ_10]` | **NO** |
| _gaugeAggregations_ | Defines the aggregations to apply to gauge-based metrics. | `string` | ` [Aggregation.LAST, Aggregation.MAX, Aggregation.AVG]` | **NO** |
| _gaugeFrequency_ | Defines the aggregation frequency for gauge-based metrics. | `string` | `[AggregationFreq.FREQ_10]` | **NO** |
| _http-server-url-patterns_ | Defines patterns to transform URLs for metrics collection. | `string` | **none** | **NO** |
| _http-server-ignore-url-patterns_ | Defines patterns of URLs that you don't want to be tracked. | `string` | **none** | **NO** |
| _gauge-reporting-interval_ | Defines the value for gauge reporting in **milliseconds.** | `number` | `5000` | **NO** |
| _httpMetricsPath_ | Defines the path to send metrics when the http transport is set. | `string` | `/tel/v2.0/metrics` | **NO** |
| _transport_ | Defines the transport type to use when sending metrics to Statful. <br><br>**Valid Transports:**`UDP, HTTP`| `string` | `HTTP` | **NO** |

> To disable aggregations for a type of metric, set the value of the aggregations' configuration to an empty list.
    
#### Metric collectors
To enable metric collection, you must set the option with the flags of the available collectors.

| Option | Description | Type | Default | Required |
|:---|:---|:---|:---|:---|
| _collectors_ | Object to employ metric collectors. | `object` | **none** | **NO** |
     
List of available collectors:

| Option | Description | Type | Default | Required |
|:---|:---|:---|:---|:---|
| _pool_ | Enables the collection of metrics from application pools. | `boolean` | `false` | **NO** |
| _httpClient_ | Enables the collection of metrics from http clients. | `boolean` | `false` | **NO** |
| _httpServer_ | Enables the collection of metrics from http servers. | `boolean` | `false` | **NO** |


## Usage

### Custom Metrics

To save custom metrics, send a message to the *eventBus* such as:

    EventBus eventBus = vertx.eventBus();
    eventBus.send(CustomMetricsConsumer.ADDRESS, 
        new CustomMetric.Builder()
            .withMetricName(getMetric()) // String
            .withValue(getValue()) // long
            .withTimestamp(getTimestamp()) // EpochSecond
            .withTags(getTags())
            .build();

A `CustomMetricsConsumer` subscribes to the `CustomMetricsConsumer.ADDRESS` and is responsible for saving the metrics to the in-memory buffer.

Only the `metricName` and `value` are required for a valid metric. Please refer to the `CustomMetric` class for more details. 
    
### Metrics Collection

#### Http Client

Track the time that takes to execute each request and its response status code. Currently, the connection time is not reported.

To identify a request that you want to track, add a request header to that request. The pattern used to recognize and extract the request header is *Tags.TRACK_HEADER*, where its value (of the header) should be set to the desired value at the *com/statful/tag/Tags.java* file.

#### Http Server

By default, all requests are tracked. To ignore a path, configure a regex that matches that path. For instance, to ignore a request that starts with */static* apply the following configuration:

    "http-server-ignore-url-patterns": [
      "\/static\/.*"
    ]

URLs are used as tag values. To avoid creating tags with IDs, or any other unexpected identifier from your URLs, you can configure the transformation regex to be applied. Here’s an example of how to remove UUIDs from the URLs on a restful API:

    "http-server-url-patterns": [
      {
        "pattern": "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}",
        "replacement": "_uuid_"
      }
    ]

This configuration transforms:

    /user/095b1cac-c153-4c6b-a09c-1bd4a1220019/update

Into:

    /user_uuid_/update

> Please keep in mind that any regex will be applied to all existing URLs so, the more regex there are to be applied, the heavier the execution will be.

#### Pools

It’s possible to track *Vertx* *ConnectionPool* usage metrics.


## Limitations

The current implementation has the following limitation:

* It does not support http2 metrics introduced by Vert.x version 3.3.0.

## Authors

[Mindera - Software Craft](https://github.com/Mindera)

## License

Statful's Vert.x Client is available under the MIT license. See the [LICENSE](https://raw.githubusercontent.com/statful/statful-client-vertx/master/LICENSE) file for more information.
