package com.restaurant.restaurantManagement.entity;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit measurementUnit;
    private BigDecimal price;
    private int currentStock;
    private int minQuantityOnStock;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    List<InventoryTransaction> inventoryTransactions;

    @OneToOne(mappedBy = "product")
    private Inventory inventory;

}
