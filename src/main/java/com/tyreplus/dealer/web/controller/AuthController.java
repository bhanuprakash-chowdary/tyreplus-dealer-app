package com.tyreplus.dealer.web.controller;

import com.tyreplus.dealer.application.dto.LoginRequest;
import com.tyreplus.dealer.application.dto.LoginResponse;
import com.tyreplus.dealer.application.dto.OtpRequest;
import com.tyreplus.dealer.application.dto.OtpResponse;
import com.tyreplus.dealer.application.dto.RegisterRequest;
import com.tyreplus.dealer.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Generate and send OTP.
     * POST /api/v1/auth/otp
     */
    @PostMapping("/otp")
    public ResponseEntity<OtpResponse> generateOtp(@Valid @RequestBody OtpRequest request) {
        authService.generateOtp(request.mobile());
        return ResponseEntity.ok(new OtpResponse("OTP sent successfully"));
    }

    /**
     * Register a new dealer.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Login endpoint.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

