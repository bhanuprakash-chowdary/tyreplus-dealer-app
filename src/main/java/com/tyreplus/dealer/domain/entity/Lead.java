package com.tyreplus.dealer.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Domain Entity representing a Lead.
 * Pure domain model without JPA annotations.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Lead {
    private UUID id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String vehicleModel;
    private String vehicleYear;
    private LeadStatus status;
    private int leadCost;
    private UUID purchasedByDealerId;
    private LocalDateTime createdAt;
    private LocalDateTime purchasedAt;
    private Set<UUID> skippedByDealerIds = new HashSet<>();


    public Lead() {
        this.status = LeadStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsBought(UUID dealerId) {
        if (dealerId == null) {
            throw new IllegalArgumentException("Dealer ID cannot be null");
        }
        if (this.status == LeadStatus.BOUGHT) {
            throw new IllegalStateException("Lead is already bought");
        }
        if (this.status == LeadStatus.EXPIRED || this.status == LeadStatus.CANCELLED) {
            throw new IllegalStateException("Cannot buy an expired or cancelled lead");
        }
        this.status = LeadStatus.BOUGHT;
        this.purchasedByDealerId = dealerId;
        this.purchasedAt = LocalDateTime.now();
    }

//    public void markAsFollowUp() {
//        if (this.status != LeadStatus.NEW) {
//            throw new IllegalStateException("Only new leads can be marked as follow-up");
//        }
//        this.status = LeadStatus.FOLLOW_UP;
//    }

    public void expire() {
        if (this.status == LeadStatus.BOUGHT) {
            throw new IllegalStateException("Cannot expire a bought lead");
        }
        this.status = LeadStatus.EXPIRED;
    }

    public void addSkip(UUID dealerId) {
        if (this.skippedByDealerIds == null) {
            this.skippedByDealerIds = new HashSet<>();
        }
        this.skippedByDealerIds.add(dealerId);
    }

    public boolean isAvailable() {
        return this.status == LeadStatus.NEW && this.purchasedByDealerId == null;
    }
}

