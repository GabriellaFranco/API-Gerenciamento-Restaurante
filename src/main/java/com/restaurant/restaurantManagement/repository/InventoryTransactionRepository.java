package com.restaurant.restaurantManagement.repository;

import com.restaurant.restaurantManagement.model.entity.InventoryTransaction;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductId(Long id);

    List<InventoryTransaction> findByResponsibleId(Long id);
}
