package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.BusinessException;
import com.restaurant.restaurantManagement.exception.OperationNotAllowedException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.product.CreateProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.GetProductDTO;
import com.restaurant.restaurantManagement.model.dto.product.UpdateProductDTO;
import com.restaurant.restaurantManagement.model.entity.Product;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.model.mapper.ProductMapper;
import com.restaurant.restaurantManagement.repository.ProductRepository;
import com.restaurant.restaurantManagement.repository.UserRepository;
import com.restaurant.restaurantManagement.service.notification.EmailService;
import com.restaurant.restaurantManagement.service.notification.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final EmailService emailService;
    private final WhatsappService whatsappService;

    public List<GetProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toGetProductDTO).toList();
    }

    public GetProductDTO getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toGetProductDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public GetProductDTO createProduct(CreateProductDTO productDTO) {
        validateUniqueProductName(productDTO.name());
        validateMeasurementUnitByCategory(productDTO.category(), productDTO.measurementUnit());
        var productMapped = productMapper.toProduct(productDTO);
        var productSaved = productRepository.save(productMapped);
        return productMapper.toGetProductDTO(productSaved);
    }

    public GetProductDTO updateProduct(Long id, UpdateProductDTO productDTO) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        product.setName(productDTO.name());
        product.setCategory(productDTO.category());
        product.setMeasurementUnit(productDTO.measurementUnit());
        product.setPrice(productDTO.price());
        product.setMinQuantityOnStock(productDTO.minQuantityOnStock());
        if (!product.getCurrentStock().equals(productDTO.currentStock())) {
            inventoryService.updateStock(product, productDTO.currentStock());
        }

        var updatedProduct = productRepository.save(product);
        return productMapper.toGetProductDTO(updatedProduct);
    }

    public void deleteProduct(Long id, String userEmail) {
        var deletedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        var deletingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (deletingUser.getProfile() == UserProfile.EMPLOYEE) {
            throw new OperationNotAllowedException("You don't have permission to delete products!");
        }
        productRepository.delete(deletedProduct);
    }

    public List<GetProductDTO> searchProductsByFilter(String name, ProductCategory category, MeasurementUnit measurementUnit) {
        return productRepository.findByFilters(name, category, measurementUnit).stream().map(productMapper::toGetProductDTO).toList();
    }

    public void validateUniqueProductName(String name) {
        if(productRepository.findByName(name).isPresent()) {
            throw new BusinessException("A product with this name already exists: " + name);
        }
    }

    public void validateMeasurementUnitByCategory(ProductCategory category, MeasurementUnit unit) {
        if (category == ProductCategory.BEVERAGES && unit != MeasurementUnit.LITER) {
            throw new BusinessException("Beverages must be measured in liters!");
        }

        if (category != ProductCategory.BEVERAGES && unit == MeasurementUnit.LITER) {
            throw new BusinessException("Ingredients must be measured in kilograms, units, boxes, dozens, or milliliters!");
        }
    }
}
