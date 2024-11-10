package com.medici.user_hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling token operations such as blacklisting and expiration management.
 * Uses Redis for storing token data, providing a foundation for access and refresh token management.
 */
@Service
public class TokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Key prefix to distinguish blacklisted tokens in Redis
    private static final String BLACKLISTED_TOKEN_PREFIX = "blacklisted:";

    /**
     * Blacklists a token by setting it in Redis with an expiration time.
     * @param token The token to be blacklisted.
     * @param expirationMs Expiration time in milliseconds from now.
     */
    public void blacklistToken(String token, long expirationMs) {
        String key = BLACKLISTED_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, "BLACKLISTED", expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Checks if a token is blacklisted by looking it up in Redis.
     * @param token The token to check.
     * @return True if the token is blacklisted; false otherwise.
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLISTED_TOKEN_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    /**
     * Stores a refresh token in Redis with a specified expiration time.
     * Can be used to manage refresh token lifecycles and limit their validity.
     * @param token The refresh token to store.
     * @param expirationMs Expiration time in milliseconds from now.
     */
    public void storeRefreshToken(String token, long expirationMs) {
        redisTemplate.opsForValue().set(token, "REFRESH_TOKEN", expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Checks if a refresh token is valid and not expired in Redis.
     * @param token The refresh token to check.
     * @return True if the token is valid and exists; false if expired or missing.
     */
    public boolean isRefreshTokenValid(String token) {
        return redisTemplate.hasKey(token);
    }

    /**
     * Revokes a refresh token by deleting it from Redis.
     * Useful for scenarios where refresh tokens need to be invalidated, such as logout.
     * @param token The refresh token to revoke.
     */
    public void revokeRefreshToken(String token) {
        redisTemplate.delete(token);
    }

    /**
     * Retrieves the expiration time of a given token in Redis.
     * @param token The token to check.
     * @return Expiration time in milliseconds from now; -1 if token does not exist.
     */
    public long getTokenExpiration(String token) {
        Long expiration = redisTemplate.getExpire(token, TimeUnit.MILLISECONDS);
        return (expiration != null) ? expiration : -1;
    }
}
