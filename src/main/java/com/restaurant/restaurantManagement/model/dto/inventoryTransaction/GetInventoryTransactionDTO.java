package com.restaurant.restaurantManagement.model.dto.inventoryTransaction;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.TransactionMotivation;
import com.restaurant.restaurantManagement.enums.TransactionType;
import com.restaurant.restaurantManagement.enums.UserProfile;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record GetInventoryTransactionDTO(
        Long id,
        TransactionType transactionType,
        int quantity,
        BigDecimal unitPrice,
        TransactionMotivation motivation,
        String details,
        LocalDateTime transactionDateAndTime,
        ProductDTO product,
        UserDTO responsible

) {
    @Builder
    public record ProductDTO(
            Long id,
            String name,
            MeasurementUnit measurementUnit
    ) {}
    @Builder
    public record UserDTO(
            Long id,
            String name,
            UserProfile profile
    ) {}
}
