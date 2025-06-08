package com.restaurant.restaurantManagement.model.dto.product;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateProductDTO(
        String name,
        ProductCategory category,
        MeasurementUnit measurementUnit,
        BigDecimal price,
        Long currentStock,
        Long minQuantityOnStock
) {}
