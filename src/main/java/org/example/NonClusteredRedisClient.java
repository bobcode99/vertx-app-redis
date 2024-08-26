package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class NonClusteredRedisClient implements RedisClient {
    private final JedisPool jedisPool;

    public NonClusteredRedisClient(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(key, value);
            if (!"OK".equals(result)) {
                throw new RuntimeException("Failed to set value in non-cluster mode");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting value in Redis", e);
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                throw new RuntimeException("Key not found in non-cluster mode");
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Error getting value from Redis", e);
        }
    }
}
