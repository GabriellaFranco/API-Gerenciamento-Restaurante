package com.restaurant.restaurantManagement.model.dto.inventoryTransaction;

import com.restaurant.restaurantManagement.enums.TransactionMotivation;
import com.restaurant.restaurantManagement.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateInventoryTransactionDTO(

        @NotNull
        TransactionType transactionType,

        @NotNull
        @Positive
        int quantity,

        @NotNull
        @Positive
        BigDecimal unitPrice,

        @NotNull
        TransactionMotivation motivation,

        @NotNull
        @Size(min = 10)
        String details,

        @NotNull
        Long productId
) {
}
