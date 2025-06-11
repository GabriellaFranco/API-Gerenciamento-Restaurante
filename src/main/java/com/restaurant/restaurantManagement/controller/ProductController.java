package com.restaurant.restaurantManagement.controller;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.model.dto.product.CreateProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.GetProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.UpdateProductDTO;
import com.restaurant.restaurantManagement.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Returns a list with all existing products",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping
    public ResponseEntity<List<GetProductDTO>> getAllProducts() {
        var products = productService.getAllProducts();
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Finds a product that matches the id provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GetProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
            summary = "Creates a new product",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Operation successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid data")
            }
    )    @PostMapping
    public ResponseEntity<GetProductDTO> createProduct(@Valid @RequestBody CreateProductDTO productDTO) {
        var product = productService.createProduct(productDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(product.id()).toUri();
        return ResponseEntity.created(uri).body(product);
    }

    @Operation(
            summary = "Updates the product with the provided id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<GetProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO productDTO) {
        var updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(
            summary = "Delete the product with the informed id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Can't self delete")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id, @AuthenticationPrincipal UserDetails authUser) {
        productService.deleteProduct(id, authUser.getUsername());
        return ResponseEntity.ok("Product deleted: " + id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GetProductDTO>> getProductsByFilter(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) ProductCategory category,
                                                                   @RequestParam(required = false) MeasurementUnit measurementUnit) {

        var products = productService.searchProductsByFilter(name, category, measurementUnit);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }
}
