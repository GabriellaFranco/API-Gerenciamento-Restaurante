package com.restaurant.restaurantManagement.model.dto.user;

import com.restaurant.restaurantManagement.enums.UserProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserDTO(

        @NotNull
        @Size(min = 6)
        String name,

        @NotNull
        @Email
        String email,

        @NotNull
        @Pattern(
                regexp = "^55[1-9]{2}9\\d{8}$",
                message = "Invalid phone number, use the format 55DDD9XXXXXXXX"
        )
        String phone,

        @NotNull
        @Size(min = 6)
        String password,

        @NotNull
        @Size(min = 11, max = 11)
        String cpf,

        @NotNull
        UserProfile profile
) {}
