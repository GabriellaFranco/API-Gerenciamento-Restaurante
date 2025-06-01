package com.restaurant.restaurantManagement.model.entity;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long currentQuantity;

    private LocalDateTime lastUpdatedAt;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
