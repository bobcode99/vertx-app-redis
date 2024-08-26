package org.example;

public interface RedisClient {
    void set(String key, String value);
    String get(String key);
}
