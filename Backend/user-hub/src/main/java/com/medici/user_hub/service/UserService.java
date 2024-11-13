package com.medici.user_hub.service;

import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.handler.DatabaseException;
import com.medici.user_hub.handler.ValidationException;
import com.medici.user_hub.model.User;
import com.medici.user_hub.model.Role;
import com.medici.user_hub.repository.UserRepository;
import com.medici.user_hub.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

/**
 * Service class for handling User-related operations.
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new user with validation and secure password hashing.
     */
    public String registerUser(User user) {
        logger.debug("Attempting to register new user with email: {}", user.getEmail());

        // Validate user input
        validateUserFields(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Registration failed - Email already exists: {}", user.getEmail());
            throw new ValidationException("Email already in use");
        }

        try {
            // Hash sensitive data
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            if (user.getSecurityAnswerHash() != null) {
                user.setSecurityAnswerHash(passwordEncoder.encode(user.getSecurityAnswerHash()));
            }

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            // Set default role if none provided
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(Collections.singleton(Role.USER));
            }

            userRepository.save(user);
            logger.info("User registered successfully with email: {}", user.getEmail());
            return "User registered successfully";
        } catch (Exception ex) {
            logger.error("Failed to register user", ex);
            throw new DatabaseException("Failed to register user in the database", ex);
        }
    }

    /**
     * Retrieves a user by their ID.
     */
    public User getUserById(String id) {
        logger.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });
    }

    /**
     * Updates an existing user's information.
     */
    public User updateUser(String id, User userUpdates) {
        logger.debug("Attempting to update user with ID: {}", id);

        User existingUser = getUserById(id);
        validateUserFields(userUpdates);

        try {
            // Update basic information
            existingUser.setUsername(userUpdates.getUsername());
            existingUser.setEmail(userUpdates.getEmail());

            // Update password if provided
            if (userUpdates.getPasswordHash() != null) {
                existingUser.setPasswordHash(passwordEncoder.encode(userUpdates.getPasswordHash()));
            }

            // Update security question and answer if provided
            if (userUpdates.getSecurityQuestion() != null) {
                existingUser.setSecurityQuestion(userUpdates.getSecurityQuestion());
            }
            if (userUpdates.getSecurityAnswerHash() != null) {
                existingUser.setSecurityAnswerHash(passwordEncoder.encode(userUpdates.getSecurityAnswerHash()));
            }

            // Update roles if provided
            if (userUpdates.getRoles() != null && !userUpdates.getRoles().isEmpty()) {
                existingUser.setRoles(new HashSet<>(userUpdates.getRoles()));
            }

            existingUser.setUpdatedAt(LocalDateTime.now());

            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully: {}", id);
            return updatedUser;
        } catch (Exception ex) {
            logger.error("Failed to update user", ex);
            throw new DatabaseException("Failed to update user with ID: " + id, ex);
        }
    }

    /**
     * Retrieves a user by their email address.
     */
    public User getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
    }

    /**
     * Retrieves a user's security question by their email.
     */
    public String getSecurityQuestionByEmail(String email) {
        logger.debug("Fetching security question for email: {}", email);
        User user = getUserByEmail(email);
        return user.getSecurityQuestion();
    }

    /**
     * Verifies security answer and resets password.
     */
    public boolean verifySecurityAnswerAndResetPassword(String email, String answer, String newPassword) {
        logger.debug("Attempting to verify security answer and reset password for email: {}", email);

        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(answer, user.getSecurityAnswerHash())) {
            logger.warn("Invalid security answer attempt for email: {}", email);
            throw new ValidationException("Incorrect security answer");
        }

        try {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logger.info("Password reset successful for user: {}", email);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to reset password", ex);
            throw new DatabaseException("Failed to reset password for user: " + email, ex);
        }
    }

    /**
     * Deletes a user by their ID.
     */
    public void deleteUser(String id) {
        logger.debug("Attempting to delete user with ID: {}", id);

        User user = getUserById(id);
        try {
            userRepository.delete(user);
            logger.info("User deleted successfully: {}", id);
        } catch (Exception ex) {
            logger.error("Failed to delete user", ex);
            throw new DatabaseException("Failed to delete user with ID: " + id, ex);
        }
    }

    /**
     * Retrieves all users from the database.
     */
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            logger.info("Retrieved {} users", users.size());
            return users;
        } catch (Exception ex) {
            logger.error("Failed to retrieve users", ex);
            throw new DatabaseException("Failed to retrieve users from database", ex);
        }
    }

    /**
     * Checks if a user has admin privileges.
     */
    public boolean isAdmin(User user) {
        return user.getRoles() != null && user.getRoles().contains(Role.ADMIN);
    }

    /**
     * Validates a raw password against an encoded password.
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Implementation of UserDetailsService for Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        logger.debug("Loading user details for ID: {}", userId);
        return userRepository.findById(userId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("User not found with ID: " + userId);
                });
    }

    /**
     * Protected getter for password encoder (used in tests).
     */
    protected BCryptPasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    /**
     * Validates user fields for registration and updates.
     */
    private void validateUserFields(User user) {
        if (user.getEmail() == null || !user.getEmail().matches(EMAIL_REGEX)) {
            logger.warn("Invalid email format: {}", user.getEmail());
            throw new ValidationException("Invalid email format: " + user.getEmail());
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            logger.warn("Empty username provided");
            throw new ValidationException("Username cannot be null or empty");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            logger.warn("Empty password provided");
            throw new ValidationException("Password cannot be null or empty");
        }
    }
}