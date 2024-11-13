package com.medici.user_hub.controller;

import com.medici.user_hub.model.User;
import com.medici.user_hub.service.UserService;
import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.handler.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    // Retrieve all users in the system (ADMIN only)
    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("AdminController - Request to retrieve all users");
        try {
            List<User> users = userService.getAllUsers();
            logger.info("AdminController - Successfully retrieved {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("AdminController - Error retrieving all users", e);
            throw e;
        }
    }

    // Update an existing user's details by their ID (ADMIN only)
    @Secured("ROLE_ADMIN")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userUpdates) {
        logger.info("AdminController - Request to update user with ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, userUpdates);
            logger.info("AdminController - Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException | ValidationException e) {
            logger.error("AdminController - Error updating user with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("AdminController - Unexpected error updating user with ID: {}", id, e);
            throw e;
        }
    }

    // Delete a user from the system by their ID (ADMIN only)
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        logger.info("AdminController - Request to delete user with ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("AdminController - Successfully deleted user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            logger.error("AdminController - User not found for ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("AdminController - Unexpected error deleting user with ID: {}", id, e);
            throw e;
        }
    }

    // Register a new user and return their authentication token (ADMIN only)
    @Secured("ROLE_ADMIN")
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        logger.info("AdminController - Request to register new user with email: {}", user.getEmail());
        try {
            String token = userService.registerUser(user);
            logger.info("AdminController - Successfully registered new user with email: {}", user.getEmail());
            return ResponseEntity.ok(token);
        } catch (ValidationException e) {
            logger.error("AdminController - Validation error registering user with email: {}", user.getEmail(), e);
            throw e;
        } catch (Exception e) {
            logger.error("AdminController - Unexpected error registering user with email: {}", user.getEmail(), e);
            throw e;
        }
    }
}
