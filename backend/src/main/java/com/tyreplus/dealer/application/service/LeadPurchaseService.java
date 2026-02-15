package com.tyreplus.dealer.application.service;

import com.tyreplus.dealer.application.dto.LeadDetailsResponse;
import com.tyreplus.dealer.application.exception.InsufficientFundsException;
import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.domain.entity.Transaction;
import com.tyreplus.dealer.domain.entity.TransactionType;
import com.tyreplus.dealer.domain.entity.Wallet;
import com.tyreplus.dealer.domain.repository.LeadRepository;
import com.tyreplus.dealer.domain.repository.TransactionRepository;
import com.tyreplus.dealer.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for handling lead purchase operations.
 * Orchestrates domain entities and enforces business rules.
 */
@Service
public class LeadPurchaseService {

    private final LeadRepository leadRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public LeadPurchaseService(
            LeadRepository leadRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {
        this.leadRepository = leadRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Purchases a lead for a dealer.
     * This operation is atomic and transactional.
     *
     * @param leadId   the ID of the lead to purchase
     * @param dealerId the ID of the dealer purchasing the lead
     * @return LeadDetailsResponse containing the purchased lead details
     * @throws InsufficientFundsException if the dealer's wallet has insufficient
     *                                    balance
     * @throws IllegalArgumentException   if the lead is not available for purchase
     */
    @Transactional
    public LeadDetailsResponse buyLead(UUID leadId, UUID dealerId) {
        // 1. Lock Lead (Prevents race conditions)
        Lead lead = leadRepository.findByIdWithLock(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"));

        // 2. Idempotency Check
        if (dealerId.equals(lead.getPurchasedByDealerId())) {
            return mapToResponse(lead);
        }

        // 3. Availability Check
        if (!lead.isAvailable()) {
            throw new IllegalStateException("Lead already purchased by another dealer.");
        }

        // 4. Lock Wallet
        Wallet wallet = walletRepository.findByDealerIdWithLock(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // 5. Execute Logic (Uses the new Bonus-first deduction)
        Wallet.DeductionBreakdown breakdown = wallet.deduct(lead.getLeadCost());
        lead.markAsBought(dealerId);

        // 6. Persistence
        walletRepository.save(wallet);
        leadRepository.save(lead);

        // 7. Detailed Transaction Recording
        recordDetailedTransaction(wallet, dealerId, lead, breakdown);

        return mapToResponse(lead);
    }

    private void recordDetailedTransaction(Wallet wallet, UUID dealerId, Lead lead,
            Wallet.DeductionBreakdown breakdown) {
        // Create transaction record
        Transaction transaction = new Transaction(
                wallet.getId(),
                dealerId,
                TransactionType.DEBIT,
                lead.getLeadCost(),
                breakdown.purchased(),
                breakdown.bonus(),
                "Lead Purchase: " + lead.getCustomerName() + " - " + lead.getVehicleModel(),
                null); // paymentId is null for internal debits
        transactionRepository.save(transaction);
    }

    private LeadDetailsResponse mapToResponse(Lead lead) {
        // Return response DTO
        return new LeadDetailsResponse(
                lead.getId(),
                lead.getCustomerName(),
                lead.getCustomerPhone(),
                lead.getCustomerEmail(),
                lead.getVehicleModel(),
                lead.getVehicleYear(),
                lead.getStatus(),
                lead.getLeadCost(),
                lead.getPurchasedByDealerId(),
                lead.getCreatedAt(),
                lead.getPurchasedAt());
    }
}
