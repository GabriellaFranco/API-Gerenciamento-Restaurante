package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.enums.TransactionType;
import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.BusinessException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.inventory.GetInventoryDTO;
import com.restaurant.restaurantManagement.model.entity.Inventory;
import com.restaurant.restaurantManagement.model.entity.Product;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.model.mapper.InventoryMapper;
import com.restaurant.restaurantManagement.model.mapper.ProductMapper;
import com.restaurant.restaurantManagement.repository.InventoryRepository;
import com.restaurant.restaurantManagement.repository.ProductRepository;
import com.restaurant.restaurantManagement.repository.UserRepository;
import com.restaurant.restaurantManagement.service.notification.EmailService;
import com.restaurant.restaurantManagement.service.notification.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    public final WhatsappService whatsappService;

    public GetInventoryDTO getInventoryByProductName(String productName) {
        return inventoryRepository.findByProductName(productName).map(inventoryMapper::toGetInventoryDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not Found for product: " + productName));
    }

    public List<GetInventoryDTO> getInventoriesByProductCategory(ProductCategory category) {
        return inventoryRepository.findByProductCategoryIgnoreCase(category).stream().map(inventoryMapper::toGetInventoryDTO).toList();
    }

    public List<GetInventoryDTO> getInventoriesWithLowStock() {
        return inventoryRepository.findWithLowStock().stream().map(inventoryMapper::toGetInventoryDTO).toList();
    }

    public GetInventoryDTO increaseStock(Product product, Long amount) {
        Long updatedStock = product.getCurrentStock() + amount;
        updateStock(product, updatedStock);
        Inventory updatedInventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found."));
        return inventoryMapper.toGetInventoryDTO(updatedInventory);
    }

    public GetInventoryDTO decreaseStock(Product product, Long quantity) {
        Long updatedStock = product.getCurrentStock() - quantity;
        if (updatedStock < 0) {
            throw new BusinessException("The stock can't be negative!");
        }
        updateStock(product, updatedStock);
        Inventory updatedInventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found."));
        return inventoryMapper.toGetInventoryDTO(updatedInventory);
    }

    public void updateStock(Product product, Long newStock) {
        product.setCurrentStock(newStock);

        if (newStock <= product.getMinQuantityOnStock()) {
            notifyOwnersIfStockIsLow(product);
        }
        productRepository.save(product);
    }

    public void notifyOwnersIfStockIsLow(Product product) {
        if (product.getCurrentStock() <= product.getMinQuantityOnStock()) {
            List<User> owners = userRepository.findByProfile(UserProfile.OWNER);

            for (User owner : owners) {
                var subject = "Low Stock: " + product.getName();
                var message = "The stock of the product " + product.getName() + " is below the minimum amount.\n" +
                        "Current stock: " + product.getCurrentStock() + "\n" +
                        "Minimum stock: " + product.getMinQuantityOnStock();

                emailService.sendEmail(owner.getEmail(), subject, message);
                whatsappService.sendWhatsAppMessage(owner.getPhone(), message);
            }
        }
    }

    public void processTransaction(Product product, TransactionType type, Long quantity) {
        if (quantity <= 0) {
            throw new BusinessException("A quantidade deve ser positiva.");
        }
        switch (type) {
            case INBOUND -> increaseStock(product, quantity);
            case OUTBOUND -> decreaseStock(product, quantity);
            default -> throw new BusinessException("Tipo de transação inválido: " + type);
        }
    }
}
