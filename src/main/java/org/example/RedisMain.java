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

public class RedisMain extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(Main.class);
    private RedisClientVertx redisClientVertx;

    @Override
    public void start() {
        // Initialize Vert.x and Redis Client
        Vertx vertx = Vertx.vertx();
        RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(vertx, options);
        RedisAPI redisAPI = RedisAPI.api(redis);
        redisClientVertx = new RedisClientVertx(redisAPI);

        // Execute the ordered steps
        logger.info("Start");

        // Step 2: Get redis data by using key "key1"
        getByKeyInMainClass("key1")
                .compose(key1Data -> {
                    if (key1Data == null || key1Data.isEmpty()) {
                        logger.error("Key1 data is empty or not found");
                        return Future.failedFuture(new Exception("Key1 data is empty or not found"));
                    } else {
                        logger.info("Key1 data: " + key1Data);
                        // Step 4: Write data to redis with key "key3" and data "data3"
                        return redisClientVertx.set("key3", "data3");
                    }
                })
                .compose(v -> {
                    // Step 5: Get data with key "key3" and print the result
                    return getByKeyInMainClass("key3");
                })
                .onSuccess(key3Data -> {
                    logger.info("Key3 data: " + key3Data);
                    logger.info("Finish");

                    // Close the Vert.x instance to exit the program
                    vertx.close();
                })
                .onFailure(err -> {
                    logger.error("An error occurred: " + err.getMessage());
                    vertx.close();
                });
    }

    // Custom method to get data by key in the Main class
    private Future<String> getByKeyInMainClass(String key) {
        Promise<String> promise = Promise.promise();
        redisClientVertx.get(key)
                .onSuccess(promise::complete)
                .onFailure(promise::fail);
        return promise.future();
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RedisMain());
    }
}
