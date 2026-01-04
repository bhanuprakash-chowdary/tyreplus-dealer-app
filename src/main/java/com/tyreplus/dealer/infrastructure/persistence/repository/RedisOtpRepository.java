package com.tyreplus.dealer.infrastructure.persistence.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class RedisOtpRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String OTP_PREFIX = "OTP:";
    private static final String ATTEMPTS_PREFIX = "ATTEMPTS:";
    private static final int MAX_ATTEMPTS = 3;

    public RedisOtpRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(String mobile, String code) {
        // Save OTP with 5-minute expiry
        redisTemplate.opsForValue().set(OTP_PREFIX + mobile, code, Duration.ofMinutes(5));
        // Reset attempts whenever a new OTP is requested
        redisTemplate.delete(ATTEMPTS_PREFIX + mobile);
    }

    public Optional<String> getOtp(String mobile) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(OTP_PREFIX + mobile));
    }

    public void deleteOtp(String mobile) {
        redisTemplate.delete(OTP_PREFIX + mobile);
        redisTemplate.delete(ATTEMPTS_PREFIX + mobile);
    }

    public int incrementAndGetAttempts(String mobile) {
        String key = ATTEMPTS_PREFIX + mobile;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(10)); // Lockout window
        }
        return attempts != null ? attempts.intValue() : 0;
    }

    public boolean isBlocked(String mobile) {
        String val = redisTemplate.opsForValue().get(ATTEMPTS_PREFIX + mobile);
        return val != null && Integer.parseInt(val) >= MAX_ATTEMPTS;
    }
}
