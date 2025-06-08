package com.restaurant.restaurantManagement.model.mapper;

import com.restaurant.restaurantManagement.model.dto.product.CreateProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.GetProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.UpdateProductDTO;
import com.restaurant.restaurantManagement.model.entity.Inventory;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toProduct(CreateProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.name())
                .category(productDTO.category())
                .measurementUnit(productDTO.measurementUnit())
                .price(productDTO.price())
                .minQuantityOnStock(productDTO.minQuantityStock())
                .inventory(Inventory.builder()
                        .currentQuantity(productDTO.inventory().currentStock())
                        .lastUpdatedAt(productDTO.inventory().lastUpdateAt())
                        .build())
                .build();
    }

    public GetProductDTO toGetProductDTO(Product product) {
        return GetProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .measurementUnit(product.getMeasurementUnit())
                .price(product.getPrice())
                .minQuantityStock(product.getMinQuantityOnStock())
                .inventory(GetProductDTO.InventoryDTO.builder()
                        .currentQuantity(product.getInventory().getCurrentQuantity())
                        .lastUpdatedAt(product.getInventory().getLastUpdatedAt())
                        .build())
                .build();
    }

    public Product toUpdateFromDTO(UpdateProductDTO updateProductDTO, Product existingProduct) {
        return Product.builder()
                .id(existingProduct.getId())
                .name(updateProductDTO.name() != null ? updateProductDTO.name() : existingProduct.getName())
                .category(updateProductDTO.category() != null ? updateProductDTO.category() : existingProduct.getCategory())
                .price(updateProductDTO.price() != null ? updateProductDTO.price() : existingProduct.getPrice())
                .measurementUnit(updateProductDTO.measurementUnit() != null ? updateProductDTO.measurementUnit() : existingProduct.getMeasurementUnit())
                .currentStock(updateProductDTO.currentStock() != null ? updateProductDTO.currentStock() : existingProduct.getCurrentStock())
                .minQuantityOnStock(updateProductDTO.minQuantityOnStock() != null ? updateProductDTO.minQuantityOnStock() : existingProduct.getMinQuantityOnStock())
                .build();
    }
}
