package com.yube.redis;

import com.yube.exceptions.ConfigurationException;
import com.yube.utils.PropertiesHandler;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Properties;

public final class RedissonClientFactory {

    private RedissonClient client;
    private static RedissonClientFactory redissonClientFactory;

    private RedissonClientFactory() throws ConfigurationException {
        Properties properties = PropertiesHandler.getProperties("redis.properties");
        Config config = new Config();
        config.useSingleServer().setAddress(properties.getProperty("REDIS_ADDRESS"));
        client = Redisson.create(config);
    }

    public static RedissonClientFactory getInstance() throws ConfigurationException {
        if (redissonClientFactory == null) {
            redissonClientFactory = new RedissonClientFactory();
        }
        return redissonClientFactory;
    }

    public RedissonClient getRedissonClient(){
        return client;
    }
}
