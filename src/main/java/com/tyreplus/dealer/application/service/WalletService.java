package com.tyreplus.dealer.application.service;

import com.tyreplus.dealer.application.dto.*;
import com.tyreplus.dealer.domain.entity.Transaction;
import com.tyreplus.dealer.domain.entity.TransactionType;
import com.tyreplus.dealer.domain.entity.Wallet;
import com.tyreplus.dealer.domain.repository.TransactionRepository;
import com.tyreplus.dealer.domain.repository.WalletRepository;
import com.tyreplus.dealer.infrastructure.payment.RazorpayAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for handling wallet operations.
 */
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RazorpayAdapter razorpayAdapter;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository,RazorpayAdapter razorpayAdapter) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.razorpayAdapter = razorpayAdapter;
    }

    public WalletResponse getWalletDetails(UUID dealerId) {
        Wallet wallet = walletRepository.findByDealerId(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for dealer: " + dealerId));

        List<Transaction> transactions = transactionRepository.findByDealerId(dealerId);

        List<WalletResponse.TransactionResponse> transactionResponses = transactions.stream()
                .map(tx -> new WalletResponse.TransactionResponse(
                        tx.getId().toString(),
                        tx.getDescription() != null ? tx.getDescription() : 
                                (tx.getType() == TransactionType.CREDIT ? "Added Money" : "Deducted Money"),
                        tx.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME),
                        tx.getAmount(),
                        tx.getType().name().toLowerCase()
                ))
                .collect(Collectors.toList());

        return new WalletResponse(wallet.getBalance(), transactionResponses);
    }

    public List<PackageResponse> getPackages() {
        // Hardcoded packages as requested
        List<PackageResponse> packages = new ArrayList<>();
        packages.add(new PackageResponse("pkg_1", "Starter", 500, 10, false));
        packages.add(new PackageResponse("pkg_2", "Growth", 2000, 50, true));
        packages.add(new PackageResponse("pkg_3", "Premium", 5000, 150, false));
        return packages;
    }

    /**
     * Step 1: Create a secure order with the Gateway
     */
    @Transactional
    public PaymentOrderResponse initiateRecharge(UUID dealerId, int amountInInr) {
        try {
            // Razorpay expects amount in Paise
            int amountInPaise = amountInInr * 100;
            String gatewayOrderId = razorpayAdapter.createGatewayOrder(amountInPaise);

            return new PaymentOrderResponse(gatewayOrderId, amountInInr, "INR");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate payment with gateway", e);
        }
    }

    /**
     * Step 2: Verify the digital signature and credit the wallet
     */
    @Transactional
    public WalletResponse completeRecharge(UUID dealerId, PaymentVerificationRequest request) {
        // 1. Digital Signature Verification (Security)
        boolean isVerified = razorpayAdapter.verifySignature(
                request.gatewayOrderId(),
                request.gatewayPaymentId(),
                request.gatewaySignature()
        );

        if (!isVerified) {
            throw new SecurityException("Payment verification failed. Invalid signature.");
        }

        // 2. Load Wallet with Lock
        Wallet wallet = walletRepository.findByDealerIdWithLock(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // 3. Update Balance
        wallet.credit(request.amount());
        walletRepository.save(wallet);

        // 4. Record Transaction
        Transaction transaction = new Transaction(
                wallet.getId(),
                dealerId,
                TransactionType.CREDIT,
                request.amount(),
                "Wallet Recharge: Verified via Gateway"
        );
        transactionRepository.save(transaction);

        return getWalletDetails(dealerId);
    }

    @Transactional
    public WalletResponse recharge(UUID dealerId, RechargeRequest request) {
        Wallet wallet = walletRepository.findByDealerIdWithLock(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for dealer: " + dealerId));

        int amount = request.amount();
        
        // If packageId is provided, use package credits, otherwise use direct amount
        if (request.packageId() != null) {
            PackageResponse pkg = getPackages().stream()
                    .filter(p -> p.id().equals(request.packageId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Package not found: " + request.packageId()));
            amount = pkg.credits();
        }

        // Credit the wallet
        wallet.credit(amount);
        walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction(
                wallet.getId(),
                dealerId,
                TransactionType.CREDIT,
                amount,
                request.packageId() != null ? "Package Purchase" : "Wallet Recharge"
        );
        transactionRepository.save(transaction);

        return getWalletDetails(dealerId);
    }
}

