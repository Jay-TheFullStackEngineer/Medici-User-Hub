package com.medici.user_hub.controller;

import com.medici.user_hub.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for health check endpoints.
 * Provides endpoints to check the health of the application, service layer, and database.
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private HealthCheckService healthCheckService;

    // Checks if the application is running
    @GetMapping("/app")
    public ResponseEntity<String> checkAppHealth() {
        return ResponseEntity.ok("Application is running"); // Return 200 OK if the app is up
    }

    // Checks if the service layer is responding
    @GetMapping("/service")
    public ResponseEntity<String> checkServiceHealth() {
        // Check if the service layer is responsive
        boolean isServiceHealthy = healthCheckService.isServiceHealthy();
        if (isServiceHealthy) {
            return ResponseEntity.ok("Service layer is healthy"); // Return 200 OK if healthy
        } else {
            return ResponseEntity.status(503).body("Service layer is not responding"); // Return 503 if unavailable
        }
    }

    // Checks if the database connection is active and responsive
    @GetMapping("/db")
    public ResponseEntity<String> checkDatabaseHealth() {
        // Check if the database is reachable
        boolean isDbHealthy = healthCheckService.isDatabaseHealthy();
        if (isDbHealthy) {
            return ResponseEntity.ok("Database connection is healthy"); // Return 200 OK if database is responsive
        } else {
            return ResponseEntity.status(503).body("Database connection failed"); // Return 503 if connection fails
        }
    }
}
