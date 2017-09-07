package com.statful.sender;

import com.statful.client.StatfulMetricsOptions;
import com.statful.metric.DataPoint;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Responsible for the HTTP transport
 */
public class HttpSender extends MetricsHolder {

    /**
     * Logger for transport errors
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSender.class);

    /**
     * Header for the authorization token
     */
    private static final String TOKEN_HEADER = "M-Api-Token";

    /**
     * Statful options to configure the sender
     */
    private final StatfulMetricsOptions options;

    /**
     * Client to send metrics
     */
    private HttpClient client;

    /**
     * @param vertx   vertx instance to create the socket from
     * @param context of execution to run operations that need vertx initialized
     * @param options Statful options to configure host and port
     */
    public HttpSender(final Vertx vertx, final Context context, final StatfulMetricsOptions options) {
        super(options, new Sampler(options, new Random()));

        this.options = options;

        context.runOnContext(aVoid -> {
            final HttpClientOptions httpClientOptions = new HttpClientOptions()
                    .setDefaultHost(options.getHost())
                    .setDefaultPort(options.getPort())
                    .setSsl(options.isSecure());

            this.client = vertx.createHttpClient(httpClientOptions);
            this.configureFlushInterval(vertx, this.options.getFlushInterval());
        });
    }

    @Override
    public void send(@Nonnull final List<DataPoint> metrics, @Nonnull final Handler<AsyncResult<Void>> sentHandler) {
        this.bundleAndSend(metrics, sentHandler);
    }

    @Override
    public void send(@Nonnull final List<DataPoint> metrics) {
        this.bundleAndSend(metrics, null);
    }

    private void bundleAndSend(@Nonnull final List<DataPoint> metrics, final Handler<AsyncResult<Void>> endHandler) {
        this.bundleMetrics(metrics).ifPresent(toSendMetrics -> send(endHandler, toSendMetrics));
    }

    private void send(final Handler<AsyncResult<Void>> handler, final String toSendMetrics) {
        final Optional<Handler<AsyncResult<Void>>> endHandler = Optional.ofNullable(handler);

        final HttpClientRequest request = client.request(HttpMethod.PUT, options.getHttpMetricsPath(), response -> {
            if (response.statusCode() != HttpResponseStatus.CREATED.code()) {
                LOGGER.error("Failed to send metrics: " + response.statusMessage() + " - Payload: " + toSendMetrics);
                endHandler.ifPresent(callerHandler -> callerHandler.handle(Future.failedFuture(response.statusMessage())));
            } else {
                endHandler.ifPresent(callerHandler -> callerHandler.handle(Future.succeededFuture()));
            }
        });

        request.putHeader(TOKEN_HEADER, options.getToken());

        request.end(toSendMetrics);
    }

    @Override
    public void close(final Handler<AsyncResult<Void>> handler) {
        final Optional<Handler<AsyncResult<Void>>> closeHandler = Optional.ofNullable(handler);

        try {
            this.client.close();
            closeHandler.ifPresent(close -> close.handle(Future.succeededFuture()));
        } catch (Exception e) {
            LOGGER.error("Failed to close http client", e);
            closeHandler.ifPresent(close -> close.handle(Future.failedFuture(e)));
        }
    }
}
