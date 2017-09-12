Statful Client for Vertx
==============

[![Build Status](https://travis-ci.org/statful/statful-client-vertx.svg?branch=master)](https://travis-ci.org/statful/statful-client-vertx)

Statful client for Vertx. This client enables pushing custom metrics as well as metrics collection for [Vert.x](http://vertx.io/) based projects and report them to [Statful](http://statful.com/)

This client leverages the capabilities provided by [Vert.x SPI](http://vertx.io/docs/apidocs/io/vertx/core/spi/metrics/VertxMetrics.html) to collect metrics.

Please check the project pom.xml to see Vert.x version dependency since this will be a moving target, the aim is to always support the latest stable version.

## Table of Contents

* [Supported Versions](#supported-versions)
* [Requirements](#requirements)
* [Quick Start](#quick-start)
* [Configuration](#configuration)
* [Usage](#usage)
* [Limitations](#limitations)
* [Authors](#authors)
* [License](#license)

## Supported Versions

| Statful client version | Tested Java versions  | Tested Vertx versions
|:---|:---|:---|
| 1.x.x | `Java 8` | `3.4.2` |

## Requirements

This client has the following requirements:

* [vert.x](http://vertx.io/) 

## Quick start

Add the client to the project dependencies:

    <dependency>
       <groupId>com.statful.client</groupId>
       <artifactId>client-vertx</artifactId>
      <version>${version-number}</version>
    </dependency>

[Vert.x](http://vertx.io/) handles the creation of the Metrics SPI.

There are two main ways to configure the client, programmatically or using a configuration file.

### Programmatically

Create an instance of *StatfulMetricsOptions* and set all the desired parameters and use it as an argument for *VertxOptions* when creating the *Vertx* instance.

Set the `StatfulMetricsFactoryImpl` factory to be used by the Metrics SPI.

Example:

    StatfulMetricsOptions options = new StatfulMetricsOptions()
            .setEnabled(true)
            .setFactory(new StatfulMetricsFactoryImpl());
            
    VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
    Vertx vertx = Vertx.vertx(vertxOptions);
    
When using a custom `Launcher` that implements the `VertxLifecycleHooks` interface the `beforeStartingVertx` method should be overwritten.

Example:

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new StatfulMetricsOptions()
                .setEnabled(true)
                .setFactory(new StatfulMetricsFactoryImpl()));
    }

### Configuration file

You need to provide two parameters when launching your application, one to enable metrics and one to set the configuration file. Enable metrics will make *Vertx* init the metrics system which will then read the configuration file.

Example:

    -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.configPath=config/statful.json

The configuration file is a simple json document, a sample can be seen bellow:

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

## Configuration

General configuration for a Statful client.

    * host [optional] [default: 'api.statful.com']
    * port [optional] [default: 443]
    * secure [not supported yet] [default: true] - enable or disable https
    * timeout [not supported yet] [default: 2000ms] - timeout for http transport
    * token - An authentication token to send to Statful
    * app [optional] - if specified set a tag ‘app=foo’
    * dryrun [optional] [default: false] - debug log metrics when flushing the buffer
    * tags [optional] - global list of tags to set, these are merged with custom tags set on method calls with priority to custom tags
    * sampleRate [optional] [default: 100] [between: 1-100] - global rate sampling
    * namespace [optional] [default: 'application'] - default namespace
    * flushSize [optional] [default: 10] - defines the periodicity of buffer flushes by buffer size
    * flushInterval [optional] [default: 0] - defines an interval to periodically flush the buffer based on time
    * maxBufferSize [optional] [default: 5000] - defines how many metrics at max are kept in the buffer between forced flushes

Vertx Statful specific configurations:

    * enabled [optional] [default: true] - enable/disable the client
    * timerAggregations [optional] [default: [Aggregation.AVG, Aggregation.P90, Aggregation.COUNT] ] - aggregations to be applied to timer based metrics
    * timerFrequency [optional] [default: AggregationFreq.FREQ_10] - aggregation frequency for timer based metrics
    * counterAggregations [optional] [default: [Aggregation.COUNT, Aggregation.SUM] ] - aggregations to be applied to counter based metrics
    * counterFrequency [optional] [default: AggregationFreq.FREQ_10] - aggregation frequency for counter based metrics
    * gaugeAggregations [optional] [default: [Aggregation.LAST, Aggregation.MAX, Aggregation.AVG] ] - aggregations to be applied to gauge based metrics
    * gaugeFrequency [optional] [default: AggregationFreq.FREQ_10] - aggregation frequency for gauge based metrics
    * http-server-url-patterns [optional] - patterns to transform urls for metrics collection
    * http-server-ignore-url-patterns [optional] - patterns of urls that you won't want tracked
    * gauge-collection-interval [optional] [default: ] - to avoid reporting gauges every time it changes
    * httpMetricsPath [optional] [default: '/tel/v2.0/metrics'] - path to send metrics to when http transport is set
    * transport [optional] [default: HTTP] - type of transport to be used when sending metrics to statful (UDP/HTTP)
    
> To disable aggregations for a type of metric set the value of the aggregations configuration to an empty list.
    
Metric collectors:

    * collectors [optional] - list of boolean flags to enable or disable the available metric collectors
     
List of available collectors:

    * pool [optional] [default: false] - collect metrics from application pools
    * httpClient [optional] [default: false] - collect metrics from http clients
    * httpServer [optional] [default: false] - collect metrics from http servers
    
## Usage

### Custom Metrics

In order to save custom metrics send a message to the *eventBus* such as:

    EventBus eventBus = vertx.eventBus();
    eventBus.send(CustomMetricsConsumer.ADDRESS, 
        new CustomMetric.Builder()
            .withMetricName(getMetric()) // String
            .withValue(getValue()) // long
            .withTimestamp(getTimestamp()) // EpochSecond
            .withTags(getTags())
            .build();

A `CustomMetricsConsumer` subscribes to the `CustomMetricsConsumer.ADDRESS` and is responsible for saving the metrics to the in memory buffer.

Only `metricName` and `value` are required for a valid metric. Please refer to the `CustomMetric` class for more details. 
    
### Metric Collection

#### Http Client

Track the time that takes to execute each request and it's response status code. Currently connection time is not reported.

To identify that a request should be tracked add a request header to a request. The name of the header should be extracted from: *com.statful.tag.Tags.TRACK_HEADER*, the value should be set to whatever value desired for the tag.

#### Http Server

By default all requests are tracked. To ignore a path configure a regex that matches that path. For instance, to ignore anything that starts with */static* apply the following configuration:

    "http-server-ignore-url-patterns": [
      "\/static\/.*"
    ]

URLs will be used as a TAG, so in order to avoid creating tags with IDs, or any other type of variable in your URLs you can configure transformation regex to be applied. An example would be removing UUIDs from the URLs on a restful API:

    "http-server-url-patterns": [
      {
        "pattern": "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}",
        "replacement": "_uuid_"
      }
    ]

This configuration would transform:

    /user/095b1cac-c153-4c6b-a09c-1bd4a1220019/update

Into:

    /user_uuid_/update

#### Pools

Track *Vertx* *ConnectionPool* usage metrics.

> Please keep in mind that regex are applied to all urls so the more regex are to apply the heavier the execution will be.

## Limitations

The current implementation has the following limitations:

* Does not support http2 metrics introduced by vertx version 3.3.0.

## Authors

[Mindera - Software Craft](https://github.com/Mindera)

## License

Statful Vertx Client is available under the MIT license. See the [LICENSE](https://raw.githubusercontent.com/statful/statful-client-vertx/master/LICENSE) file for more information.