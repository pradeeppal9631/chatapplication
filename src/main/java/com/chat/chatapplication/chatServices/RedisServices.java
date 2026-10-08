package com.chat.chatapplication.chatServices;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServices {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisServices(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setUserServer(String username, String serverId) {

        String key = "chat:user:" + username;

        redisTemplate.opsForValue().set(key, serverId);

        System.out.println(
                "Redis: " + username + " -> " + serverId
        );
    }

    public String getUserServer(String username) {

        String key = "chat:user:" + username;

        return redisTemplate.opsForValue().get(key);
    }

    public void removeUser(String username) {

        String key = "chat:user:" + username;

        redisTemplate.delete(key);

        System.out.println(
                "Redis: removed " + username
        );
    }

    public void blacklistToken(
            String token,
            long expirationInSeconds) {

        String key = "chat:blacklist:" + token;

        redisTemplate.opsForValue().set(
                key,
                "blacklisted",
                expirationInSeconds,
                TimeUnit.SECONDS
        );

        System.out.println(
                "Redis: JWT blacklisted"
        );
    }


    public boolean isTokenBlacklisted(String token) {

        String key = "chat:blacklist:" + token;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }
}