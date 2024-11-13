package com.medici.user_hub.service;

import com.medici.user_hub.handler.DatabaseException;
import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.handler.ValidationException;
import com.medici.user_hub.model.User;
import com.medici.user_hub.model.Role;
import com.medici.user_hub.repository.UserRepository;
import com.medici.user_hub.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("123");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setPasswordHash("password123"); // Raw password
        testUser.setSecurityAnswerHash("securityAnswer"); // Raw answer
        testUser.setSecurityQuestion("What is your pet's name?");
        testUser.setRoles(Collections.singleton(Role.USER));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        String result = userService.registerUser(testUser);

        // Assert
        assertThat(result).isEqualTo("User registered successfully");
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenRegisteringWithInvalidEmail() {
        // Arrange
        testUser.setEmail("invalid-email");

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(testUser))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid email format: invalid-email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Arrange
        when(userRepository.findById("123")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById("123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("123");
        verify(userRepository).findById("123");
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        // Arrange
        List<User> users = Collections.singletonList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("123");
        verify(userRepository).findAll();
    }

    @Test
    void shouldGetUserByEmailSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByEmail(testUser.getEmail());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void shouldGetSecurityQuestionSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Act
        String result = userService.getSecurityQuestionByEmail(testUser.getEmail());

        // Assert
        assertThat(result).isEqualTo(testUser.getSecurityQuestion());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void shouldVerifyAdminRoleCorrectly() {
        // Arrange
        testUser.setRoles(Collections.singleton(Role.ADMIN));

        // Act
        boolean result = userService.isAdmin(testUser);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldHandleDatabaseExceptionWhenSaving() {
        // Arrange
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(testUser))
                .isInstanceOf(DatabaseException.class)
                .hasMessage("Failed to register user in the database");
    }

    @Test
    void shouldHandleNullUsername() {
        // Arrange
        testUser.setUsername(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(testUser))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Username cannot be null or empty");
    }

    @Test
    void shouldValidatePasswordCorrectly() {
        // Arrange
        String rawPassword = "password123";
        String encodedPassword = userService.getPasswordEncoder().encode(rawPassword);

        // Act
        boolean result = userService.validatePassword(rawPassword, encodedPassword);

        // Assert
        assertThat(result).isTrue();
    }
}