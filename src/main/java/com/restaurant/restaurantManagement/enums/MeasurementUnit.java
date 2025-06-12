package com.restaurant.restaurantManagement.enums;

import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;

public enum MeasurementUnit {
    KILOGRAM,
    UNIT,
    LITER,
    MILLILITER,
    BOX,
    DOZEN;

    public MeasurementUnit parseMeasurementUnit(String measurementUnit) {
        try {
            return MeasurementUnit.valueOf(measurementUnit.toUpperCase());
        } catch (IllegalArgumentException exc) {
            throw new ResourceNotFoundException("Measurement unit not found: " + measurementUnit);
        }
    }
}
