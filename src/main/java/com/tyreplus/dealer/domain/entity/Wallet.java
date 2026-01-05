package com.tyreplus.dealer.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Domain Entity representing a Wallet.
 * Pure domain model without JPA annotations.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Wallet {
    private UUID id;
    private UUID dealerId;
    private int credits;
    private Long version;

    public Wallet() {
        this.credits = 0;
    }

    public Wallet(UUID dealerId, int initialBalance) {
        this();
        this.dealerId = dealerId;
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.credits = initialBalance;
    }

    /**
     * Business logic method to check if the wallet can afford a cost.
     *
     * @param cost the cost to check
     * @return true if the wallet has sufficient balance
     */
    public boolean canAfford(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        return this.credits >= cost;
    }

    /**
     * Deducts an amount from the wallet balance.
     *
     * @param credits the amount to deduct
     * @throws IllegalArgumentException if amount is negative
     * @throws IllegalStateException if insufficient balance
     */
    public void deduct(int credits) {
        if (credits < 0) {
            throw new IllegalArgumentException("Deduction amount cannot be negative");
        }
        if (!canAfford(credits)) {
            throw new IllegalStateException(
                    String.format("Insufficient balance. Current: %d, Required: %d", credits, credits)
            );
        }
        this.credits -= credits;
    }

    /**
     * Adds an amount to the wallet balance.
     *
     * @param credits the amount to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void credit(int credits) {
        if (credits < 0) {
            throw new IllegalArgumentException("Credit amount cannot be negative");
        }
        this.credits += credits;
    }
}

