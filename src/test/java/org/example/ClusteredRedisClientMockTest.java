package org.example;

import com.github.fppt.jedismock.RedisServer;
import com.github.fppt.jedismock.server.ServiceOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@Testcontainers
public class ClusteredRedisClientMockTest {

    private static RedisClient redisClient;

    @BeforeAll
    public static void setUp() throws IOException {
//This binds mock redis server to a random port
        RedisServer server = RedisServer
                .newRedisServer()
                .setOptions(ServiceOptions.defaultOptions().withClusterModeEnabled())
                .start();

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort(server.getHost(), server.getBindPort()));
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);

        redisClient = new ClusteredRedisClient(jedisCluster);
    }


    @Test
    public void testSetAndGet() {
        String key = "testKey";
        String value = "testValue";

        redisClient.set(key, value);
        String retrievedValue = redisClient.get(key);

        Assertions.assertEquals(value, retrievedValue);
    }

    @Test
    public void testGetNonExistentKey() {
        assertThrows(RuntimeException.class, () -> redisClient.get("nonExistentKey"));
    }
}
