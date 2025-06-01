package com.restaurant.restaurantManagement.model.dto.product;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateProductDTO(

        @NotNull
        @Size(min = 3)
        String name,

        @NotNull
        ProductCategory category,

        @NotNull
        MeasurementUnit measurementUnit,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Positive
        int minQuantityStock
) {
}
