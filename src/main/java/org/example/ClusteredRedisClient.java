package org.example;

import redis.clients.jedis.JedisCluster;

public class ClusteredRedisClient implements RedisClient {
    private final JedisCluster jedisCluster;

    public ClusteredRedisClient(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public void set(String key, String value) {
        try {
            String result = jedisCluster.set(key, value);
            if (!"OK".equals(result)) {
                throw new RuntimeException("Failed to set value in cluster mode");
            }
            System.out.println("final set");
        } catch (Exception e) {
            throw new RuntimeException("Error setting value in Redis", e);
        }
    }

    @Override
    public String get(String key) {
        try {
            String value = jedisCluster.get(key);
            if (value == null) {
                throw new RuntimeException("Key not found in cluster mode");
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Error getting value from Redis", e);
        }
    }
}