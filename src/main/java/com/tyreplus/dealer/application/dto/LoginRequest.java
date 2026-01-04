package com.tyreplus.dealer.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for Login.
 * Java 21 Record with Jakarta Validation.
 */
public record LoginRequest(
        @NotBlank(message = "Mobile number is required")
        String mobile,
        
        @NotBlank(message = "OTP is required")
        String otp
) {
}

