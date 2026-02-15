package com.tyreplus.dealer.application.service;

import com.tyreplus.dealer.application.dto.LeadDetailsResponse;
import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.domain.entity.LeadStatus;
import com.tyreplus.dealer.domain.repository.LeadRepository;
import com.tyreplus.dealer.infrastructure.persistence.entity.LeadJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadDiscoveryService {

    private final LeadRepository leadRepository;

    public Page<LeadDetailsResponse> getLeads(UUID dealerId, String filter, String sort, int page, int size) {
        // 1. Convert String filter to LeadStatus enum (null if "All")
        LeadStatus status = null;
        if (!"All".equalsIgnoreCase(filter)) {
            try {
                status = LeadStatus.valueOf(filter.toUpperCase());
            } catch (IllegalArgumentException e) {
                status = null; // Fallback to all if invalid status sent
            }
        }

        // 2. Setup Pagination (Sort is handled inside the @Query using the String param)
        Pageable pageable = PageRequest.of(page, size);

        // 3. Map the Page of Domain entities to Page of Response DTOs
        return leadRepository.findLeadsWithFilters(status, dealerId, sort, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void skipLead(UUID leadId, UUID dealerId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"));
        lead.addSkip(dealerId);
        leadRepository.save(lead);
    }

    public LeadDetailsResponse getLeadById(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"));
        return mapToResponse(lead);
    }

    private LeadDetailsResponse mapToResponse(Lead lead) {
        return new LeadDetailsResponse(
                lead.getId(), lead.getCustomerName(), lead.getCustomerPhone(),
                lead.getCustomerEmail(), lead.getVehicleModel(), lead.getVehicleYear(),
                lead.getStatus(), lead.getLeadCost(), lead.getPurchasedByDealerId(),
                lead.getCreatedAt(), lead.getPurchasedAt()
        );
    }
}