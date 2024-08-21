package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.Future;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // Configure Redis client
        RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(vertx, options);
        RedisAPI redisAPI = RedisAPI.api(redis);
        RedisClient redisClient = new RedisClient(redisAPI);

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

                    // Close the Vert.x instance to exit the program
                    vertx.close();
                });
    }

    private static Future<String> setAndGetValue(RedisClient redisClient, String key, String value) {
        return redisClient.set(key, value)
                .compose(v -> {
                    System.out.println("Key set successfully.");
                    return redisClient.get(key);
                });
    }
}
