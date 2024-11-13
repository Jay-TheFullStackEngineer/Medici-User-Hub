package com.medici.user_hub.controller;

import com.medici.user_hub.service.HealthCheckService;
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
public class HealthCheckControllerTest {

    @Mock
    private HealthCheckService healthCheckService;

    @InjectMocks
    private HealthCheckController healthCheckController;

    @BeforeEach
    void setUp() {
        // Any additional setup if needed
    }

    @Test
    void checkAppHealth_ReturnsOk() {
        // Act
        ResponseEntity<String> response = healthCheckController.checkAppHealth();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application is running", response.getBody());
    }

    @Test
    void checkServiceHealth_ServiceHealthy_ReturnsOk() {
        // Arrange
        when(healthCheckService.isServiceHealthy()).thenReturn(true);

        // Act
        ResponseEntity<String> response = healthCheckController.checkServiceHealth();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Service layer is healthy", response.getBody());
        verify(healthCheckService, times(1)).isServiceHealthy();
    }

    @Test
    void checkServiceHealth_ServiceNotResponding_ReturnsServiceUnavailable() {
        // Arrange
        when(healthCheckService.isServiceHealthy()).thenReturn(false);

        // Act
        ResponseEntity<String> response = healthCheckController.checkServiceHealth();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service layer is not responding", response.getBody());
        verify(healthCheckService, times(1)).isServiceHealthy();
    }

    @Test
    void checkServiceHealth_ThrowsException_ReturnsServiceUnavailable() {
        // Arrange
        when(healthCheckService.isServiceHealthy()).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<String> response = healthCheckController.checkServiceHealth();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service layer health check failed", response.getBody());
        verify(healthCheckService, times(1)).isServiceHealthy();
    }

    @Test
    void checkDatabaseHealth_DatabaseHealthy_ReturnsOk() {
        // Arrange
        when(healthCheckService.isDatabaseHealthy()).thenReturn(true);

        // Act
        ResponseEntity<String> response = healthCheckController.checkDatabaseHealth();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Database connection is healthy", response.getBody());
        verify(healthCheckService, times(1)).isDatabaseHealthy();
    }

    @Test
    void checkDatabaseHealth_DatabaseConnectionFailed_ReturnsServiceUnavailable() {
        // Arrange
        when(healthCheckService.isDatabaseHealthy()).thenReturn(false);

        // Act
        ResponseEntity<String> response = healthCheckController.checkDatabaseHealth();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Database connection failed", response.getBody());
        verify(healthCheckService, times(1)).isDatabaseHealthy();
    }

    @Test
    void checkDatabaseHealth_ThrowsException_ReturnsServiceUnavailable() {
        // Arrange
        when(healthCheckService.isDatabaseHealthy()).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<String> response = healthCheckController.checkDatabaseHealth();

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Database health check failed", response.getBody());
        verify(healthCheckService, times(1)).isDatabaseHealthy();
    }
}