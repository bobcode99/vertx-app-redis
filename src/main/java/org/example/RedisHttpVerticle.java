package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

import java.util.List;

public class RedisHttpVerticle extends AbstractVerticle {

    private RedisAPI redisAPI;
    private final Logger logger = LoggerFactory.getLogger(RedisHttpVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        // Configure Redis client
        RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(vertx, options);
        redisAPI = RedisAPI.api(redis);

        // Set up the router
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create()); // Handle the body for POST requests

        // Define the /set endpoint
        router.post("/set").handler(ctx -> {
            JsonObject body = ctx.getBodyAsJson();
            String key = body.getString("key");
            String value = body.getString("value");

            if (key == null || value == null) {
                ctx.response().setStatusCode(400).end("Key and value must be provided");
            } else {
                redisAPI.set(List.of(key, value))
                        .onSuccess(res -> ctx.response().end("Key set successfully"))
                        .onFailure(err -> ctx.response().setStatusCode(500).end("Failed to set key: " + err.getMessage()));
            }
        });

        // Define the /get endpoint
        router.get("/get").handler(ctx -> {
            String key = ctx.queryParams().get("key");

            if (key == null) {
                ctx.response().setStatusCode(400).end("Key must be provided");
            } else {
                redisAPI.get(key)
                        .onSuccess(res -> {
                            if (res != null) {
                                ctx.response().end(res.toString());
                            } else {
                                ctx.response().setStatusCode(404).end("Key not found");
                            }
                        })
                        .onFailure(err -> ctx.response().setStatusCode(500).end("Failed to get key: " + err.getMessage()));
            }
        });

        router.get("/ping").handler(ctx-> {
            logger.info("hii");
            ctx.response().end("pong");
        });

        // Start the HTTP server
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888)
                .onSuccess(server -> {
                    System.out.println("HTTP server started on port 8888");
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RedisHttpVerticle());
    }
}
