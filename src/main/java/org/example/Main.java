package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

public class Main extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(RedisHttpVerticle.class);
    private RedisClient redisClient;

    private static Future<String> setAndGetValue(RedisClient redisClient, String key, String value) {
        return redisClient.set(key, value)
                .compose(v -> {
                    System.out.println("Key set successfully.");
                    return redisClient.get(key);
                });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new Main());
    }

    @Override
    public void start() throws Exception {
        // Configure Redis client
        RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(vertx, options);
        RedisAPI redisAPI = RedisAPI.api(redis);
        redisClient = new RedisClient(redisAPI);
        // Example key-value to set in Redis
        String key = "foo";
        String value = "bar";

        // Set and then get the value
        setAndGetValue(redisClient, key, value)
                .onComplete(result -> {
                    if (result.succeeded()) {
                        System.out.println("Successfully retrieved: " + result.result());
                    } else {
                        System.err.println("Failed: " + result.cause().getMessage());
                    }
                    vertx.close();
                });
        logger.info("after setAndGetValue");
        // Use Future to handle result of getStringBlocking
        getStringBlocking(redisClient, "notexistkey").onComplete(ar -> {
            if (ar.succeeded()) {
                String data = ar.result();
                logger.info("data: " + data);
            } else {
                logger.error("Failed to retrieve data: " + ar.cause().getMessage());
            }
            logger.info("done getStringBlocking");
            vertx.close(); // Ensure Vert.x is closed after all operations are complete
        });
        logger.info("done  outside");
        logger.info("done  outside2");
        logger.info("done  outside 3");
    }

    private Future<String> getStringBlocking(RedisClient redisClient, String key) {
        Promise<String> promise = Promise.promise();
        vertx.executeBlocking(p -> {
            redisClient.get(key).onComplete(res -> {
                if (res.succeeded()) {
                    p.complete(res.result());
                } else {
                    p.fail(res.cause());
                }
            });
        }, false, promise);
        return promise.future();
    }
}
