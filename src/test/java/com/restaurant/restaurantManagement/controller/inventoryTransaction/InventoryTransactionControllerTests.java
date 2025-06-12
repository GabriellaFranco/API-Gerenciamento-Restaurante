package com.restaurant.restaurantManagement.controller.inventoryTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantManagement.configuration.security.SecurityConfig;
import com.restaurant.restaurantManagement.controller.InventoryTransactionController;
import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.TransactionMotivation;
import com.restaurant.restaurantManagement.enums.TransactionType;
import com.restaurant.restaurantManagement.model.dto.inventoryTransaction.GetInventoryTransactionDTO;
import com.restaurant.restaurantManagement.repository.ProductRepository;
import com.restaurant.restaurantManagement.service.InventoryTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfig.class)
@WebMvcTest(InventoryTransactionController.class)
public class InventoryTransactionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private InventoryTransactionService inventoryTransactionService;

    private GetInventoryTransactionDTO transactionDTO;

    @BeforeEach
    void setup() {
        transactionDTO = GetInventoryTransactionDTO.builder()
                .product(GetInventoryTransactionDTO.ProductDTO.builder()
                        .id(1L)
                        .name("Tomato")
                        .measurementUnit(MeasurementUnit.KILOGRAM)
                        .build())
                .responsible(GetInventoryTransactionDTO.UserDTO.builder()
                        .id(1L)
                        .name("João da Silva")
                        .build())
                .transactionType(TransactionType.INBOUND)
                .id(1L)
                .quantity(10L)
                .unitPrice(BigDecimal.valueOf(9.99))
                .motivation(TransactionMotivation.REPLENISHMENT)
                .details("Weekly buying quota of the product")
                .build();
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryTransactionController_WhenGetTransactionsByProductId_ShouldReturnAListOfProducts() throws Exception {
        when(inventoryTransactionService.getTransactionsByProductId(1L))
                .thenReturn(List.of(transactionDTO));

        mockMvc.perform(get("/inventory-transactions/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product.name").value("Tomato"))
                .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryTransactionController_WhenGetTransactionsByResponsibleId_ShouldReturnAListOfProducts() throws Exception {
        when(inventoryTransactionService.getTransactionsByResponsibleId(1L)).thenReturn(List.of(transactionDTO));
        mockMvc.perform(get("/inventory-transactions/responsible/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].responsible.name").value("João da Silva"))
                .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = "OWNER")
    void testInventoryTransactionController_WhenRegisterTransaction_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/inventory-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated());

        verify(inventoryTransactionService, times(1)).registerTransaction(any(GetInventoryTransactionDTO.class));
    }

}
