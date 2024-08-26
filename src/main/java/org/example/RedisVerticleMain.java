package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;

public class RedisVerticleMain extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(RedisVerticleMain.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RedisVerticleMain());
    }

    private RedisClient getRedisClient(boolean clusterMode) {
        RedisClient redisClient = null;

        // Initialize either a clustered or non-clustered Redis client
        if (clusterMode) {
            System.out.println("hereee");
            Set<HostAndPort> nodes = new HashSet<HostAndPort>();
            nodes.add(new HostAndPort("127.0.0.1", 6380));
            JedisCluster jedisCluster = new JedisCluster(nodes);
            redisClient = new ClusteredRedisClient(jedisCluster);
        } else {
            JedisPool jedisPool = new JedisPool("localhost", 6379);
            redisClient = new NonClusteredRedisClient(jedisPool);
        }
        return redisClient;

    }

    @Override
    public void start() {
        RedisClient redisClient = getRedisClient(false);
        redisClient.set("a", "vvv");
        String res = redisClient.get("a");
        System.out.println(res);

        RedisClient redisClientCluster = getRedisClient(true);
        redisClientCluster.set("cccc1", "wefwefwefewfewf");
        String resFromRC = redisClientCluster.get("cccc1");
        System.out.println(resFromRC);
    }
}
