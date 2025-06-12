package com.restaurant.restaurantManagement.model.dto.inventory;

import lombok.Builder;

@Builder
public record TransactionQuantityDTO(
        Long quantity
) {}
