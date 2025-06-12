package com.restaurant.restaurantManagement.enums;

import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;

public enum ProductCategory {
    PERISHABLES,
    NOT_PERISHABLE,
    CANNED,
    BEVERAGES,
    CONDIMENTS,
    BAKERY,
    DAIRY,
    MEAT,
    SEAFOOD,
    PREPARED_FOOD;

    public ProductCategory parseProductCategory(String category) {
        try {
            return ProductCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException exc) {
            throw new ResourceNotFoundException("Product category not found: " + category);
        }
    }
}
