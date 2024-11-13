package com.medici.user_hub.controller;

import com.medici.user_hub.model.User;
import com.medici.user_hub.service.UserService;
import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.handler.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId("1");
        user1.setEmail("user1@example.com");
        user1.setPasswordHash("password1");
        user1.setUsername("User 1");

        user2 = new User();
        user2.setId("2");
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("password2");
        user2.setUsername("User 2");
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<User>> response = adminController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_ThrowsException_ReturnsInternalServerError() {
        // Arrange
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> adminController.getAllUsers());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ReturnsUpdatedUser() {
        // Arrange
        String userId = "1";

        User userUpdates = new User();
        userUpdates.setEmail("updated@example.com");
        userUpdates.setPasswordHash("newpassword");
        userUpdates.setUsername("Updated User");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail(userUpdates.getEmail());
        updatedUser.setPasswordHash(userUpdates.getPasswordHash());
        updatedUser.setUsername(userUpdates.getUsername());

        when(userService.updateUser(userId, userUpdates)).thenReturn(updatedUser);

        // Act
        ResponseEntity<User> response = adminController.updateUser(userId, userUpdates);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1)).updateUser(userId, userUpdates);
    }

    @Test
    void updateUser_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String userId = "1";
        User userUpdates = new User();
        userUpdates.setEmail("updated@example.com");
        userUpdates.setPasswordHash("newpassword");
        userUpdates.setUsername("Updated User");
        when(userService.updateUser(userId, userUpdates)).thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> adminController.updateUser(userId, userUpdates));
        verify(userService, times(1)).updateUser(userId, userUpdates);
    }


    @Test
    void updateUser_InvalidUserData_ThrowsValidationException() {
        // Arrange
        String userId = "1";
        User userUpdates = new User();
        userUpdates.setEmail("");
        userUpdates.setPasswordHash("newpassword");
        userUpdates.setUsername("Updated User");
        when(userService.updateUser(userId, userUpdates)).thenThrow(new ValidationException("Email is required"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminController.updateUser(userId, userUpdates));
        verify(userService, times(1)).updateUser(userId, userUpdates);
    }

    @Test
    void deleteUser_DeletesUser() {
        // Arrange
        String userId = "1";

        // Act
        ResponseEntity<Void> response = adminController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String userId = "1";
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(userId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> adminController.deleteUser(userId));
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void createUser_ReturnsAuthToken() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setPasswordHash("password");
        newUser.setUsername("New User");
        String expectedToken = "generatedAuthToken";
        when(userService.registerUser(newUser)).thenReturn(expectedToken);

        // Act
        ResponseEntity<String> response = adminController.createUser(newUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(userService, times(1)).registerUser(newUser);
    }

    @Test
    void createUser_InvalidUserData_ThrowsValidationException() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("");
        newUser.setPasswordHash("password");
        newUser.setUsername("New User");
        when(userService.registerUser(newUser)).thenThrow(new ValidationException("Email is required"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminController.createUser(newUser));
        verify(userService, times(1)).registerUser(newUser);
    }
}