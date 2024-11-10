package com.medici.user_hub.service;

import com.medici.user_hub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to perform health checks for the application.
 * Provides methods to check the health of the service layer and database connection.
 */
@Service
public class HealthCheckService {

    @Autowired
    private UserRepository userRepository;

    // Check if the service layer is healthy by verifying repository access
    public boolean isServiceHealthy() {
        try {
            // Perform a simple count query to ensure the repository is accessible
            userRepository.count();
            return true; // Return true if the query succeeds
        } catch (Exception ex) {
            return false; // Return false if an exception occurs
        }
    }

    // Check if the database is healthy by testing if a connection can be established
    public boolean isDatabaseHealthy() {
        try {
            // Execute a basic query to check MongoDB connectivity
            userRepository.existsByEmail("test@example.com");
            return true;
        } catch (Exception ex) {
            return false; // Return false if an exception occurs
        }
    }
}
