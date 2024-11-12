package com.medici.user_hub.controller;

import com.medici.user_hub.dto.UserDTO;
import com.medici.user_hub.model.User;
import com.medici.user_hub.model.Role;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for user registration, login, and account management.
 * Provides endpoints for creating, updating, deleting, and fetching user accounts.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Register a new user with a default USER role.
     * @param user The user to be registered.
     * @return A JWT token if registration is successful.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            String token = userService.registerUser(user);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Login a user by validating their credentials.
     * @param email The user's email.
     * @param password The user's password.
     * @return User details and JWT token if login is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userService.validatePassword(password, user.getPasswordHash())) {
                // Generate token
                String token = jwtService.generateAccessToken(user);

                // Create UserDTO to send user details
                UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                );

                // Include token in response header
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);

                // Return user information and token
                return ResponseEntity.ok().headers(headers).body(userDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    /**
     * Update a user's profile information.
     * @param updatedUser The updated user data.
     * @param token The JWT token for authorization.
     * @return Success message if update is successful.
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User updatedUser, @RequestHeader("Authorization") String token) {
        try {
            String userId = jwtService.validateTokenAndGetUserId(token.replace("Bearer ", ""));
            if (userId != null) {
                userService.updateUser(userId, updatedUser);
                return ResponseEntity.ok("User updated successfully");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    /**
     * Delete a user's account.
     * @param token The JWT token for authorization.
     * @return Success message if deletion is successful.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtService.validateTokenAndGetUserId(token.replace("Bearer ", ""));
            if (userId != null) {
                userService.deleteUser(userId);
                return ResponseEntity.ok("User account deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }

    /**
     * Fetch all users.
     * @return A list of all users in the system.
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
}
