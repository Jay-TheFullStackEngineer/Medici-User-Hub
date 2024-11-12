package com.medici.user_hub.controller;

import com.medici.user_hub.model.User;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.TokenService;
import com.medici.user_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related endpoints, including token refreshing and logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    /**
     * Endpoint for refreshing the access token.
     * @param refreshToken The refresh token provided by the client.
     * @return A new access token if the refresh token is valid; 401 Unauthorized otherwise.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestBody String refreshToken) {
        try {
            // Validate refresh token and extract user ID
            String userId = jwtService.validateTokenAndGetUserId(refreshToken);
            if (userId != null) {
                // Retrieve user by ID
                User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Generate a new access token for the user
                String newAccessToken = jwtService.generateAccessToken(user);
                return ResponseEntity.ok(newAccessToken); // Return new access token
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    /**
     * Endpoint for logging out a user by blacklisting their access token.
     * @param accessToken The access token to be blacklisted.
     * @return A success message if logout is successful.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String accessToken) {
        try {
            // Get the expiration time of the token
            long expiration = jwtService.getTokenExpiration(accessToken);

            // Blacklist the access token to prevent further use
            tokenService.blacklistToken(accessToken, expiration);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout");
        }
    }
}
