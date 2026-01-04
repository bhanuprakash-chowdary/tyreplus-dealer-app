package com.tyreplus.dealer.web.controller;

import com.tyreplus.dealer.application.dto.LeadDetailsResponse;
import com.tyreplus.dealer.application.service.LeadDiscoveryService;
import com.tyreplus.dealer.application.service.LeadPurchaseService;
import com.tyreplus.dealer.application.service.LeadStatusUpdateService;
import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.domain.entity.LeadStatus;
import com.tyreplus.dealer.infrastructure.security.DealerDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Lead operations.
 */
@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadPurchaseService purchaseService;
    private final LeadStatusUpdateService statusService;
    private final LeadDiscoveryService discoveryService;

    public LeadController(LeadPurchaseService purchaseService, LeadStatusUpdateService statusService, LeadDiscoveryService discoveryService) {
        this.purchaseService = purchaseService;
        this.statusService = statusService;
        this.discoveryService = discoveryService;
    }

    @GetMapping
    public ResponseEntity<Page<LeadDetailsResponse>> getLeads(
            @AuthenticationPrincipal DealerDetails dealer,
            @RequestParam(defaultValue = "All") String filter,
            @RequestParam(defaultValue = "date_desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discoveryService.getLeads(dealer.getId(), filter, sort, page, size));
    }

    @GetMapping("/{leadId}")
    public ResponseEntity<LeadDetailsResponse> getDetails(@PathVariable UUID leadId) {
        return ResponseEntity.ok(discoveryService.getLeadById(leadId));
    }

    @PostMapping("/{leadId}/buy")
    public ResponseEntity<LeadDetailsResponse> buy(@PathVariable UUID leadId, @AuthenticationPrincipal DealerDetails dealer) {
        return ResponseEntity.ok(purchaseService.buyLead(leadId, dealer.getId()));
    }

    @PostMapping("/{leadId}/skip")
    public ResponseEntity<Void> skip(@PathVariable UUID leadId, @AuthenticationPrincipal DealerDetails dealer) {
        discoveryService.skipLead(leadId, dealer.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{leadId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID leadId, @RequestParam LeadStatus status, @AuthenticationPrincipal DealerDetails dealer) {
        statusService.updateStatus(leadId, dealer.getId(), status);
        return ResponseEntity.ok().build();
    }
}

