package com.tyreplus.dealer.application.dto;

import java.util.List;

/**
 * Response DTO for Wallet details.
 * Java 21 Record following DDD principles.
 */
public record WalletResponse(
        int credits,
        List<TransactionResponse> transactions
) {
    public record TransactionResponse(
            String id,
            String title,
            String date,
            int credits,
            String type
    ) {
    }
}

