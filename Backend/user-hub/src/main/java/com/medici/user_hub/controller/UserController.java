package com.medici.user_hub.controller;

import com.medici.user_hub.model.User;
import com.medici.user_hub.model.Role;
import com.medici.user_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * REST controller for user registration and role-based registration.
 * Provides endpoints for creating regular users and administrators.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register a new user with a default USER role
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            // Initialize roles and assign USER role to the new user
            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER);
            user.setRoles(roles);

            // Register the user and generate a JWT token
            String token = userService.registerUser(user);
            return ResponseEntity.ok(token); // Return the generated token on successful registration
        } catch (IllegalArgumentException e) {
            // Return a bad request if registration fails (e.g., email already in use)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Register a new administrator with both USER and ADMIN roles
    @PostMapping("/registerAdmin")
    public ResponseEntity<String> registerAdmin(@RequestBody User user) {
        try {
            // Initialize roles and assign both USER and ADMIN roles to the new admin
            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER);
            roles.add(Role.ADMIN);
            user.setRoles(roles);

            // Register the admin and generate a JWT token
            String token = userService.registerUser(user);
            return ResponseEntity.ok(token); // Return the generated token on successful registration
        } catch (IllegalArgumentException e) {
            // Return a bad request if registration fails (e.g., email already in use)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
