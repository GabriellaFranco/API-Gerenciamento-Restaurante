package com.restaurant.restaurantManagement.controller;

import com.restaurant.restaurantManagement.model.dto.inventoryTransaction.GetInventoryTransactionDTO;
import com.restaurant.restaurantManagement.service.InventoryTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("inventory-transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @Operation(
            summary = "Creates a new inventory transaction",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Operation successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid data")
            }
    )
    @PostMapping
    public ResponseEntity<GetInventoryTransactionDTO> registerTransaction(@Valid @RequestBody GetInventoryTransactionDTO transactionDTO) {
        inventoryTransactionService.registerTransaction(transactionDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transactionDTO.id()).toUri();
        return ResponseEntity.created(uri).body(transactionDTO);
    }

    @Operation(
            summary = "Returns a list with all existing inventory transactions that match the informed product id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<GetInventoryTransactionDTO>> getTransactionByProductId(@PathVariable Long productId) {
        var transactions = inventoryTransactionService.getTransactionsByProductId(productId);
        return transactions.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Returns a list with all existing inventory transactions that match the informed responsible id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping("/responsible/{responsibleId}")
    public ResponseEntity<List<GetInventoryTransactionDTO>> getTransactionsByResponsibleId(@PathVariable Long responsibleId) {
        var transactions = inventoryTransactionService.getTransactionsByResponsibleId(responsibleId);
        return transactions.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(transactions);
    }
}
