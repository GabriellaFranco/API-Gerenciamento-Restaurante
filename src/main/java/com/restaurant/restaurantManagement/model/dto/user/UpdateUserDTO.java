package com.restaurant.restaurantManagement.model.dto.user;

import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.model.entity.Authority;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateUserDTO(
        String name,
        String email,
        String phone,
        String password,
        UserProfile profile,
        List<Authority> authority
) {}
