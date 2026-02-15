package com.tyreplus.dealer.application.dto;

import com.tyreplus.dealer.domain.entity.LeadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Lead details.
 * Java 21 Record following DDD principles.
 */
public record LeadDetailsResponse(
        UUID id,
        String customerName,
        String customerPhone,
        String customerEmail,
        String vehicleModel,
        String vehicleYear,
        LeadStatus status,
        int leadCost,
        UUID purchasedByDealerId,
        LocalDateTime createdAt,
        LocalDateTime purchasedAt
) {
}

