package com.tyreplus.dealer.infrastructure.persistence.entity;

import com.tyreplus.dealer.domain.entity.LeadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JPA Entity for Lead.
 * Maps domain entity to database table.
 */
@Entity
@Table(name = "leads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @Column(name = "vehicle_year")
    private String vehicleYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeadStatus status;

    @Column(name = "lead_cost", nullable = false)
    private int leadCost;

    @Column(name = "purchased_by_dealer_id", columnDefinition = "UUID")
    private UUID purchasedByDealerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @ElementCollection
    @CollectionTable(name = "lead_skips", joinColumns = @JoinColumn(name = "lead_id"))
    @Column(name = "dealer_id")
    private Set<UUID> skippedByDealerIds = new HashSet<>();
}

