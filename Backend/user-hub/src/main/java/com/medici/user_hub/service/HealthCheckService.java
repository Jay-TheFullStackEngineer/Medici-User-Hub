package com.medici.user_hub.service;

import com.medici.user_hub.handler.DatabaseException;
import com.medici.user_hub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to perform health checks for the application.
 * Provides methods to check the health of the service layer and database connection.
 */
@Service
public class HealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    @Autowired
    private UserRepository userRepository;

    // Check if the service layer is healthy by verifying repository access
    public boolean isServiceHealthy() {
        logger.info("HealthCheckService - Performing service layer health check");

        try {
            userRepository.count(); // Ensure repository access by performing a simple count query
            logger.info("HealthCheckService - Service layer is healthy");
            return true;
        } catch (Exception ex) {
            logger.error("HealthCheckService - Service layer health check failed", ex);
            throw new DatabaseException("Service layer health check failed", ex);
        }
    }

    // Check if the database is healthy by testing if a connection can be established
    public boolean isDatabaseHealthy() {
        logger.info("HealthCheckService - Performing database health check");

        try {
            userRepository.existsByEmail("test@example.com"); // Execute a simple query to check connectivity
            logger.info("HealthCheckService - Database connection is healthy");
            return true;
        } catch (Exception ex) {
            logger.error("HealthCheckService - Database health check failed", ex);
            throw new DatabaseException("Database health check failed", ex);
        }
    }
}
