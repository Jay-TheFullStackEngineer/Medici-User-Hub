package com.medici.user_hub.controller;

import com.medici.user_hub.model.User;
import com.medici.user_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user data by administrators.
 * Provides endpoints for CRUD operations on users, allowing admins to view, update, delete, and register users.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // Retrieve all users in the system
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update an existing user's details by their ID
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userUpdates) {
        // Call the service to update the user with the given ID and updated details
        User updatedUser = userService.updateUser(id, userUpdates);
        return ResponseEntity.ok(updatedUser); // Return the updated user
    }

    // Delete a user from the system by their ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id); // Call the service to delete the user
        return ResponseEntity.noContent().build(); // Return a no-content response on successful deletion
    }

    // Register a new user and return their authentication token
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        // Register the user and generate a JWT token for authentication
        String token = userService.registerUser(user);
        return ResponseEntity.ok(token); // Return the generated token
    }
}
