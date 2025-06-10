package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.EmailAlreadyRegisteredException;
import com.restaurant.restaurantManagement.exception.OperationNotAllowedException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.user.CreateUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.GetUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.UpdateUserDTO;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.model.mapper.UserMapper;
import com.restaurant.restaurantManagement.repository.AuthorityRepository;
import com.restaurant.restaurantManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public List<GetUserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toGetUserDTO).toList();
    }

    public GetUserDTO getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toGetUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public GetUserDTO createUser(CreateUserDTO userDTO) {
        validateUserEmail(userDTO.email());
        var userMapped = userMapper.toUser(userDTO);

        AssignUserAuthority(userMapped);
        userMapped.setPassword(passwordEncoder.encode(userMapped.getPassword()));
        var userSaved = userRepository.save(userMapped);

        return userMapper.toGetUserDTO(userSaved);
    }

    public GetUserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        user.setName(updateUserDTO.name());
        user.setEmail(updateUserDTO.email());
        user.setPhone(updateUserDTO.phone());
        user.setPassword(updateUserDTO.password());
        user.setProfile(updateUserDTO.profile());
        user.setAuthorities(updateUserDTO.authority());

        var updatedUser = userRepository.save(user);
        return userMapper.toGetUserDTO(updatedUser);
    }

    public void deleteUser(Long idToDelete, String userEmail) {
        var loggedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        if (idToDelete.equals(loggedUser.getId())) {
            throw new OperationNotAllowedException("You cannot exclude your own account!");
        }

        var userToDelete = userRepository.findById(idToDelete)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + idToDelete));

        userRepository.delete(userToDelete);
    }

    public List<GetUserDTO> searchUsersByFilters(String name, String email, String phone, String cpf, UserProfile profile) {
        return userRepository.findByFilters(email, cpf, name, phone, profile).stream().map(userMapper::toGetUserDTO).toList();
    }

    private void AssignUserAuthority(User user) {
        if (user.getProfile().equals(UserProfile.OWNER)) {
            var ownerAuthority = authorityRepository.findByName("ROLE_OWNER")
                    .orElseThrow(() -> new ResourceNotFoundException("Authority not found"));
            user.setAuthorities(List.of(ownerAuthority));
        } else {
            var staffAuthority = authorityRepository.findByName("ROLE_STAFF")
                    .orElseThrow(() -> new ResourceNotFoundException("Authority not found"));
            user.setAuthorities(List.of(staffAuthority));
        }

    }

    private void validateUserEmail(String email) {
        var userEmail = userRepository.findByEmail(email);
        if (userEmail.isPresent()) {
            throw new EmailAlreadyRegisteredException("User already registered: " + email);
        }
    }

}
