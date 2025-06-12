package com.restaurant.restaurantManagement.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.restaurant.restaurantManagement.configuration.security.SecurityConfig;
import com.restaurant.restaurantManagement.controller.ProductController;
import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.product.CreateProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.GetProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.UpdateProductDTO;
import com.restaurant.restaurantManagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private GetProductDTO product1;
    private GetProductDTO product2;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        product1 = GetProductDTO.builder()
                .id(1L)
                .name("Chicken")
                .category(ProductCategory.MEAT)
                .measurementUnit(MeasurementUnit.KILOGRAM)
                .price(BigDecimal.valueOf(9.99))
                .minQuantityStock(20L)
                .build();

        product2 = GetProductDTO.builder()
                .id(2L)
                .name("Corn")
                .category(ProductCategory.CANNED)
                .measurementUnit(MeasurementUnit.UNIT)
                .price(BigDecimal.valueOf(2.53))
                .minQuantityStock(15L)
                .build();
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenGetAllProducts_ShouldReturnAListWithAllExistingProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chicken"))
                .andExpect(jsonPath("$[1].name").value("Corn"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenGetAllProductsWithNoExistingProducts_ShouldReturnNoContent() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());
        mockMvc.perform(get("/products"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenGetProductById_ShouldReturnAProduct() throws Exception {
        when(productService.getProductById(2L)).thenReturn(product2);
        mockMvc.perform(get("/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Corn"))
                .andExpect(jsonPath("$.measurementUnit").value(MeasurementUnit.UNIT.toString()));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenGetProductByIdWithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(productService.getProductById(5L)).thenThrow(new ResourceNotFoundException("Product not found"));
        mockMvc.perform(get("/products/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenCreatingProduct_ShouldReturnCreated() throws Exception {
        var newProduct = GetProductDTO.builder()
                .name("Wine")
                .category(ProductCategory.BEVERAGES)
                .measurementUnit(MeasurementUnit.LITER)
                .price(BigDecimal.valueOf(160.99))
                .minQuantityStock(5L)
                .build();

        when(productService.createProduct(any(CreateProductDTO.class))).thenReturn(newProduct);
        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Wine"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenUpdatingAProduct_ShouldReturnUpdatedProduct() throws Exception {
        var updateProduct = UpdateProductDTO.builder()
                .name("Vodka")
                .build();

        var updatedProduct = GetProductDTO.builder()
                .id(1L)
                .name("Lamb")
                .category(ProductCategory.MEAT)
                .measurementUnit(MeasurementUnit.KILOGRAM)
                .price(BigDecimal.valueOf(9.99))
                .minQuantityStock(20L)
                .build();

        when(productService.updateProduct(eq(1L), any(UpdateProductDTO.class))).thenReturn(updatedProduct);
        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lamb"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenSearchProductByFilter_ShouldReturnAListOfProducts() throws Exception {
        when(productService.searchProductsByFilter("Chicken", ProductCategory.MEAT, MeasurementUnit.KILOGRAM))
                .thenReturn(List.of(product1));

        mockMvc.perform(get("/products/search")
                        .param("name", "Chicken")
                        .param("category", ProductCategory.MEAT.toString())
                        .param("measurementUnit", MeasurementUnit.KILOGRAM.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Chicken"))
                .andExpect(jsonPath("$[0].category").value(ProductCategory.MEAT.toString()));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testProductController_WhenSearchProductByFilterWithOnlyName_ShouldReturnAListOfProducts() throws Exception {
        when(productService.searchProductsByFilter("Chicken", null, null)).thenReturn(List.of(product1));

        mockMvc.perform(get("/products/search")
                        .param("name", "Chicken"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Chicken"))
                .andExpect(jsonPath("$[0].category").value(ProductCategory.MEAT.toString()));
    }

}
