package com.tyreplus.dealer.web.controller;

import com.tyreplus.dealer.application.dto.LeadDetailsResponse;
import com.tyreplus.dealer.application.service.LeadDiscoveryService;
import com.tyreplus.dealer.application.service.LeadPurchaseService;
import com.tyreplus.dealer.application.service.LeadStatusUpdateService;
import com.tyreplus.dealer.domain.entity.LeadStatus;
import com.tyreplus.dealer.infrastructure.security.DealerDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Lead Management", description = "Endpoints for discovering, purchasing, and managing leads")
@SecurityRequirement(name = "Bearer Authentication")
public class LeadController {

    private final LeadPurchaseService purchaseService;
    private final LeadStatusUpdateService statusService;
    private final LeadDiscoveryService discoveryService;

    public LeadController(LeadPurchaseService purchaseService, LeadStatusUpdateService statusService,
            LeadDiscoveryService discoveryService) {
        this.purchaseService = purchaseService;
        this.statusService = statusService;
        this.discoveryService = discoveryService;
    }

    @Operation(summary = "Get Leads", description = "Retrieves a paginated list of leads based on filter (All, Purchased, New) and sort order.", responses = {
            @ApiResponse(responseCode = "200", description = "Leads retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<LeadDetailsResponse>> getLeads(
            @AuthenticationPrincipal DealerDetails dealer,
            @Parameter(description = "Filter type: 'All', 'Purchased', or 'New'") @RequestParam(defaultValue = "All") String filter,
            @Parameter(description = "Sort order: 'date_desc' or 'date_asc'") @RequestParam(defaultValue = "date_desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discoveryService.getLeads(dealer.getId(), filter, sort, page, size));
    }

    @Operation(summary = "Get Lead Details", description = "Retrieves detailed information about a specific lead.", responses = {
            @ApiResponse(responseCode = "200", description = "Lead details retrieved"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    @GetMapping("/{leadId}")
    public ResponseEntity<LeadDetailsResponse> getDetails(@PathVariable UUID leadId) {
        return ResponseEntity.ok(discoveryService.getLeadById(leadId));
    }

    @Operation(summary = "Buy Lead", description = "Purchases a lead using wallet credits.", responses = {
            @ApiResponse(responseCode = "200", description = "Lead purchased successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds or already purchased"),
            @ApiResponse(responseCode = "404", description = "Lead or Wallet not found")
    })
    @PostMapping("/{leadId}/buy")
    public ResponseEntity<LeadDetailsResponse> buy(@PathVariable UUID leadId,
            @AuthenticationPrincipal DealerDetails dealer) {
        return ResponseEntity.ok(purchaseService.buyLead(leadId, dealer.getId()));
    }

    @Operation(summary = "Skip Lead", description = "Marks a lead as skipped so it doesn't appear in the main list.", responses = {
            @ApiResponse(responseCode = "204", description = "Lead skipped successfully")
    })
    @PostMapping("/{leadId}/skip")
    public ResponseEntity<Void> skip(@PathVariable UUID leadId, @AuthenticationPrincipal DealerDetails dealer) {
        discoveryService.skipLead(leadId, dealer.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Lead Status", description = "Updates the status of a purchased lead (e.g., OPEN, IN_PROGRESS, CLOSED).", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/{leadId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID leadId, @RequestParam LeadStatus status,
            @AuthenticationPrincipal DealerDetails dealer) {
        statusService.updateStatus(leadId, dealer.getId(), status);
        return ResponseEntity.ok().build();
    }
}
