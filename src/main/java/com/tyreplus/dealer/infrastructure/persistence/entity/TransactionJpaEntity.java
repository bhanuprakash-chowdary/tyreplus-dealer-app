package com.tyreplus.dealer.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for Transaction.
 * Maps domain entity to database table.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "wallet_id", nullable = false, columnDefinition = "UUID")
    private UUID walletId;

    @Column(name = "dealer_id", nullable = false, columnDefinition = "UUID")
    private UUID dealerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private com.tyreplus.dealer.domain.entity.TransactionType type;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "description")
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}

