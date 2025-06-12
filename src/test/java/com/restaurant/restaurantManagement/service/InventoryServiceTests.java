package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.BusinessException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.entity.Inventory;
import com.restaurant.restaurantManagement.model.entity.Product;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.repository.InventoryRepository;
import com.restaurant.restaurantManagement.repository.ProductRepository;
import com.restaurant.restaurantManagement.repository.UserRepository;
import com.restaurant.restaurantManagement.service.notification.EmailService;
import com.restaurant.restaurantManagement.service.notification.WhatsappService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private WhatsappService whatsappService;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private Product product;

    @BeforeEach
    void setup() {
        product =  Product.builder()
                .id(1L)
                .name("Pepino")
                .category(ProductCategory.CANNED)
                .measurementUnit(MeasurementUnit.UNIT)
                .price(BigDecimal.valueOf(3.99))
                .currentStock(20L)
                .minQuantityOnStock(15L)
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .lastUpdatedAt(LocalDateTime.now().minusDays(10))
                .currentQuantity(20L)
                .build();
    }

    @Test
    void testInventoryService_WhenGetInventoryByNonExistingProductName_ShouldThrowResourceNotFoundException() {
        when(inventoryRepository.findByProductName(product.getName())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryByProductName(product.getName()));
    }

    @Test
    void testInventoryService_WhenIncreaseStock_ShouldIncreaseStockAndSaveProduct() {
        product.setCurrentStock(10L);
        product.setMinQuantityOnStock(5L);

        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        inventoryService.increaseStock(product, 5L);

        assertEquals(15L, product.getCurrentStock());
        verify(productRepository).save(product);
    }

    @Test
    void testInventoryService_WhenDecreaseStock_ShouldDecreaseStockAndSaveProduct() {
        product.setCurrentStock(10L);
        product.setMinQuantityOnStock(3L);

        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        inventoryService.decreaseStock(product, 5L);

        assertEquals(5L, product.getCurrentStock());
        verify(productRepository).save(product);
    }

    @Test
    void testInventoryService_WhenDecreaseStockBelowZero_ShouldThrowBusinessException() {
        product.setCurrentStock(3L);
        assertThrows(BusinessException.class, () -> inventoryService.decreaseStock(product, 5L));
        verify(productRepository, never()).save(any());
    }

    @Test
    void testInventoryService_WhenStockIsBelowMinimumQuantity_ShouldNotifyOwners() {
        product.setCurrentStock(6L);
        product.setMinQuantityOnStock(5L);

        User owner = new User();
        owner.setEmail("owner@example.com");
        owner.setPhone("+55123456789");
        owner.setProfile(UserProfile.OWNER);

        when(userRepository.findByProfile(UserProfile.OWNER)).thenReturn(List.of(owner));
        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        inventoryService.updateStock(product, 4L);

        verify(emailService).sendEmail(eq("owner@example.com"), contains("Low Stock"), contains("Pepino"));
        verify(whatsappService).sendWhatsAppMessage(eq("+55123456789"), contains("Pepino"));

    }
}
