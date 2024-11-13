package com.medici.user_hub.service;

import com.medici.user_hub.handler.TokenServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Service for handling token-related operations with Redis, including blacklisting, storing,
 * validating, and revoking tokens. Provides structured logging for improved monitoring.
 */
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLISTED_TOKEN_PREFIX = "blacklisted:";

    // Blacklists a token by storing it in Redis with a specified expiration time.
    public void blacklistToken(String token, long expirationMs) {
        try {
            String key = BLACKLISTED_TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(key, "BLACKLISTED", expirationMs, TimeUnit.MILLISECONDS);
            logger.info("Token {} has been blacklisted for {} milliseconds", token, expirationMs);
        } catch (Exception e) {
            logger.error("Failed to blacklist token {}", token, e);
            throw new TokenServiceException("Failed to blacklist token", e);
        }
    }

    // Checks if a token is blacklisted.
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLISTED_TOKEN_PREFIX + token;
            boolean isBlacklisted = redisTemplate.hasKey(key);
            logger.info("Token {} is blacklisted: {}", token, isBlacklisted);
            return isBlacklisted;
        } catch (Exception e) {
            logger.error("Failed to check if token {} is blacklisted", token, e);
            throw new TokenServiceException("Failed to check if token is blacklisted", e);
        }
    }

    // Stores a refresh token in Redis with a specific expiration.
    public void storeRefreshToken(String token, long expirationMs) {
        try {
            redisTemplate.opsForValue().set(token, "REFRESH_TOKEN", expirationMs, TimeUnit.MILLISECONDS);
            logger.info("Refresh token {} stored with expiration of {} milliseconds", token, expirationMs);
        } catch (Exception e) {
            logger.error("Failed to store refresh token {}", token, e);
            throw new TokenServiceException("Failed to store refresh token", e);
        }
    }

    // Checks if a refresh token is valid and exists in Redis.
    public boolean isRefreshTokenValid(String token) {
        try {
            boolean isValid = redisTemplate.hasKey(token);
            logger.info("Refresh token {} is valid: {}", token, isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Failed to check if refresh token {} is valid", token, e);
            throw new TokenServiceException("Failed to check refresh token validity", e);
        }
    }

    // Revokes a refresh token by deleting it from Redis.
    public void revokeRefreshToken(String token) {
        try {
            redisTemplate.delete(token);
            logger.info("Refresh token {} has been revoked", token);
        } catch (Exception e) {
            logger.error("Failed to revoke refresh token {}", token, e);
            throw new TokenServiceException("Failed to revoke refresh token", e);
        }
    }

    // Retrieves the expiration time of a token in Redis.
    public long getTokenExpiration(String token) {
        try {
            Long expiration = redisTemplate.getExpire(token, TimeUnit.MILLISECONDS);
            logger.info("Token {} expiration retrieved: {} milliseconds remaining", token, expiration);
            return (expiration != null) ? expiration : -1;
        } catch (Exception e) {
            logger.error("Failed to get expiration for token {}", token, e);
            throw new TokenServiceException("Failed to get token expiration", e);
        }
    }
}
