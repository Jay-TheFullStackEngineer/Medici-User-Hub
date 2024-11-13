package com.medici.user_hub.controller;

import com.medici.user_hub.dto.UserDTO;
import com.medici.user_hub.model.User;
import com.medici.user_hub.service.JwtService;
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
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");

        token = "validToken";
    }

    @Test
    void registerUser_ValidUser_ReturnsToken() {
        // Arrange
        when(userService.registerUser(user)).thenReturn(token);

        // Act
        ResponseEntity<String> response = userController.registerUser(user);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
        verify(userService, times(1)).registerUser(user);
    }

    @Test
    void registerUser_InvalidUser_ThrowsValidationException() {
        // Arrange
        when(userService.registerUser(user)).thenThrow(new ValidationException("Invalid user data"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> userController.registerUser(user));
        verify(userService, times(1)).registerUser(user);
    }

    @Test
    void login_ValidCredentials_ReturnsUserDTO() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(userService.validatePassword(password, user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(token);

        // Act
        ResponseEntity<?> response = userController.login(email, password);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserDTO);
        assertEquals(token, response.getHeaders().get("Authorization").get(0).replace("Bearer ", ""));
        verify(userService, times(1)).getUserByEmail(email);
        verify(userService, times(1)).validatePassword(password, user.getPasswordHash());
        verify(jwtService, times(1)).generateAccessToken(user);
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(userService.validatePassword(password, user.getPasswordHash())).thenReturn(false);

        // Act
        ResponseEntity<?> response = userController.login(email, password);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
        verify(userService, times(1)).getUserByEmail(email);
        verify(userService, times(1)).validatePassword(password, user.getPasswordHash());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void login_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password";
        when(userService.getUserByEmail(email)).thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userController.login(email, password));
        verify(userService, times(1)).getUserByEmail(email);
        verify(userService, never()).validatePassword(anyString(), anyString());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void updateUser_ValidUser_ReturnsSuccess() {
        // Arrange
        String userId = "1";
        when(jwtService.validateTokenAndGetUserId(token)).thenReturn(userId);

        // Act
        ResponseEntity<String> response = userController.updateUser(user, "Bearer " + token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
        verify(jwtService, times(1)).validateTokenAndGetUserId(token);
        verify(userService, times(1)).updateUser(userId, user);
    }

    @Test
    void updateUser_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String userId = "1";
        when(jwtService.validateTokenAndGetUserId(token)).thenReturn(userId);
        doThrow(new ResourceNotFoundException("User not found")).when(userService).updateUser(userId, user);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userController.updateUser(user, "Bearer " + token));
        verify(jwtService, times(1)).validateTokenAndGetUserId(token);
        verify(userService, times(1)).updateUser(userId, user);
    }

    @Test
    void deleteUser_ValidUser_ReturnsSuccess() {
        // Arrange
        String userId = "1";
        when(jwtService.validateTokenAndGetUserId(token)).thenReturn(userId);

        // Act
        ResponseEntity<String> response = userController.deleteUser("Bearer " + token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User account deleted successfully", response.getBody());
        verify(jwtService, times(1)).validateTokenAndGetUserId(token);
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String userId = "1";
        when(jwtService.validateTokenAndGetUserId(token)).thenReturn(userId);
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(userId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userController.deleteUser("Bearer " + token));
        verify(jwtService, times(1)).validateTokenAndGetUserId(token);
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void getAllUsers_ReturnsUserDTOs() {
        // Arrange
        List<User> users = Arrays.asList(user, new User());
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users.size(), response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }
}