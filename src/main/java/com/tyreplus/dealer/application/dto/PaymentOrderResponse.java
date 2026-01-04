package com.tyreplus.dealer.application.dto;

/**
 * Response sent to Mobile App after creating an order with Razorpay.
 */
public record PaymentOrderResponse(
        String gatewayOrderId,
        int amount,
        String currency
) {}