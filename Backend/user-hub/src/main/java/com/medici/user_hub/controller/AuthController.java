package com.medici.user_hub.controller;

import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.model.User;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.TokenService;
import com.medici.user_hub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    // Refresh access token for authenticated users
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestBody String refreshToken) {
        logger.info("AuthController - Request to refresh access token");
        try {
            String userId = jwtService.validateTokenAndGetUserId(refreshToken);
            if (userId != null) {
                User user = userService.getUserById(userId); // Throws ResourceNotFoundException if not found
                String newAccessToken = jwtService.generateAccessToken(user);
                logger.info("AuthController - Successfully refreshed access token for user ID: {}", userId);
                return ResponseEntity.ok(newAccessToken);
            } else {
                logger.warn("AuthController - Invalid refresh token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (ResourceNotFoundException e) {
            logger.error("AuthController - User not found during token refresh", e);
            throw e;
        } catch (Exception e) {
            logger.error("AuthController - Unexpected error during token refresh", e);
            throw e;
        }
    }

    // Logout by blacklisting the access token
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String accessToken) {
        logger.info("AuthController - Request to logout user with token");
        try {
            long expiration = jwtService.getTokenExpiration(accessToken);
            tokenService.blacklistToken(accessToken, expiration);
            logger.info("AuthController - Successfully logged out and blacklisted token");
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            logger.error("AuthController - Failed to logout and blacklist token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout");
        }
    }

    // Retrieve the security question for a given email (publicly accessible)
    @GetMapping("/security-question")
    public ResponseEntity<String> getSecurityQuestion(@RequestParam String email) {
        logger.info("AuthController - Request to retrieve security question for email: {}", email);
        try {
            String securityQuestion = userService.getSecurityQuestionByEmail(email); // Throws ResourceNotFoundException if email not found
            logger.info("AuthController - Successfully retrieved security question for email: {}", email);
            return ResponseEntity.ok(securityQuestion);
        } catch (ResourceNotFoundException e) {
            logger.error("AuthController - User not found with provided email: {}", email, e);
            throw e;
        } catch (Exception e) {
            logger.error("AuthController - Unexpected error retrieving security question for email: {}", email, e);
            throw e;
        }
    }

    // Validate security answer and reset password (publicly accessible)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String answer,
            @RequestParam String newPassword) {
        logger.info("AuthController - Request to reset password for email: {}", email);
        try {
            boolean resetSuccessful = userService.verifySecurityAnswerAndResetPassword(email, answer, newPassword);
            if (resetSuccessful) {
                logger.info("AuthController - Password reset successfully for email: {}", email);
                return ResponseEntity.ok("Password reset successful");
            } else {
                logger.warn("AuthController - Password reset failed for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed");
            }
        } catch (ResourceNotFoundException e) {
            logger.error("AuthController - User not found for password reset with email: {}", email, e);
            throw e;
        } catch (Exception e) {
            logger.error("AuthController - Unexpected error during password reset for email: {}", email, e);
            throw e;
        }
    }
}
