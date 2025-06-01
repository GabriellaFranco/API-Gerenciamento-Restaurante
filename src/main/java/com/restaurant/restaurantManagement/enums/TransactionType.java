package com.restaurant.restaurantManagement.enums;

import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;

public enum TransactionType {
    INBOUND,
    OUTBOUND,
    ADJUSTMENT;

    public TransactionType parseTransactionType(String transactionType) {
        try {
            return TransactionType.valueOf(transactionType.toUpperCase());
        } catch (IllegalArgumentException exc) {
            throw new ResourceNotFoundException("Transaction type not found: " + transactionType);
        }
    }
}
