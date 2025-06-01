package com.restaurant.restaurantManagement.enums;

import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;

public enum UserProfile {
    OWNER,
    EMPLOYEE;

    public UserProfile parseUserProfile(String profile) {
        try {
            return UserProfile.valueOf(profile.toUpperCase());
        } catch (IllegalArgumentException exc) {
            throw new ResourceNotFoundException("Profile not found: " + profile);
        }
    }
}


