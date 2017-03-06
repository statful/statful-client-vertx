package com.statful.client;

import com.statful.tag.Tags;
import io.netty.util.internal.ThreadLocalRandom;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.TestContext;

import java.util.List;

class IntegrationTestCase {
    private static final int HTTP_PORT = 1235;
    private static final String HOST = "0.0.0.0";

    private HttpServer httpReceiver;

    void setUpHttpReceiver(Vertx vertx) throws Exception {
        this.httpReceiver = vertx.createHttpServer();
    }

    void configureHttpReceiver(final Vertx vertx, final TestContext context, final List<String> requestsWithIgnore) {
        httpReceiver.requestHandler(request -> {
            // create a delay to simulate a lengthy api call
            long delay = ThreadLocalRandom.current().nextInt(200, 1000 + 1);
            vertx.setTimer(delay, event -> request.response().end("hey!"));
        }).listen(HTTP_PORT, HOST, listenResult -> {
            if (listenResult.succeeded()) {
                this.makeHttpRequests(vertx, context, requestsWithIgnore);
            } else {
                context.fail(listenResult.cause());
            }
        });
    }

    private void makeHttpRequests(Vertx vertx, TestContext context, List<String> requests) {
        requests.forEach(requestValue -> {
            // confirm that all requests have a 200 status code
            HttpClientRequest request = vertx.createHttpClient().get(HTTP_PORT, HOST, "/" + requestValue, event -> context.assertTrue(event.statusCode() == 200));
            request.headers().add(Tags.TRACK_HEADER.toString(), requestValue);
            request.end();
        });
    }

    /**
     * Not using junit @after annotation since we want to wait for the servers to close
     *
     * @param handler completion handler
     */
    void teardownHttpReceiver(Handler<AsyncResult<Void>> handler) {
        this.httpReceiver.close(handler);
    }
}
