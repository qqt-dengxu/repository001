package com.example.chapter7.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author DX
 * @version 1.0
 * @date 2021/4/27 17:01
 */
@Configuration
public class RedisConfig {

    private RedisConnectionFactory connectionFactory = null;

    @Bean(name = "RedisConnectionFactory")
    public RedisConnectionFactory initRedisConnectionFactory() {
        if (this.connectionFactory != null) {
            return this.connectionFactory;
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲数
        jedisPoolConfig.setMaxIdle(30);
        // 最大连接数
        jedisPoolConfig.setMaxTotal(30);
        // 最大等待毫秒数
        jedisPoolConfig.setMaxWaitMillis(2000);
        // 创建Jedis连接工厂
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        // 获取单机的Redis配置
        RedisStandaloneConfiguration standaloneConfiguration = jedisConnectionFactory.getStandaloneConfiguration();
        standaloneConfiguration.setHostName("127.0.0.1");
        standaloneConfiguration.setPort(6379);
        //standaloneConfiguration.setPassword("");
        this.connectionFactory = jedisConnectionFactory;

        return connectionFactory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> initRedisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        redisTemplate.setConnectionFactory(initRedisConnectionFactory());
        return redisTemplate;
    }
}
