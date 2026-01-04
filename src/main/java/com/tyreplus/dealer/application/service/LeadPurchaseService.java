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
     * @throws InsufficientFundsException if the dealer's wallet has insufficient balance
     * @throws IllegalArgumentException   if the lead is not available for purchase
     */
    @Transactional
    public LeadDetailsResponse buyLead(UUID leadId, UUID dealerId) {
        // 1. Fetch Lead with PESSIMISTIC_WRITE lock
        // This stops any other dealer from even checking availability until this transaction finishes
        // 1. Lock the lead immediately
        Lead lead = leadRepository.findByIdWithLock(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"));

        // 2. IDEMPOTENCY CHECK: Did THIS dealer already buy this lead?
        if (dealerId.equals(lead.getPurchasedByDealerId())) {
            return mapToResponse(lead); // Just return the details, don't charge again!
        }

        // 3. AVAILABILITY CHECK: Did SOMEONE ELSE buy it?
        if (!lead.isAvailable()) {
            throw new IllegalStateException("This lead has already been purchased by another dealer.");
        }

        // 4. Fetch Wallet with PESSIMISTIC_WRITE lock
        Wallet wallet = walletRepository.findByDealerIdWithLock(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // 5. Domain Logic - Keep the 'how' inside the entities
        if (!wallet.canAfford(lead.getLeadCost())) {
            throw new InsufficientFundsException("Balance too low");
        }

        // 6. State Changes
        wallet.deduct(lead.getLeadCost());
        lead.markAsBought(dealerId);

        // 7. Persistence
        walletRepository.save(wallet);
        leadRepository.save(lead);

        // 8. Transaction Logging
        recordTransaction(wallet, dealerId, lead);

        return mapToResponse(lead);
    }

    private void recordTransaction(Wallet wallet, UUID dealerId, Lead lead) {
        // Create transaction record
        Transaction transaction = new Transaction(
                wallet.getId(),
                dealerId,
                TransactionType.DEBIT,
                lead.getLeadCost(),
                "Lead Purchase: " + lead.getCustomerName() + " - " + lead.getVehicleModel()
        );
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
                lead.getPurchasedAt()
        );
    }
}

