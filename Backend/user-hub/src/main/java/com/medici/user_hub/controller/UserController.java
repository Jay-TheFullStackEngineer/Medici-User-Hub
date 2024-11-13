package com.medici.user_hub.controller;

import com.medici.user_hub.dto.UserDTO;
import com.medici.user_hub.model.User;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.UserService;
import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.handler.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        logger.info("UserController - Request to register a new user with email: {}", user.getEmail());
        try {
            String token = userService.registerUser(user);
            logger.info("UserController - Successfully registered new user with email: {}", user.getEmail());
            return ResponseEntity.ok(token);
        } catch (ValidationException e) {
            logger.error("UserController - Validation error during registration for email: {}", user.getEmail(), e);
            throw e;
        } catch (Exception e) {
            logger.error("UserController - Unexpected error during registration for email: {}", user.getEmail(), e);
            throw e;
        }
    }

    // Login a user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        logger.info("UserController - Request to login user with email: {}", email);
        try {
            User user = userService.getUserByEmail(email); // Throws ResourceNotFoundException if not found
            if (userService.validatePassword(password, user.getPasswordHash())) {
                String token = jwtService.generateAccessToken(user);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);

                UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getSecurityQuestion(),
                        user.getRoles(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                );

                logger.info("UserController - User with email: {} logged in successfully", email);
                return ResponseEntity.ok().headers(headers).body(userDTO);
            } else {
                logger.warn("UserController - Invalid password for email: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }
        } catch (ResourceNotFoundException e) {
            logger.error("UserController - User not found for login with email: {}", email, e);
            throw e;
        } catch (Exception e) {
            logger.error("UserController - Unexpected error during login for email: {}", email, e);
            throw e;
        }
    }

    // Update user profile (authenticated users only)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User updatedUser, @RequestHeader("Authorization") String token) {
        String userId = jwtService.validateTokenAndGetUserId(token.replace("Bearer ", ""));
        logger.info("UserController - Request to update profile for user ID: {}", userId);
        try {
            userService.updateUser(userId, updatedUser); // Throws exceptions if validation or not found
            logger.info("UserController - Successfully updated profile for user ID: {}", userId);
            return ResponseEntity.ok("User updated successfully");
        } catch (ResourceNotFoundException | ValidationException e) {
            logger.error("UserController - Error updating profile for user ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("UserController - Unexpected error updating profile for user ID: {}", userId, e);
            throw e;
        }
    }

    // Delete user account (authenticated users only)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
        String userId = jwtService.validateTokenAndGetUserId(token.replace("Bearer ", ""));
        logger.info("UserController - Request to delete account for user ID: {}", userId);
        try {
            userService.deleteUser(userId); // Throws ResourceNotFoundException if not found
            logger.info("UserController - Successfully deleted account for user ID: {}", userId);
            return ResponseEntity.ok("User account deleted successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("UserController - User not found for deletion with ID: {}", userId, e);
            throw e;
        } catch (Exception e) {
            logger.error("UserController - Unexpected error during deletion for user ID: {}", userId, e);
            throw e;
        }
    }

    // Fetch all users (admin only)
    @Secured("ROLE_ADMIN")
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("UserController - Request to retrieve all users");
        try {
            List<UserDTO> userDTOs = userService.getAllUsers().stream()
                    .map(user -> new UserDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getSecurityQuestion(),
                            user.getRoles(),
                            user.getCreatedAt(),
                            user.getUpdatedAt()
                    ))
                    .collect(Collectors.toList());
            logger.info("UserController - Successfully retrieved {} users", userDTOs.size());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            logger.error("UserController - Error retrieving all users", e);
            throw e;
        }
    }
}
