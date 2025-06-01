package com.restaurant.restaurantManagement.model.entity;

import com.restaurant.restaurantManagement.enums.MeasurementUnit;
import com.restaurant.restaurantManagement.enums.TransactionMotivation;
import com.restaurant.restaurantManagement.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit measurementUnit;
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private TransactionMotivation motivation;
    private String details;
    private LocalDateTime transactionDateAndTime;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userResponsible;
}
