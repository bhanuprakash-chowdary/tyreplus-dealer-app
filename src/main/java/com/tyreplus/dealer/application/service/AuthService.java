package com.tyreplus.dealer.application.service;

import com.tyreplus.dealer.application.dto.LoginRequest;
import com.tyreplus.dealer.application.dto.LoginResponse;
import com.tyreplus.dealer.application.dto.RegisterRequest;
import com.tyreplus.dealer.application.exception.InvalidOtpException;
import com.tyreplus.dealer.application.exception.UserNotFoundException;
import com.tyreplus.dealer.domain.entity.Dealer;
import com.tyreplus.dealer.domain.entity.Wallet;
import com.tyreplus.dealer.domain.repository.DealerRepository;
import com.tyreplus.dealer.domain.repository.WalletRepository;
import com.tyreplus.dealer.domain.valueobject.Address;
import com.tyreplus.dealer.domain.valueobject.BusinessHours;
import com.tyreplus.dealer.domain.valueobject.ContactDetails;
import com.tyreplus.dealer.infrastructure.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Application service for handling authentication operations.
 */
@Service
public class AuthService {

    private final DealerRepository dealerRepository;
    private final WalletRepository walletRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    // Constructor updated to include WalletRepository
    public AuthService(DealerRepository dealerRepository,
                       WalletRepository walletRepository,
                       OtpService otpService,
                       JwtUtil jwtUtil) {
        this.dealerRepository = dealerRepository;
        this.walletRepository = walletRepository;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }

    public void generateOtp(String mobile) {
        otpService.generateOtp(mobile);
    }

    /**
     * Login logic: Cleaner exception flow.
     */
    public LoginResponse login(LoginRequest request) {
        otpService.validateOtp(request.mobile(), request.otp());

        Dealer dealer = dealerRepository.findByMobile(request.mobile())
                .orElseThrow(() -> new UserNotFoundException(
                        "Dealer not found with mobile: " + request.mobile() + ". Please register first."));

        String token = jwtUtil.generateToken(
                request.mobile(),
                dealer.getId().toString(),
                "dealer"
        );

        return new LoginResponse(
                token,
                new LoginResponse.UserInfo(
                        dealer.getId().toString(),
                        dealer.getBusinessName(),
                        "dealer",
                        null
                )
        );
    }

    /**
     * Register logic: Now initializes a Wallet for the new dealer.
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 1. Check for duplicates
        if (dealerRepository.existsByMobile(request.mobile())) {
            throw new IllegalArgumentException("Mobile number already registered");
        }
        if (dealerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 2. Validate OTP (Bubbles up automatically if it fails)
        otpService.validateOtp(request.mobile(), request.otp());

        // 3. Map Domain Objects
        ContactDetails contactDetails = new ContactDetails(
                request.email(),
                request.mobile(),
                request.whatsapp()
        );

        Address address = new Address(
                request.address().street(),
                request.address().city(),
                request.address().state(),
                request.address().pincode(),
                "India"
        );

        BusinessHours businessHours = new BusinessHours(
                parseTime(request.businessHours().openTime()),
                parseTime(request.businessHours().closeTime()),
                request.businessHours().openDays().contains("Sat")
        );

        // 4. Create and Save Dealer
        Dealer dealer = Dealer.builder()
                .businessName(request.businessName())
                .ownerName(request.ownerName())
                .isVerified(false)
                .contactDetails(contactDetails)
                .address(address)
                .businessHours(businessHours)
                .build();

        Dealer savedDealer = dealerRepository.save(dealer);

        // 5. CRITICAL: Initialize Wallet (Welcome balance of 0 or change to 100 for promo)
        Wallet wallet = new Wallet(savedDealer.getId(), 0);
        walletRepository.save(wallet);

        // 6. Token generation
        String token = jwtUtil.generateToken(
                request.mobile(),
                savedDealer.getId().toString(),
                "dealer"
        );

        return new LoginResponse(
                token,
                new LoginResponse.UserInfo(
                        savedDealer.getId().toString(),
                        savedDealer.getBusinessName(),
                        "dealer",
                        null
                )
        );
    }

    private LocalTime parseTime(String timeStr) {
        timeStr = timeStr.trim().toUpperCase();
        if (timeStr.contains("AM") || timeStr.contains("PM")) {
            // Use 'hh' for two-digit hours like 09:00
            // Use 'h' for single-digit hours like 9:00
            // To handle both, use a case-insensitive formatter with Locale.ENGLISH
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("[hh:mm a][h:mm a]") // Handles both 09:00 AM and 9:00 AM
                    .toFormatter(java.util.Locale.ENGLISH);

            return LocalTime.parse(timeStr, formatter);
        } else {
            return LocalTime.parse(timeStr);
        }
    }
}