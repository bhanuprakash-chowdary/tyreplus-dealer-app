package com.tyreplus.dealer.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * JPA Entity for Wallet.
 * Wallet stores ONLY credits (not money).
 * */
@Entity
@Table(
        name = "wallets",
        uniqueConstraints = @UniqueConstraint(columnNames = "dealer_id")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "dealer_id", nullable = false, unique = true)
    private UUID dealerId;

    /**
     * Internal credits balance.
     * NEVER represents real money.
     */
    @Column(name = "credits", nullable = false)
    private int credits;

    /**
     * Optimistic locking (secondary safety).
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

