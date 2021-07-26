package com.example.chapter7.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author DX
 * @version 1.0
 * @date 2021/6/16 17:24
 */
@Component
public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] bytes) {
        // 消息体
        String body = new String(message.getBody());
        // 渠道名称
        String topic = new String(bytes);
        System.out.println(body);
        System.out.println(topic);
    }
}
