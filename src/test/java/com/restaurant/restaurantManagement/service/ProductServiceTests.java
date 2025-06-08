package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.BusinessException;
import com.restaurant.restaurantManagement.exception.OperationNotAllowedException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.entity.Product;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.model.mapper.ProductMapper;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private WhatsappService whatsappService;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(1L)
                .name("Coca-Cola")
                .category(ProductCategory.BEVERAGES)
                .measurementUnit(MeasurementUnit.LITER)
                .price(BigDecimal.valueOf(10))
                .currentStock(5L)
                .minQuantityOnStock(10L)
                .build();
    }

    @Test
    void testProductService_WhenGetProductByIdWithNonExistingId_ShouldThrowResourceNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(9L));
    }

    @Test
    void testProductService_WhenCreatingAProductWithExistingName_ShouldThrowBusinessException() {
        when(productRepository.findByName("Coca-Cola")).thenReturn(Optional.of(product));
        var exc = assertThrows(BusinessException.class, () -> productService.validateUniqueProductName("Coca-Cola"));
        assertTrue(exc.getMessage().contains("already exists"));
    }

    @Test
    void testProductService_WhenRegisteringBeverageWithWrongMeasurementUnit_ShouldThrowBusinessException() {
        var exc = assertThrows(BusinessException.class, () ->
                productService.validateMeasurementUnitByCategory(ProductCategory.BEVERAGES, MeasurementUnit.BOX));
        assertTrue(exc.getMessage().contains("measured in liters"));
    }

    @Test
    void testProductService_WhenTryingToDeleteProductHavingEmployeeProfile_ShouldThrowOperationNotAllowedException() {
        User employee = new User();
        employee.setProfile(UserProfile.EMPLOYEE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertThrows(OperationNotAllowedException.class, () -> productService.deleteProduct(1L, employee));
    }

    @Test
    void testProductService_WhenTryingToDeleteProductHavingOwnerProfile_ShouldDeleteProduct() {
        User owner = new User();
        owner.setProfile(UserProfile.OWNER);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L, owner);
        verify(productRepository).delete(product);
    }

}
