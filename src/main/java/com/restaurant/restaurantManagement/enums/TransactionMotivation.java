package com.restaurant.restaurantManagement.enums;

import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;

public enum TransactionMotivation {
    REPLENISHMENT,
    CONSUMPTION,
    WASTE,
    SPILLAGE,
    THEFT;

    public TransactionMotivation parseTransactionMotivation(String motivation) {
        try {
            return TransactionMotivation.valueOf(motivation.toUpperCase());
        } catch (IllegalArgumentException exc) {
            throw new ResourceNotFoundException("Transaction motivation not found:" + motivation);
        }
    }
}
