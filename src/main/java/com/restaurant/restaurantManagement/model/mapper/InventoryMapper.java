package com.restaurant.restaurantManagement.model.mapper;

import com.restaurant.restaurantManagement.model.dto.inventory.CreateInventoryDTO;
import com.restaurant.restaurantManagement.model.dto.inventory.GetInventoryDTO;
import com.restaurant.restaurantManagement.model.entity.Inventory;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toInventory(CreateInventoryDTO inventoryDTO, Product product) {
        return Inventory.builder()
                .currentQuantity(inventoryDTO.currentQuantity())
                .product(product)
                .build();
    }

    public GetInventoryDTO toGetInventoryDTO(Inventory inventory) {
        return GetInventoryDTO.builder()
                .id(inventory.getId())
                .currentQuantity(inventory.getCurrentQuantity())
                .lastUpdatedAt(inventory.getLastUpdatedAt())
                .product(GetInventoryDTO.ProductDTO.builder()
                        .id(inventory.getProduct().getId())
                        .name(inventory.getProduct().getName())
                        .build())
                .build();
    }
}
