package com.restaurant.restaurantManagement.model.dto.user;

import com.restaurant.restaurantManagement.enums.UserProfile;
import lombok.Builder;

@Builder
public record GetUserDTO(
        Long id,
        String name,
        String email,
        String phone,
        String cpf,
        UserProfile profile
) {}
