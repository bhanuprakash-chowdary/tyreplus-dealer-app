package com.tyreplus.dealer.infrastructure.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redis;
    private final long REFRESH_EXPIRY=3;

    public RefreshTokenService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String create(UUID userId) {
        String token = UUID.randomUUID().toString();
        redis.opsForValue().set(
                "refresh:" + token,
                userId.toString(),
                Duration.ofDays(REFRESH_EXPIRY)
        );
        return token;
    }

    public UUID validate(String token) {
        String value = redis.opsForValue().get("refresh:" + token);
        if (value == null) {
            throw new RuntimeException("Invalid refresh token");
        }
        return UUID.fromString(value);
    }

    public void revoke(String token) {
        redis.delete("refresh:" + token);
    }
}

