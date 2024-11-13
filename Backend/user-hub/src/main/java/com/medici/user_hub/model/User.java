package com.medici.user_hub.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents a User in the application with essential fields for identification,
 * authentication, and authorization.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;  // Unique identifier for the user in MongoDB
    private String username;  // Username of the user, used for display and login
    private String email;  // Email of the user, must be unique
    private String passwordHash;  // Hashed password for secure authentication
    private LocalDateTime createdAt;  // Timestamp for when the user was created
    private LocalDateTime updatedAt;  // Timestamp for the last update to the user's data
    private Set<Role> roles;  // Set of roles assigned to the user (e.g., USER, ADMIN)
    private String securityQuestion; // Security question used for password reset
    private String securityAnswerHash; // Security answer used for password reset

    // Constructor initializes creation and update timestamps
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    // Returns the user's unique ID
    public String getId() {
        return id;
    }

    // Sets the user's unique ID (used by MongoDB)
    public void setId(String id) {
        this.id = id;
    }

    // Returns the username
    public String getUsername() {
        return username;
    }

    // Sets the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Returns the user's email
    public String getEmail() {
        return email;
    }

    // Sets the user's email
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns the user's security question
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    // Gets the user's security question
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    // Returns the hashed password
    public String getPasswordHash() {
        return passwordHash;
    }

    // Sets the hashed password
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Returns the hashed security password
    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    // Sets the hashed security password
    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    // Returns the creation timestamp
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Sets the creation timestamp
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Returns the update timestamp
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Sets the update timestamp
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Returns the roles assigned to the user
    public Set<Role> getRoles() {
        return roles;
    }

    // Sets the roles assigned to the user
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
