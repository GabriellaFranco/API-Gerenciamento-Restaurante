package com.restaurant.restaurantManagement.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantManagement.configuration.security.SecurityConfig;
import com.restaurant.restaurantManagement.controller.UserController;
import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.exception.ResourceNotFoundException;
import com.restaurant.restaurantManagement.model.dto.user.CreateUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.GetUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.UpdateUserDTO;
import com.restaurant.restaurantManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private GetUserDTO getUserDTO;
    private GetUserDTO user1;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        getUserDTO = GetUserDTO.builder()
                .id(1L)
                .name("Gabriella")
                .email("dev@teste.com")
                .phone("5547999998888")
                .cpf("13456789012")
                .profile(UserProfile.OWNER)
                .build();

        user1 = new GetUserDTO(1L, "John Doe",
                "john@example.com", "123456789", "123.456.789-00" , UserProfile.OWNER);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void testUserController_WhenGetAllUsers_ShouldReturnAListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(getUserDTO, user1));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gabriella"))
                .andExpect(jsonPath("$[0].email").value("dev@teste.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void testUserController_WhenGetAllUsersWithNoExistingUsers_ShouldReturnStatusNoContent() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void testUserController_WhenGetUserById_ShouldReturnUserObject() throws Exception {
        when(userService.getUserById(1L)).thenReturn(getUserDTO);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gabriella"))
                .andExpect(jsonPath("$.email").value("dev@teste.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"OWNER"})
    void testUserController_WhenGetUserByIdWithNonExistingId_ShouldReturnStatusNotFound() throws Exception {
        when(userService.getUserById(5L)).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(get("/users/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testUserController_WhenCreatingUser_ShouldReturnStatusCreated() throws Exception {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("Novo Usuário")
                .email("novo@teste.com")
                .password("123456")
                .phone("5547999999999")
                .cpf("12345678901")
                .profile(UserProfile.EMPLOYEE)
                .build();

        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(getUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Gabriella"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testUserController_WhenUpdatingUser_ShouldReturnUpdatedUser() throws Exception {
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .name("Gabriella Atualizada")
                .phone("55998887766")
                .build();

        GetUserDTO updatedUserDTO = GetUserDTO.builder()
                .id(1L)
                .name("Gabriella Atualizada")
                .email("dev@teste.com")
                .phone("55998887766")
                .cpf("13456789012")
                .profile(UserProfile.OWNER)
                .build();

        when(userService.updateUser(eq(1L), any(UpdateUserDTO.class))).thenReturn(updatedUserDTO);
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gabriella Atualizada"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testUserController_WhenSearchUsersByFilter_ShouldReturnAListOfFilteredUsers() throws Exception {
        when(userService.searchUsersByFilters("John", "john@example.com", "123.456.789-00", "123456789", UserProfile.OWNER))
                .thenReturn(List.of());

        mockMvc.perform(get("/users/search")
                        .param("name", "John")
                        .param("email", "john@example.com")
                        .param("cpf", "123.456.789-00")
                        .param("phone", "123456789")
                        .param("profile", "OWNER"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    @WithMockUser(username = "dev@teste.com", roles = {"OWNER"})
    void testUserController_WhenSearchUserByFilterWithOnlyName_ShouldReturnAListOfUsers() throws Exception {
        when(userService.searchUsersByFilters("John", null, null, null, null))
                .thenReturn(List.of(user1));

        mockMvc.perform(get("/users/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }
}
