package org.example;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.RedisAPI;

import java.util.List;

public class RedisClient {
    private final RedisAPI redisAPI;

    public RedisClient(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
    }

    public Future<Void> set(String key, String value) {
        Promise<Void> promise = Promise.promise();
        redisAPI.set(List.of(key, value))
                .onSuccess(response -> promise.complete())
                .onFailure(promise::fail);
        return promise.future();
    }

    public Future<String> get(String key) {
        Promise<String> promise = Promise.promise();
        redisAPI.get(key)
                .onSuccess(response -> promise.complete(response.toString()))
                .onFailure(promise::fail);
        return promise.future();
    }
}
