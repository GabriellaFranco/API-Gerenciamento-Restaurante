package com.restaurant.restaurantManagement.controller;

import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.model.dto.user.CreateUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.GetUserDTO;
import com.restaurant.restaurantManagement.model.dto.user.UpdateUserDTO;
import com.restaurant.restaurantManagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Returns a list with all existing users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getAllUsers() {
        var users = userService.getAllUsers();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Finds a user that matches the id provided",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GetUserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
            summary = "Creates a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Operation successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid data")
            }
    )
    @PostMapping
    public ResponseEntity<GetUserDTO> createUser(@Valid @RequestBody CreateUserDTO userDTO) {
        var user = userService.createUser(userDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(
            summary = "Updates the user with the provided id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<GetUserDTO> updateUserInformation(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO userDTO) {
        var updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete the user with the informed id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Can't self delete")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(id, userDetails.getUsername());
        return ResponseEntity.ok("User deleted: " + id);
    }

    @Operation(
            summary = "Finds a list of users matching the informed params",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Operation successful"),
                    @ApiResponse(responseCode = "204", description = "No content to show")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<GetUserDTO>> getUserByFilter(@RequestParam(required = false) String email,
                                                            @RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String cpf,
                                                            @RequestParam(required = false) String phone,
                                                            @RequestParam(required = false) UserProfile profile) {

        List<GetUserDTO> users = userService.searchUsersByFilters(name, email, cpf, phone, profile);
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }
}
