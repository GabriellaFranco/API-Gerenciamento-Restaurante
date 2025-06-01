package com.restaurant.restaurantManagement.model.mapper;

import com.restaurant.restaurantManagement.model.dto.inventoryTransaction.CreateInventoryTransactionDTO;
import com.restaurant.restaurantManagement.model.dto.inventoryTransaction.GetInventoryTransactionDTO;
import com.restaurant.restaurantManagement.model.entity.InventoryTransaction;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class InventoryTransactionMapper {

    public InventoryTransaction toInventoryTransaction(CreateInventoryTransactionDTO inventoryTransactionDTO, Product product) {
        return InventoryTransaction.builder()
                .type(inventoryTransactionDTO.transactionType())
                .quantity(inventoryTransactionDTO.quantity())
                .unitPrice(inventoryTransactionDTO.unitPrice())
                .motivation(inventoryTransactionDTO.motivation())
                .details(inventoryTransactionDTO.details())
                .product(product)
                .build();
    }

    public GetInventoryTransactionDTO toGetInventoryTransactionDTO(InventoryTransaction inventoryTransaction) {
        return GetInventoryTransactionDTO.builder()
                .id(inventoryTransaction.getId())
                .transactionType(inventoryTransaction.getType())
                .quantity(inventoryTransaction.getQuantity())
                .unitPrice(inventoryTransaction.getUnitPrice())
                .motivation(inventoryTransaction.getMotivation())
                .details(inventoryTransaction.getDetails())
                .product(GetInventoryTransactionDTO.ProductDTO.builder()
                        .id(inventoryTransaction.getProduct().getId())
                        .name(inventoryTransaction.getProduct().getName())
                        .measurementUnit(inventoryTransaction.getProduct().getMeasurementUnit())
                        .build())
                .responsible(GetInventoryTransactionDTO.UserDTO.builder()
                        .id(inventoryTransaction.getUserResponsible().getId())
                        .name(inventoryTransaction.getUserResponsible().getName())
                        .profile(inventoryTransaction.getUserResponsible().getProfile())
                        .build())
                .build();
    }
}
