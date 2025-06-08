package com.restaurant.restaurantManagement.model.dto.inventory;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CreateInventoryDTO(
        ProductDTO product,
        @Positive
        Long currentQuantity

) {
    @Builder
    public record ProductDTO(
            Long id,
            String name,
            MeasurementUnit measurementUnit
    ) {}
}
