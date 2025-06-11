package com.restaurant.restaurantManagement.model.dto.product;

import lombok.Builder;

@Builder
public record UpdateStockDTO(
        Long quantity
) {}
