package com.restaurant.restaurantManagement.model.dto.inventory;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import lombok.Builder;

@Builder
public record CreateInventoryDTO(
        ProductDTO product,
        Long currentQuantity

) {
    @Builder
    public record ProductDTO(
            Long id,
            String name,
            MeasurementUnit measurementUnit
    ) {}
}
