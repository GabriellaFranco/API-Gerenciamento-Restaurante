package com.restaurant.restaurantManagement.controller.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantManagement.configuration.security.SecurityConfig;
import com.restaurant.restaurantManagement.controller.InventoryController;
import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.model.dto.inventory.CreateInventoryDTO;
import com.restaurant.restaurantManagement.model.dto.inventory.GetInventoryDTO;
import com.restaurant.restaurantManagement.model.dto.product.GetProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.UpdateStockDTO;
import com.restaurant.restaurantManagement.model.entity.Product;
import com.restaurant.restaurantManagement.service.InventoryService;
import com.restaurant.restaurantManagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(InventoryController.class)
public class InventoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ProductService productService;

    private GetInventoryDTO inventory;
    private Product product;

    @BeforeEach
    void setup() {

        inventory = GetInventoryDTO.builder()
                .id(1L)
                .product(GetInventoryDTO.ProductDTO.builder()
                        .name("Coconut")
                        .category(ProductCategory.PERISHABLES)
                        .minQuantityStock(30L)
                        .build())
                .currentQuantity(20L)
                .build();

        product = Product.builder()
                .name("Radish")
                .id(1L)
                .category(ProductCategory.PERISHABLES)
                .measurementUnit(MeasurementUnit.KILOGRAM)
                .currentStock(20L)
                .build();
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryController_WhenGetInventoryByProductName_ShouldReturnInventoryObject() throws Exception {
        when(inventoryService.getInventoryByProductName("coconut")).thenReturn(inventory);
        mockMvc.perform(get("/inventories/coconut/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Coconut"))
                .andExpect(jsonPath("$.currentQuantity").value(20));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryController_WhenGetInventoriesByProductCategory_ShouldReturnAListOfInventories() throws Exception {
        when(inventoryService.getInventoriesByProductCategory(ProductCategory.PERISHABLES)).thenReturn(List.of(inventory));

        mockMvc.perform(get("/inventories/PERISHABLES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product.name").value("Coconut"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryController_WhenGetInventoriesByLowStock_ShouldReturnAListOfInventories() throws Exception {
        when(inventoryService.getInventoriesWithLowStock()).thenReturn(List.of(inventory));

        mockMvc.perform(get("/inventories/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product.name").value("Coconut"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryController_WhenIncreaseStockByProductName_ShouldIncreaseProductStock() throws Exception {
        UpdateStockDTO updateStockDTO = new UpdateStockDTO(10L);
        when(productService.getProductByName("Radish")).thenReturn(product);
        GetInventoryDTO updatedInventory = GetInventoryDTO.builder()
                .id(1L)
                .product(GetInventoryDTO.ProductDTO.builder().name("Radish").build())
                .currentQuantity(30L)
                .build();

        when(inventoryService.getInventoryByProductName("Radish")).thenReturn(updatedInventory);

        mockMvc.perform(post("/inventories/Radish/inbound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Radish"))
                .andExpect(jsonPath("$.currentQuantity").value(30));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryController_WhenDecreaseStockByProductName_ShouldDecreaseProductStock() throws Exception {
        UpdateStockDTO updateStockDTO = new UpdateStockDTO(10L);
        when(productService.getProductByName("Radish")).thenReturn(product);
        GetInventoryDTO updatedInventory = GetInventoryDTO.builder()
                .id(1L)
                .product(GetInventoryDTO.ProductDTO.builder().name("Radish").build())
                .currentQuantity(10L)
                .build();

        when(inventoryService.getInventoryByProductName("Radish")).thenReturn(updatedInventory);
        mockMvc.perform(post("/inventories/Radish/outbound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStockDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Radish"))
                .andExpect(jsonPath("$.currentQuantity").value(10));

    }
}


