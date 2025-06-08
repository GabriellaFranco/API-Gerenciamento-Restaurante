package com.restaurant.restaurantManagement.service;

import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.EmailAlreadyRegisteredException;
import com.restaurant.restaurantManagement.exception.OperationNotAllowedException;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.user.CreateUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.GetUserDTO;
import com.restaurant.restaurantManagement.model.entity.Authority;
import com.restaurant.restaurantManagement.model.entity.User;
import com.restaurant.restaurantManagement.model.mapper.UserMapper;
import com.restaurant.restaurantManagement.repository.AuthorityRepository;
import com.restaurant.restaurantManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Authority authority;
    private CreateUserDTO createUserDTO;
    private GetUserDTO getUserDTO;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@email.com")
                .cpf("12345678910")
                .phone("5547999998769")
                .password("123456")
                .profile(UserProfile.EMPLOYEE)
                .build();

        createUserDTO = CreateUserDTO.builder()
                .name("Alice")
                .email("alice@email.com")
                .cpf("12345678910")
                .phone("5547999998769")
                .password("123456")
                .profile(UserProfile.EMPLOYEE)
                .build();

        getUserDTO = GetUserDTO.builder()
                .id(2L)
                .name("Liana")
                .email("liana@teste.com")
                .phone("5547988706514")
                .cpf("11223344550")
                .profile(UserProfile.EMPLOYEE)
                .build();

        authority = Authority.builder()
                .name("ROLE_STAFF")
                .build();
    }

    @Test
    void testUserService_WhenCreatingAUserWithAnExistingEmail_ShouldThrowEmailAlreadyRegisteredException() {
        when(userRepository.findByEmail(createUserDTO.email())).thenReturn(Optional.of(user));
        assertThrows(EmailAlreadyRegisteredException.class, () -> {
            userService.createUser(createUserDTO);
        });
    }

    @Test
    void testUserService_WhenCreatingAUserWithEmployeeProfile_ShouldAssignStaffAuthority() {
        when(userMapper.toUser(createUserDTO)).thenReturn(user);
        when(authorityRepository.findByName("ROLE_STAFF")).thenReturn(Optional.of(authority));
        user.setProfile(UserProfile.EMPLOYEE);
        userService.createUser(createUserDTO);
        verify(authorityRepository).findByName("ROLE_STAFF");
    }

    @Test
    void testUserService_WhenCreatingAUser_ShouldEncodeUserPassword() {
        when(userMapper.toUser(createUserDTO)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("Password Encoded");
        when(authorityRepository.findByName("ROLE_STAFF")).thenReturn(Optional.of(authority));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);
        when(userMapper.toGetUserDTO(user)).thenReturn(mock(GetUserDTO.class));

        userService.createUser(createUserDTO);
        verify(passwordEncoder).encode(createUserDTO.password());
    }

    @Test
    void testUserService_WhenCreatingAUser_ShouldSaveSuccessfully() {
        when(userRepository.findByEmail(createUserDTO.email()))
                .thenReturn(Optional.empty());

        when(userMapper.toUser(createUserDTO)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(authorityRepository.findByName("ROLE_STAFF"))
                .thenReturn(Optional.of(authority));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toGetUserDTO(user)).thenReturn(getUserDTO);

        GetUserDTO result = userService.createUser(createUserDTO);

        assertNotNull(result);
    }

    @Test
    void testUserService_WhenFindingAllUsers_ShouldReturnAListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<GetUserDTO> result = userService.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testUserService_WhenFindingUserWithInvalidId_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(5L);
        });
    }

    @Test
    void testUserService_WhenTryingToSelfDelete_ShouldThrowOperationNotAllowedException() {
        assertThrows(OperationNotAllowedException.class, () -> {
            userService.deleteUser(1L, 1L);
        });
    }
}
