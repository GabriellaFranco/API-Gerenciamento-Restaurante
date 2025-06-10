package com.restaurant.restaurantManagement.repository;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import com.restaurant.restaurantManagement.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p\n" +
            "    WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))\n" +
            "      AND (:measurementUnit IS NULL OR p.measurementUnit = :measurementUnit)\n" +
            "      AND (:category IS NULL OR p.category = :category)\n" +
            "\"\"\")")
    List<Product> findByFilters(String name, ProductCategory category, MeasurementUnit measurementUnit);
}
