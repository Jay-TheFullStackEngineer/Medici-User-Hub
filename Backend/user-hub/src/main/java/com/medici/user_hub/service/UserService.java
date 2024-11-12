package com.medici.user_hub.service;

import com.medici.user_hub.model.User;
import com.medici.user_hub.model.Role;
import com.medici.user_hub.repository.UserRepository;
import com.medici.user_hub.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user operations including registration, retrieval, updates, deletion,
 * and role-based access. Handles user authentication through JWT generation.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register a new user, hash their password, and generate a JWT token
    public String registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use"); // Ensure email is unique
        }
        // Encrypt the user's password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // Save the user in the database with auto-generated ID
        userRepository.save(user);
        // Generate and return a JWT token for user authentication
        return jwtService.generateAccessToken(user);
    }

    // Retrieve a user by their unique ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Update an existing user's details by ID, rehash the password, and update the timestamp
    public User updateUser(String id, User userUpdates) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setUsername(userUpdates.getUsername()); // Update username
            existingUser.setEmail(userUpdates.getEmail()); // Update email
            existingUser.setPasswordHash(passwordEncoder.encode(userUpdates.getPasswordHash())); // Rehash new password
            existingUser.setRoles(userUpdates.getRoles()); // Update roles
            existingUser.setUpdatedAt(LocalDateTime.now()); // Set update timestamp
            return userRepository.save(existingUser); // Save updated user
        } else {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
    }

    // Delete a user by their unique ID
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Retrieve all users in the system
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve a user by their email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Check if a user has an ADMIN role
    public boolean isAdmin(User user) {
        return user.getRoles() != null && user.getRoles().contains(Role.ADMIN);
    }

    // Add this method in UserService
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
