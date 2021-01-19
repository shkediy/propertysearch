package com.redislabs.demo.data;

import io.redisearch.client.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnection {
    JedisPool pool;
    Client client;

    public RedisConnection(String host, int port, String index)
    {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(10000);
        jedisPoolConfig.setMaxTotal(128);
        pool = new JedisPool(jedisPoolConfig, host, port, 10000);
        client = new Client(index, pool);
    }

    public Client getClient()
    {
        return client;
    }

    public Jedis getResource() {
        return pool.getResource();
    }

}

