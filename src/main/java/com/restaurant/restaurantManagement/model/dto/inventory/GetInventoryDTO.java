package com.restaurant.restaurantManagement.model.dto.inventory;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetInventoryDTO(
      Long id,
      Long currentQuantity,
      LocalDateTime lastUpdatedAt,
      ProductDTO product

) {
    @Builder
    public record ProductDTO(
            Long id,
            String name
    ) {}
}
