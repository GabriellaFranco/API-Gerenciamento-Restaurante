package com.restaurant.restaurantManagement.repository;

import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.model.dto.product.GetProductDTO;
import com.restaurant.restaurantManagement.model.entity.Inventory;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE LOWER(i.product.name) = LOWER(:productName)")
    Optional<Inventory> findByProductName(String productName);

    @Query("SELECT i FROM Inventory i WHERE i.product = product")
    Optional<Inventory> findByProduct(Product product);

    @Query("SELECT i FROM Inventory i WHERE i.product.currentStock <= i.product.minQuantityOnStock")
    List<Inventory> findWithLowStock();

    @Query("SELECT i FROM Inventory i WHERE i.product.category = :category")
    List<Inventory> findByProductCategoryIgnoreCase(ProductCategory category);


}
