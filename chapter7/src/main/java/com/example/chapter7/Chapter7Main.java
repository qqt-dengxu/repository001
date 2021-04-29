package com.example.chapter7;

import com.example.chapter7.config.RedisConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author DX
 * @version 1.0
 * @date 2021/4/28 9:06
 */
public class Chapter7Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisTemplate redisTemplate = context.getBean(RedisTemplate.class);
        redisTemplate.opsForValue().set("key1", "value1");
        redisTemplate.opsForHash().put("hash", "field", "hvalue");
    }
}
