package com.medici.user_hub.service;

import com.medici.user_hub.handler.TokenServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void blacklistToken_success() {
        // Arrange
        String token = "testToken";
        long expirationMs = 1000L;

        doNothing().when(valueOperations)
                .set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // Act
        tokenService.blacklistToken(token, expirationMs);

        // Assert
        verify(valueOperations).set(
                eq("blacklisted:testToken"),
                eq("BLACKLISTED"),
                eq(expirationMs),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void blacklistToken_whenRedisThrowsException_throwsTokenServiceException() {
        // Arrange
        String token = "testToken";
        doThrow(new RuntimeException("Redis error"))
                .when(valueOperations)
                .set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // Act & Assert
        assertThrows(TokenServiceException.class, () ->
                tokenService.blacklistToken(token, 1000L));
    }

    @Test
    void isTokenBlacklisted_whenTokenIsBlacklisted_returnsTrue() {
        // Arrange
        String token = "blacklistedToken";
        when(redisTemplate.hasKey("blacklisted:" + token)).thenReturn(true);

        // Act
        boolean result = tokenService.isTokenBlacklisted(token);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey("blacklisted:" + token);
    }

    @Test
    void isTokenBlacklisted_whenTokenIsNotBlacklisted_returnsFalse() {
        // Arrange
        String token = "notBlacklistedToken";
        when(redisTemplate.hasKey("blacklisted:" + token)).thenReturn(false);

        // Act
        boolean result = tokenService.isTokenBlacklisted(token);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey("blacklisted:" + token);
    }

    @Test
    void storeRefreshToken_success() {
        // Arrange
        String token = "refreshToken";
        long expirationMs = 1000L;

        doNothing().when(valueOperations)
                .set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // Act
        tokenService.storeRefreshToken(token, expirationMs);

        // Assert
        verify(valueOperations).set(
                eq(token),
                eq("REFRESH_TOKEN"),
                eq(expirationMs),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void isRefreshTokenValid_whenTokenIsValid_returnsTrue() {
        // Arrange
        String token = "validToken";
        when(redisTemplate.hasKey(token)).thenReturn(true);

        // Act
        boolean result = tokenService.isRefreshTokenValid(token);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey(token);
    }

    @Test
    void revokeRefreshToken_success() {
        // Arrange
        String token = "tokenToRevoke";
        when(redisTemplate.delete(token)).thenReturn(true);

        // Act
        tokenService.revokeRefreshToken(token);

        // Assert
        verify(redisTemplate).delete(token);
    }

    @Test
    void getTokenExpiration_whenTokenExists_returnsExpiration() {
        // Arrange
        String token = "testToken";
        long expectedExpiration = 5000L;
        when(redisTemplate.getExpire(token, TimeUnit.MILLISECONDS))
                .thenReturn(expectedExpiration);

        // Act
        long result = tokenService.getTokenExpiration(token);

        // Assert
        assertEquals(expectedExpiration, result);
        verify(redisTemplate).getExpire(token, TimeUnit.MILLISECONDS);
    }

    @Test
    void getTokenExpiration_whenTokenDoesNotExist_returnsNegativeOne() {
        // Arrange
        String token = "nonExistentToken";
        when(redisTemplate.getExpire(token, TimeUnit.MILLISECONDS))
                .thenReturn(null);

        // Act
        long result = tokenService.getTokenExpiration(token);

        // Assert
        assertEquals(-1, result);
        verify(redisTemplate).getExpire(token, TimeUnit.MILLISECONDS);
    }
}