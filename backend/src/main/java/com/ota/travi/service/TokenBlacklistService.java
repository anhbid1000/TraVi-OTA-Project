package com.ota.travi.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // Đưa token vào danh sách đen, tự động biến mất khi thời gian JWT hết hạn
    public void addToBlacklist(String token, long expirationDurationMs) {
        redisTemplate.opsForValue().set(token, "blacklisted", expirationDurationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
