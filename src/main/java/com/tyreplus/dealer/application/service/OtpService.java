package com.tyreplus.dealer.application.service;

import com.tyreplus.dealer.application.exception.InvalidOtpException;
import com.tyreplus.dealer.domain.entity.Otp;
import com.tyreplus.dealer.domain.repository.OtpRepository;
import com.tyreplus.dealer.infrastructure.persistence.repository.LeadJpaRepository;
import com.tyreplus.dealer.infrastructure.persistence.repository.RedisOtpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing OTP generation and validation.
 */
@Service
public class OtpService {

    private final RedisOtpRepository redisOtpRepository;
    private final SmsService smsService;
    private final SecureRandom random = new SecureRandom();

    public OtpService(RedisOtpRepository redisOtpRepository, SmsService smsService) {
        this.redisOtpRepository = redisOtpRepository;
        this.smsService = smsService;
    }

    public void generateOtp(String mobile) {
        String code = String.valueOf(100 + random.nextInt(900));
        redisOtpRepository.saveOtp(mobile, code);

        smsService.sendSms(mobile, "Your TyrePlus code is: " + code);
    }

    public void validateOtp(String mobile, String code) {
        // 1. Check if blocked
        if (redisOtpRepository.isBlocked(mobile)) {
            throw new InvalidOtpException("Too many failed attempts. Try again in 10 minutes.");
        }

        // 2. Fetch OTP
        String savedCode = redisOtpRepository.getOtp(mobile)
                .orElseThrow(() -> new InvalidOtpException("OTP expired or not generated"));

        // 3. Compare
        if (!savedCode.equals(code)) {
            int attempts = redisOtpRepository.incrementAndGetAttempts(mobile);
            int remaining = Math.max(0, 3 - attempts);
            throw new InvalidOtpException("Invalid OTP. Attempts remaining: " + remaining);
        }

        // 4. Success: Clean up
        redisOtpRepository.deleteOtp(mobile);
    }
}

