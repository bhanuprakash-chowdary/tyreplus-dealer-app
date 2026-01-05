package com.tyreplus.dealer.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain Entity representing a Transaction.
 * Records every credit/debit operation on a wallet.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Transaction {
    private UUID id;
    private UUID walletId;
    private UUID dealerId;
    private TransactionType type;
    private int credits;
    private String description;
    private LocalDateTime timestamp;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(UUID walletId, UUID dealerId, TransactionType type, int credits, String description) {
        this();
        this.walletId = walletId;
        this.dealerId = dealerId;
        this.type = type;
        this.credits = credits;
        this.description = description;
    }
}

