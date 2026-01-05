package com.tyreplus.dealer.application.service;

import com.tyreplus.dealer.application.dto.*;
import com.tyreplus.dealer.domain.entity.RechargePackage;
import com.tyreplus.dealer.domain.entity.Transaction;
import com.tyreplus.dealer.domain.entity.TransactionType;
import com.tyreplus.dealer.domain.entity.Wallet;
import com.tyreplus.dealer.domain.repository.RechargePackageRepository;
import com.tyreplus.dealer.domain.repository.TransactionRepository;
import com.tyreplus.dealer.domain.repository.WalletRepository;
import com.tyreplus.dealer.infrastructure.payment.RazorpayAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Application service for handling wallet operations.
 */
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RazorpayAdapter razorpayAdapter;
    private final RechargePackageRepository packageRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository,RazorpayAdapter razorpayAdapter, RechargePackageRepository packageRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.razorpayAdapter = razorpayAdapter;
        this.packageRepository = packageRepository;
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
                        tx.getCredits(),
                        tx.getType().name().toLowerCase()
                )).toList();

        return new WalletResponse(wallet.getCredits(), transactionResponses);
    }

    @Transactional(readOnly = true)
    public List<PackageResponse> getPackages() {
        return packageRepository.findActivePackages().stream().
                map(rechargePackage ->
                        new PackageResponse(rechargePackage.getId().toString(),
                                rechargePackage.getName(),
                                rechargePackage.getPriceInInr(),
                                rechargePackage.getCredits(),
                                rechargePackage.isPopular()))
                .toList();
    }

    /**
     * Step 1: Create a secure order with the Gateway
     */
    @Transactional
    public PaymentOrderResponse initiateRecharge(UUID dealerId, UUID packageId) {
        try {

            RechargePackage pkg = packageRepository.findById(packageId).
                    orElseThrow(() -> new IllegalArgumentException("Package not found"));
            // Razorpay expects amount in Paise
            String gatewayOrderId = razorpayAdapter.createGatewayOrder(pkg.getPriceInInr() * 100);

            return new PaymentOrderResponse(gatewayOrderId, pkg.getCredits(), "CREDITS",pkg.getName());
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

        RechargePackage pkg = packageRepository.findById(UUID.fromString(request.packageId())).
                orElseThrow(() -> new IllegalArgumentException("Invalid Package"));

        // 3. Update Balance
        wallet.credit(pkg.getCredits());
        walletRepository.save(wallet);

        // 4. Record Transaction
        Transaction transaction = new Transaction(
                wallet.getId(),
                dealerId,
                TransactionType.CREDIT,
                pkg.getCredits(),
                "Package Purchase: " + pkg.getName()
        );
        transactionRepository.save(transaction);

        return getWalletDetails(dealerId);
    }

    /**
     * TEST ONLY – directly credits wallet.
     * Useful for QA / local / admin testing.
     */
    @Transactional
    public WalletResponse testRecharge(UUID dealerId, RechargeRequest request) {
        Wallet wallet = walletRepository.findByDealerIdWithLock(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for dealer: " + dealerId));

        RechargePackage pkg = packageRepository.findById(UUID.fromString(request.packageId())).
                orElseThrow(() -> new IllegalArgumentException("Package not found: " + request.packageId()));

        int creditsToAdd = pkg.getCredits();
        String description = "TEST Package Purchase: " + pkg.getName();

        // Credit the wallet
        wallet.credit(creditsToAdd);
        walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction(
                wallet.getId(),
                dealerId,
                TransactionType.CREDIT,
                creditsToAdd,
                description
        );
        transactionRepository.save(transaction);

        return getWalletDetails(dealerId);
    }
}

