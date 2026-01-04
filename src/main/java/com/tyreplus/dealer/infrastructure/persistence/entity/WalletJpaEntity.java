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
 * Maps domain entity to database table.
 */
@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(columnNames = "dealer_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "dealer_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID dealerId;

    @Column(name = "balance", nullable = false)
    private int balance;

    @Version
    @Column(name = "version")
    private Long version; // For optimistic locking
}

