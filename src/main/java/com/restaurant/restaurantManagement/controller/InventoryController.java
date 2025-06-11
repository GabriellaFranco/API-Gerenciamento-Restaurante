package com.restaurant.restaurantManagement.controller;


import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.model.dto.inventory.GetInventoryDTO;
import com.restaurant.restaurantManagement.model.dto.product.UpdateStockDTO;
import com.restaurant.restaurantManagement.service.InventoryService;
import com.restaurant.restaurantManagement.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    @Operation(
            summary = "Finds a inventory that matches the product name provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/{productName}/inventory")
    public ResponseEntity<GetInventoryDTO> getInventoryByProductName(@PathVariable String productName) {
        var inventory = inventoryService.getInventoryByProductName(productName);
        return ResponseEntity.ok(inventory);
    }

    @Operation(
            summary = "Returns a list with all existing inventories that match the informed product category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping("/{category}")
    public ResponseEntity<List<GetInventoryDTO>> getInventoriesByCategory(@PathVariable ProductCategory category) {
        var inventories = inventoryService.getInventoriesByProductCategory(category);
        return inventories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(inventories);
    }

    @Operation(
            summary = "Returns a list with all existing inventories that have low stock",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping("/low-stock")
    public ResponseEntity<List<GetInventoryDTO>> getInventoriesWithLowStock() {
        var inventories = inventoryService.getInventoriesWithLowStock();
        return inventories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(inventories);
    }

    @Operation(
            summary = "Performs a transaction to increase the inventory stock of the informed product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid UpdateStockDTO"),
            }
    )
    @PostMapping("/{productName}/inbound")
    public ResponseEntity<GetInventoryDTO> increaseStockByProductName(@PathVariable String productName, @RequestBody UpdateStockDTO quantityDTO) {
        var product = productService.getProductByName(productName);
        inventoryService.increaseStock(product, quantityDTO.quantity());
        var updatedInventory = inventoryService.getInventoryByProductName(productName);
        return ResponseEntity.ok(updatedInventory);
    }

    @Operation(
            summary = "Performs a transaction to decrease the inventory stock of the informed product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid UpdateStockDTO"),
            }
    )
    @PostMapping("/{productName}/outbound")
    public ResponseEntity<GetInventoryDTO> decreaseStockByProductName(@PathVariable String productName, @RequestBody UpdateStockDTO quantityDTO) {
        var product = productService.getProductByName(productName);
        inventoryService.decreaseStock(product, quantityDTO.quantity());
        var updatedInventory = inventoryService.getInventoryByProductName(productName);
        return ResponseEntity.ok(updatedInventory);
    }

}
