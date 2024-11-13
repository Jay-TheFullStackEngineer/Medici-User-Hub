package com.medici.user_hub.service;

import com.medici.user_hub.handler.DatabaseException;
import com.medici.user_hub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HealthCheckService healthCheckService;

    @BeforeEach
    void setUp() {
        // No additional setup needed as we're using MockitoExtension
    }

    @Test
    void isServiceHealthy_whenRepositoryIsAccessible_returnsTrue() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);

        // Act
        boolean result = healthCheckService.isServiceHealthy();

        // Assert
        assertTrue(result);
        verify(userRepository).count();
    }

    @Test
    void isServiceHealthy_whenRepositoryThrowsException_throwsDatabaseException() {
        // Arrange
        when(userRepository.count()).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> healthCheckService.isServiceHealthy());

        assertEquals("Service layer health check failed", exception.getMessage());
        verify(userRepository).count();
    }

    @Test
    void isDatabaseHealthy_whenDatabaseIsConnected_returnsTrue() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        boolean result = healthCheckService.isDatabaseHealthy();

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void isDatabaseHealthy_whenDatabaseIsNotConnected_throwsDatabaseException() {
        // Arrange
        when(userRepository.existsByEmail(anyString()))
                .thenThrow(new DataAccessException("Connection failed") {});

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> healthCheckService.isDatabaseHealthy());

        assertEquals("Database health check failed", exception.getMessage());
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void isServiceHealthy_whenRepositoryThrowsNull_throwsDatabaseException() {
        // Arrange
        when(userRepository.count()).thenThrow(new NullPointerException());

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> healthCheckService.isServiceHealthy());

        assertEquals("Service layer health check failed", exception.getMessage());
        verify(userRepository).count();
    }

    @Test
    void isDatabaseHealthy_whenDatabaseThrowsRuntimeException_throwsDatabaseException() {
        // Arrange
        when(userRepository.existsByEmail(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> healthCheckService.isDatabaseHealthy());

        assertEquals("Database health check failed", exception.getMessage());
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void isServiceHealthy_ensureLoggingDoesNotAffectResult() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);

        // Act
        boolean result = healthCheckService.isServiceHealthy();

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).count();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void isDatabaseHealthy_ensureLoggingDoesNotAffectResult() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Act
        boolean result = healthCheckService.isDatabaseHealthy();

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }
}