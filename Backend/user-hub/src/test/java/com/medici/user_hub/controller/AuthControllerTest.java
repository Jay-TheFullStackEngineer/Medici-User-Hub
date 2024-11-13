package com.medici.user_hub.controller;

import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.model.User;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.TokenService;
import com.medici.user_hub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private User user;
    private String refreshToken;
    private String accessToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setEmail("user@example.com");
        user.setPasswordHash("password");

        refreshToken = "validRefreshToken";
        accessToken = "validAccessToken";
    }

    @Test
    void refreshAccessToken_ValidRefreshToken_ReturnsNewAccessToken() {
        // Arrange
        String newAccessToken = "newAccessToken";
        when(jwtService.validateTokenAndGetUserId(refreshToken)).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);

        // Act
        ResponseEntity<String> response = authController.refreshAccessToken(refreshToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newAccessToken, response.getBody());
        verify(jwtService, times(1)).validateTokenAndGetUserId(refreshToken);
        verify(userService, times(1)).getUserById(user.getId());
        verify(jwtService, times(1)).generateAccessToken(user);
    }

    @Test
    void refreshAccessToken_InvalidRefreshToken_ReturnsUnauthorized() {
        // Arrange
        when(jwtService.validateTokenAndGetUserId(refreshToken)).thenReturn(null);

        // Act
        ResponseEntity<String> response = authController.refreshAccessToken(refreshToken);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid refresh token", response.getBody());
        verify(jwtService, times(1)).validateTokenAndGetUserId(refreshToken);
        verify(userService, never()).getUserById(anyString());
        verify(jwtService, never()).generateAccessToken(any(User.class));
    }

    @Test
    void refreshAccessToken_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(jwtService.validateTokenAndGetUserId(refreshToken)).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authController.refreshAccessToken(refreshToken));
        verify(jwtService, times(1)).validateTokenAndGetUserId(refreshToken);
        verify(userService, times(1)).getUserById(user.getId());
        verify(jwtService, never()).generateAccessToken(any(User.class));
    }

    @Test
    void logout_ValidAccessToken_ReturnsSuccess() {
        // Arrange
        long expiration = System.currentTimeMillis() + 3600000; // 1 hour from now
        when(jwtService.getTokenExpiration(accessToken)).thenReturn(expiration);

        // Act
        ResponseEntity<String> response = authController.logout(accessToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        verify(jwtService, times(1)).getTokenExpiration(accessToken);
        verify(tokenService, times(1)).blacklistToken(accessToken, expiration);
    }

    @Test
    void logout_FailedBlacklisting_ReturnsInternalServerError() {
        // Arrange
        long expiration = System.currentTimeMillis() + 3600000; // 1 hour from now
        when(jwtService.getTokenExpiration(accessToken)).thenReturn(expiration);
        doThrow(new RuntimeException("Failed to blacklist token")).when(tokenService).blacklistToken(accessToken, expiration);

        // Act
        ResponseEntity<String> response = authController.logout(accessToken);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to logout", response.getBody());
        verify(jwtService, times(1)).getTokenExpiration(accessToken);
        verify(tokenService, times(1)).blacklistToken(accessToken, expiration);
    }

    @Test
    void getSecurityQuestion_ValidEmail_ReturnsSecurityQuestion() {
        // Arrange
        String email = "user@example.com";
        String securityQuestion = "What is your favorite color?";
        when(userService.getSecurityQuestionByEmail(email)).thenReturn(securityQuestion);

        // Act
        ResponseEntity<String> response = authController.getSecurityQuestion(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(securityQuestion, response.getBody());
        verify(userService, times(1)).getSecurityQuestionByEmail(email);
    }

    @Test
    void getSecurityQuestion_EmailNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userService.getSecurityQuestionByEmail(email)).thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authController.getSecurityQuestion(email));
        verify(userService, times(1)).getSecurityQuestionByEmail(email);
    }

    @Test
    void resetPassword_ValidEmailAndAnswer_ReturnsSuccess() {
        // Arrange
        String email = "user@example.com";
        String answer = "blue";
        String newPassword = "newPassword123";
        when(userService.verifySecurityAnswerAndResetPassword(email, answer, newPassword)).thenReturn(true);

        // Act
        ResponseEntity<String> response = authController.resetPassword(email, answer, newPassword);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successful", response.getBody());
        verify(userService, times(1)).verifySecurityAnswerAndResetPassword(email, answer, newPassword);
    }

    @Test
    void resetPassword_InvalidEmailOrAnswer_ReturnsBadRequest() {
        // Arrange
        String email = "user@example.com";
        String answer = "wrongAnswer";
        String newPassword = "newPassword123";
        when(userService.verifySecurityAnswerAndResetPassword(email, answer, newPassword)).thenReturn(false);

        // Act
        ResponseEntity<String> response = authController.resetPassword(email, answer, newPassword);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password reset failed", response.getBody());
        verify(userService, times(1)).verifySecurityAnswerAndResetPassword(email, answer, newPassword);
    }

    @Test
    void resetPassword_EmailNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        String email = "nonexistent@example.com";
        String answer = "blue";
        String newPassword = "newPassword123";
        when(userService.verifySecurityAnswerAndResetPassword(email, answer, newPassword))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authController.resetPassword(email, answer, newPassword));
        verify(userService, times(1)).verifySecurityAnswerAndResetPassword(email, answer, newPassword);
    }
}