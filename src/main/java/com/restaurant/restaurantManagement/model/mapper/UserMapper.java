package com.restaurant.restaurantManagement.model.mapper;

import com.restaurant.restaurantManagement.model.dto.user.CreateUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.GetUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.UpdateUserDTO;
import com.restaurant.restaurantManagement.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(CreateUserDTO userDTO) {
        return User.builder()
                .name(userDTO.name())
                .email(userDTO.email())
                .phone(userDTO.phone())
                .cpf(userDTO.cpf())
                .password(userDTO.password())
                .profile(userDTO.profile())
                .build();
    }

    public GetUserDTO toGetUserDTO(User user) {
        return GetUserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .cpf(user.getCpf())
                .profile(user.getProfile())
                .build();
    }

    public User updateFromDTO(UpdateUserDTO updateUserDTO, User existingUser) {
        return User.builder()
                .id(existingUser.getId())
                .email(updateUserDTO.email() != null ? updateUserDTO.email() : existingUser.getEmail())
                .name(updateUserDTO.name() != null ? updateUserDTO.name() : existingUser.getName())
                .phone(updateUserDTO.phone() != null ? updateUserDTO.phone() : existingUser.getPhone())
                .password(updateUserDTO.password() != null ? updateUserDTO.password() : existingUser.getPassword())
                .profile(updateUserDTO.profile() != null ? updateUserDTO.profile() : existingUser.getProfile())
                .authorities(updateUserDTO.authority() != null ? updateUserDTO.authority() : existingUser.getAuthorities())
                .build();
    }
}
