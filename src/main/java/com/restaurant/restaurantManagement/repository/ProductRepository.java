package com.restaurant.restaurantManagement.repository;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(String name);

    @Query("""
    SELECT p FROM Product p
    WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:measurementUnit IS NULL OR p.measurementUnit = :measurementUnit)
      AND (:category IS NULL OR p.category = :category)
""")
    List<Product> findByFilters(String name, ProductCategory category, MeasurementUnit measurementUnit);

}
