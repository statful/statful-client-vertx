Statful Client for Vertx
==============

[![Build Status](https://travis-ci.org/statful/statful-client-vertx.svg?branch=master)](https://travis-ci.org/statful/statful-client-vertx)

Statful client for Vertx. This client enables metrics collection for [Vert.x](http://vertx.io/) based projects and report them to [Statful](http://statful.com/)

This client leverages the capabilities provided by [Vert.x SPI](http://vertx.io/docs/apidocs/io/vertx/core/spi/metrics/VertxMetrics.html) to collect metrics.

Please check the project pom.xml to see Vert.x version dependency since this will be a moving target, the aim is to always support the latest stable version.

## Table of Contents

* [Supported Versions](#supported-versions)
* [Requirements](#requirements)
* [Quick Start](#quick-start)
* [Configuration](#configuration)
* [Reference](#reference)
* [Limitations](#limitations)
* [Authors](#authors)
* [License](#license)

## Supported Versions

| Statful client version | Tested Java versions  | Tested Vertx versions
|:---|:---|:---|
| 1.x.x | `Java 8` | `3.3.3` |

## Requirements

This client has the following requirements:

* [vert.x](http://vertx.io/) 

## Quick start

Add the client to your dependencies

    <dependency>
       <groupId>com.statful.client</groupId>
       <artifactId>client-vertx</artifactId>
      <version>${version-number}</version>
    </dependency>

[Vert.x](http://vertx.io/) handles the creation of the Metrics SPI for you.

There are two main ways to configure the client, programmatically or using a config file.

### Programmatically

You create an instance of *StatfulMetricsOptions* set all the parameters that you want and and use it as an argument for *VertxOptions* when creation your *Vertx* instance.

Example:

    StatfulMetricsOptions options = new StatfulMetricsOptions().setEnabled(true);
    // configure remaining desired options...
    VertxOptions vertxOptions = new VertxOptions().setMetricsOptions(options);
    Vertx vertx = Vertx.vertx(vertxOptions);

### Configuration file

You need to provide to parameters when launching your application, one to enable metrics and one to set the configuration file. Enable metrics will make *Vertx* init the metrics system which will then read the configuration file.

Example:

    -Dvertx.metrics.options.enabled=true -Dvertx.metrics.options.configPath=config/statful.json

The configuration file is a simple json document, a sample can be seen bellow

    {
        "host": "api.statful.com",
        "port": 443,
        ...
    }

# Configuration

General configuration for a Statful client.

    * prefix [required] - global metrics prefix
    * host [optional] [default: '127.0.0.1']
    * port [optional] [default: 2013]
    * secure [not supported yet] [default: true] - enable or disable https
    * timeout [not supported yet] [default: 2000ms] - timeout for http/tcp transports
    * token [optional] - An authentication token to send to Statful
    * app [optional] - if specified set a tag ‘app=foo’
    * dryrun [optional] [default: false] - do not actually send metrics when flushing the buffer
    * tags [optional] - global list of tags to set
    * sampleRate [optional] [default: 100] [between: 1-100] - global rate sampling
    * namespace [optional] [default: ‘application’] - default namespace (can be overridden in method calls)
    * flushSize [optional] [default: 10] - defines the periodicity of buffer flushes
    * flushInterval [optional] [default: 0] - defines an interval to flush the metrics
    * maxBufferSize [optional] [default: 5000] - defines how many metrics at max are kept in memory between flushes

Vertx Statful specific configurations:

    * enabled - true if you want to enable metrics false otherwise
    * timerFrequency - aggregation frequency for timer based metrics
    * timerAggregations - aggregations to be applied to timer based metrics
    * http-server-url-patterns - patterns to transform urls for metrics collection
    * http-server-ignore-url-patterns - patterns of urls that you won't want tracked
    * gauge-collection-interval - to avoid reporting gauges every time it changes
    * httpMetricsPath [optional] - path to send metrics to when http transport is set
    
Metric collectors:

    * collectors - list of boolean flags to enable or disable the available metric collectors
     
List of available collectors:

    * pool - collect metrics from application pools
    * httpClient - collect metrics from http clients
    * httpServer - collect metrics from http servers
    
## Reference

Type of metrics collected:

### Http Client

Track the time that takes to execute each request and it's response status code. Currently connection time is not reported

To identify that a request should be tracked you need to add a request header to your request. The name of the header should be extracted from: *com.statful.tag.Tags.TRACK_HEADER*, the value should be set to whatever value you want to use for the tag

### Http Server

By default all requests are tracked. To ignore a path configure a regex that matches that path. For instance, if you want to ignore anything that starts with */static* apply the following configuration:

    "http-server-ignore-url-patterns": [
      "\/static\/.*"
    ]

URLs will be used as a TAG, so in order to avoid creating tags with IDs, or any other type of variable in your URLs you can configure transformation regex to be applied. An example would be removing UUIDs from the URLs on a restful API

    "http-server-url-patterns": [
      {
        "pattern": "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}",
        "replacement": "_uuid_"
      }
    ]

This configuration would transform

    /user/095b1cac-c153-4c6b-a09c-1bd4a1220019/update

into

    /user_uuid_/update

> Please keep in mind that we are applying the regex to all urls so the more regex you want to apply the heavier the execution will be.

## Limitations

The current implementation has the following limitations:

* only supports the UDP transport.
* Does not support http2 metrics introduced by vertx version 3.3.0.

## Authors

[Mindera - Software Craft](https://github.com/Mindera)

## License

Statful Vertx Client is available under the MIT license. See the [LICENSE](https://raw.githubusercontent.com/statful/statful-client-vertx/master/LICENSE) file for more information.