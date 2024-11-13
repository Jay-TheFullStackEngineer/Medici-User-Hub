package com.medici.user_hub.service;

import com.medici.user_hub.handler.TokenServiceException;
import com.medici.user_hub.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing JWT tokens including generation, validation, and expiration checks.
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessTokenExpirationMs}")
    private int accessTokenExpirationMs;

    @Value("${jwt.refreshTokenExpirationMs}")
    private int refreshTokenExpirationMs;

    /**
     * Creates a signing key from the JWT secret.
     *
     * @return The signing key used for JWT operations
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * Generates an access token for a user.
     *
     * @param user The user for whom to generate the token
     * @return A JWT access token
     * @throws IllegalArgumentException if user is null
     * @throws TokenServiceException if token generation fails
     */
    public String generateAccessToken(User user) {
        if (user == null) {
            logger.error("Cannot generate access token for null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("type", "ACCESS");

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                    .signWith(SignatureAlgorithm.HS512, getSigningKey())
                    .compact();

            logger.info("Generated access token for user ID: {}", user.getId());
            return token;
        } catch (Exception e) {
            logger.error("Error generating access token for user ID: {}", user.getId(), e);
            throw new TokenServiceException("Failed to generate access token", e);
        }
    }

    /**
     * Generates a refresh token for a user.
     *
     * @param user The user for whom to generate the token
     * @return A JWT refresh token
     * @throws IllegalArgumentException if user is null
     * @throws TokenServiceException if token generation fails
     */
    public String generateRefreshToken(User user) {
        if (user == null) {
            logger.error("Cannot generate refresh token for null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("type", "REFRESH");

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                    .signWith(SignatureAlgorithm.HS512, getSigningKey())
                    .compact();

            logger.info("Generated refresh token for user ID: {}", user.getId());
            return token;
        } catch (Exception e) {
            logger.error("Error generating refresh token for user ID: {}", user.getId(), e);
            throw new TokenServiceException("Failed to generate refresh token", e);
        }
    }

    /**
     * Validates a token and extracts the user ID.
     *
     * @param token The token to validate
     * @return The user ID from the token
     * @throws TokenServiceException if token is invalid or expired
     */
    public String validateTokenAndGetUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("Cannot validate null or empty token");
            throw new TokenServiceException("Token cannot be null or empty");
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            logger.info("Successfully validated token for user ID: {}", userId);
            return userId;
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired", e);
            throw new TokenServiceException("Token has expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token", e);
            throw new TokenServiceException("Invalid JWT token", e);
        }
    }

    /**
     * Checks if a token is expired.
     *
     * @param token The token to check
     * @return true if token is expired, false otherwise
     * @throws TokenServiceException if token cannot be parsed
     */
    public boolean isTokenExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("Cannot check expiration for null or empty token");
            throw new TokenServiceException("Token cannot be null or empty");
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            boolean isExpired = expiration.before(new Date());
            logger.debug("Token expiration check - Token: {}, Expired: {}", token, isExpired);
            return isExpired;
        } catch (ExpiredJwtException e) {
            logger.info("Token has already expired");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Error checking token expiration", e);
            throw new TokenServiceException("Could not determine token expiration", e);
        }
    }

    /**
     * Gets the expiration time of a token.
     *
     * @param token The token to check
     * @return The expiration time in milliseconds
     * @throws TokenServiceException if token cannot be parsed
     */
    public long getTokenExpiration(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("Cannot get expiration for null or empty token");
            throw new TokenServiceException("Token cannot be null or empty");
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            logger.info("Retrieved token expiration time: {}", expiration.getTime());
            return expiration.getTime();
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Error retrieving token expiration", e);
            throw new TokenServiceException("Could not retrieve token expiration", e);
        }
    }
}