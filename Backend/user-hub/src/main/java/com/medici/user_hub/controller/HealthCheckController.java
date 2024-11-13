package com.medici.user_hub.controller;

import com.medici.user_hub.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @Autowired
    private HealthCheckService healthCheckService;

    // Application health check (public)
    @GetMapping("/app")
    public ResponseEntity<String> checkAppHealth() {
        logger.info("HealthCheckController - Checking application health");
        try {
            // Basic application health check - return OK if running
            String message = "Application is running";
            logger.info("HealthCheckController - Application health check successful");
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.error("HealthCheckController - Application health check failed", e);
            return ResponseEntity.status(503).body("Application health check failed");
        }
    }

    // Service layer health check (public)
    @GetMapping("/service")
    public ResponseEntity<String> checkServiceHealth() {
        logger.info("HealthCheckController - Checking service layer health");
        try {
            boolean isServiceHealthy = healthCheckService.isServiceHealthy();
            if (isServiceHealthy) {
                logger.info("HealthCheckController - Service layer health check successful");
                return ResponseEntity.ok("Service layer is healthy");
            } else {
                logger.warn("HealthCheckController - Service layer is not responding");
                return ResponseEntity.status(503).body("Service layer is not responding");
            }
        } catch (Exception e) {
            logger.error("HealthCheckController - Error during service layer health check", e);
            return ResponseEntity.status(503).body("Service layer health check failed");
        }
    }

    // Database health check (public)
    @GetMapping("/db")
    public ResponseEntity<String> checkDatabaseHealth() {
        logger.info("HealthCheckController - Checking database health");
        try {
            boolean isDbHealthy = healthCheckService.isDatabaseHealthy();
            if (isDbHealthy) {
                logger.info("HealthCheckController - Database health check successful");
                return ResponseEntity.ok("Database connection is healthy");
            } else {
                logger.warn("HealthCheckController - Database connection failed");
                return ResponseEntity.status(503).body("Database connection failed");
            }
        } catch (Exception e) {
            logger.error("HealthCheckController - Error during database health check", e);
            return ResponseEntity.status(503).body("Database health check failed");
        }
    }
}
