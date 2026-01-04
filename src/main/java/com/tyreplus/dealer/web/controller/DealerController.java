package com.tyreplus.dealer.web.controller;

import com.tyreplus.dealer.application.dto.DashboardResponse;
import com.tyreplus.dealer.application.dto.DealerProfileResponse;
import com.tyreplus.dealer.application.dto.UpdateDealerProfileRequest;
import com.tyreplus.dealer.application.service.DashboardService;
import com.tyreplus.dealer.application.service.DealerProfileService;
import com.tyreplus.dealer.infrastructure.security.DealerDetails; // Our new class
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Dealer operations.
 */
@RestController
@RequestMapping("/api/v1/dealer")
public class DealerController {

    private final DealerProfileService dealerProfileService;
    private final DashboardService dashboardService;

    public DealerController(DealerProfileService dealerProfileService, DashboardService dashboardService) {
        this.dealerProfileService = dealerProfileService;
        this.dashboardService = dashboardService;
    }

    /**
     * Get dealer profile.
     * GET /api/v1/dealer/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<DealerProfileResponse> getProfile(
            @AuthenticationPrincipal DealerDetails dealerDetails) {
        // No manual parsing needed! dealerDetails.getId() is already a UUID.
        return ResponseEntity.ok(dealerProfileService.getProfile(dealerDetails.getId()));
    }

    /**
     * Update dealer profile.
     * PUT /api/v1/dealer/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<DealerProfileResponse> updateProfile(
            @AuthenticationPrincipal DealerDetails dealerDetails,
            @Valid @RequestBody UpdateDealerProfileRequest request) {
        return ResponseEntity.ok(dealerProfileService.updateProfile(dealerDetails.getId(), request));
    }

    /**
     * Get dashboard data.
     * GET /api/v1/dealer/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal DealerDetails dealerDetails) {
        return ResponseEntity.ok(dashboardService.getDashboard(dealerDetails.getId()));
    }
}