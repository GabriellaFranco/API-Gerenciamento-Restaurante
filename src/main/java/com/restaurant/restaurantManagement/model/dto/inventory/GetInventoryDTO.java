package com.restaurant.restaurantManagement.model.dto.inventory;

import com.restaurant.restaurantManagement.enums.ProductCategory;
import lombok.*;

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
            String name,
            ProductCategory category,
            Long minQuantityStock
    ) {}
}
