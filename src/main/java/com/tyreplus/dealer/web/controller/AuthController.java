package com.tyreplus.dealer.web.controller;

import com.tyreplus.dealer.application.dto.*;
import com.tyreplus.dealer.application.service.AuthService;
import com.tyreplus.dealer.infrastructure.security.DealerDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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
        LoginResponse result = authService.register(request);

        ResponseCookie refreshCookie = buildRefreshCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result);
    }

    /**
     * Login endpoint.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = authService.login(request);

        ResponseCookie refreshCookie = buildRefreshCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(value = "refresh_token", required = false) String cookieToken,
            @RequestHeader(value = "X-Refresh-Token", required = false) String headerToken) {

        String refreshToken = headerToken != null ? headerToken : cookieToken;

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token missing");
        }

        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh_token", required = false) String cookieToken,
            @RequestHeader(value = "X-Refresh-Token", required = false) String headerToken) {

        String refreshToken = headerToken != null ? headerToken : cookieToken;

        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", "")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    /**
     * Set password (OTP-authenticated user only).
     * Requires JWT in Authorization header.
     */
    @PostMapping("/password")
    public ResponseEntity<Void> setPassword(
            @AuthenticationPrincipal DealerDetails dealerDetails,
            @Valid @RequestBody SetPasswordRequest request
    ) {
        authService.setPassword(dealerDetails.getId(), request.password());
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)              // set false only for localhost HTTP
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(14))
                .build();
    }
}

