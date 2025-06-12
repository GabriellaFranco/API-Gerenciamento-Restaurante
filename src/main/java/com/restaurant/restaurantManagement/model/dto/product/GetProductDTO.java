package com.restaurant.restaurantManagement.model.dto.product;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record GetProductDTO(
        Long id,
        String name,
        ProductCategory category,
        MeasurementUnit measurementUnit,
        BigDecimal price,
        Long minQuantityStock,
        InventoryDTO inventory
) {

    @Builder
    public record InventoryDTO(
            Long currentQuantity,
            LocalDateTime lastUpdatedAt
    ) {}
}
