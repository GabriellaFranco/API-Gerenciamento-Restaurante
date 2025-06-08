package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.TransactionType;
import com.restaurant.restaurantManagement.exception.BusinessException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.inventoryTransaction.GetInventoryTransactionDTO;
import com.restaurant.restaurantManagement.model.entity.InventoryTransaction;
import com.restaurant.restaurantManagement.model.entity.Product;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.model.mapper.InventoryTransactionMapper;
import com.restaurant.restaurantManagement.repository.InventoryTransactionRepository;
import com.restaurant.restaurantManagement.repository.ProductRepository;
import com.restaurant.restaurantManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public List<GetInventoryTransactionDTO> getTransactionsByProductId(Long productId) {
        return inventoryTransactionRepository.findByProductId(productId).stream()
                .map(inventoryTransactionMapper::toGetInventoryTransactionDTO).toList();
    }

    public List<GetInventoryTransactionDTO> getTransactionsByResponsibleId(Long responsibleId) {
        return inventoryTransactionRepository.findByResponsibleId(responsibleId).stream()
                .map(inventoryTransactionMapper::toGetInventoryTransactionDTO).toList();
    }

    @Transactional
    public void registerTransaction(GetInventoryTransactionDTO transactionDTO) {
        var product = productRepository.findById(transactionDTO.product().id())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + transactionDTO.product().id()));
        var user = userRepository.findById(transactionDTO.responsible().id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + transactionDTO.responsible().id()));

        var quantity = transactionDTO.quantity();
        if (quantity <= 0) {
            throw new BusinessException("The quantity must be positive!");
        }

        inventoryService.processTransaction(product, transactionDTO.transactionType(), transactionDTO.quantity());
        var transaction = buildTransaction(product, user, transactionDTO);
        inventoryTransactionRepository.save(transaction);

    }

    private InventoryTransaction buildTransaction(Product product, User responsible, GetInventoryTransactionDTO dto) {
        return InventoryTransaction.builder()
                .product(product)
                .userResponsible(responsible)
                .type(dto.transactionType())
                .quantity(dto.quantity())
                .measurementUnit(dto.product().measurementUnit())
                .unitPrice(dto.unitPrice())
                .motivation(dto.motivation())
                .details(dto.details())
                .transactionDateAndTime(LocalDateTime.now())
                .build();
    }
}
