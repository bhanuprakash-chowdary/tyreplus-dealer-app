package com.tyreplus.dealer.web.controller;

import com.tyreplus.dealer.application.dto.*;
import com.tyreplus.dealer.application.service.WalletService;
import com.tyreplus.dealer.infrastructure.security.DealerDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Wallet operations.
 */
@RestController
@RequestMapping("/api/v1/dealer")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Get wallet details.
     * GET /api/v1/dealer/wallet
     */
    @GetMapping("/wallet")
    public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal DealerDetails dealerDetails) {
        WalletResponse response = walletService.getWalletDetails(dealerDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get available packages.
     * GET /api/v1/dealer/packages
     */
    @GetMapping("/packages")
    public ResponseEntity<List<PackageResponse>> getPackages() {
        List<PackageResponse> packages = walletService.getPackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * Initiate Payment
     * POST /api/v1/dealer/recharge/initiate
     */
    @PostMapping("/recharge/initiate")
    public ResponseEntity<PaymentOrderResponse> initiate(@AuthenticationPrincipal DealerDetails dealer,
                                                         @RequestBody int amount) {
        return ResponseEntity.ok(walletService.initiateRecharge(dealer.getId(), amount));
    }

    /**
     * verify payment.
     * POST /api/v1/dealer/recharge/verify
     */
    @PostMapping("/recharge/verify")
    public ResponseEntity<WalletResponse> verify(@AuthenticationPrincipal DealerDetails dealer,
                                                 @RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(walletService.completeRecharge(dealer.getId(), request));
    }

    /**
     * Recharge wallet.
     * POST /api/v1/dealer/wallet/recharge
     */
    @PostMapping("/wallet/recharge")
    public ResponseEntity<WalletResponse> recharge(
            @AuthenticationPrincipal DealerDetails dealerDetails,
            @Valid @RequestBody RechargeRequest request) {
        WalletResponse response = walletService.recharge(dealerDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

}

