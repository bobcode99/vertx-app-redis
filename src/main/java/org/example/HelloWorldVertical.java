package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class HelloWorldVertical extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    req.response()
                            .putHeader("content-type", "text/plain")
                            .end("Hello, World!");
                })
                .listen(8888, http -> {
                    if (http.succeeded()) {
                        System.out.println("HTTP server started on port 8888");
                    } else {
                        System.out.println("HTTP server failed to start: " + http.cause());
                    }
                });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HelloWorldVertical());
    }}

