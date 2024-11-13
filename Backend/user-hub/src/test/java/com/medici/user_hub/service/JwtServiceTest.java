package com.medici.user_hub.service;

import com.medici.user_hub.handler.TokenServiceException;
import com.medici.user_hub.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "jwt.secret=ZmQ5NDIwMjQ5NjJkNzZmYjc1ZDllMDM3MGNhYWIwZjczN2M2OTk5Mjk3ZGE1ODU5M2JjOTNiZDQ0ZTM3YjhmYQ==",
        "jwt.accessTokenExpirationMs=900000",
        "jwt.refreshTokenExpirationMs=86400000"
})
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private static final String TEST_SECRET = "ZmQ5NDIwMjQ5NjJkNzZmYjc1ZDllMDM3MGNhYWIwZjczN2M2OTk5Mjk3ZGE1ODU5M2JjOTNiZDQ0ZTM3YjhmYQ==";
    private static final int ACCESS_TOKEN_EXPIRATION = 900000;
    private static final int REFRESH_TOKEN_EXPIRATION = 86400000;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");

        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMs", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationMs", REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void generateAccessToken_withValidUser_returnsValidToken() {
        // Act
        String token = jwtService.generateAccessToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .setSigningKey(TEST_SECRET)
                .parseClaimsJws(token)
                .getBody();

        assertEquals(testUser.getId(), claims.getSubject());
        assertEquals("ACCESS", claims.get("type"));
        assertEquals(testUser.getUsername(), claims.get("username"));
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
        assertTrue(claims.getExpiration().getTime() <= System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION + 1000);
    }

    @Test
    void generateAccessToken_withNullUser_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                jwtService.generateAccessToken(null));
    }

    @Test
    void generateRefreshToken_withValidUser_returnsValidToken() {
        // Act
        String token = jwtService.generateRefreshToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .setSigningKey(TEST_SECRET)
                .parseClaimsJws(token)
                .getBody();

        assertEquals(testUser.getId(), claims.getSubject());
        assertEquals("REFRESH", claims.get("type"));
        assertEquals(testUser.getUsername(), claims.get("username"));
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
        assertTrue(claims.getExpiration().getTime() <= System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION + 1000);
    }

    @Test
    void validateTokenAndGetUserId_withValidToken_returnsUserId() {
        // Arrange
        String token = jwtService.generateAccessToken(testUser);

        // Act
        String userId = jwtService.validateTokenAndGetUserId(token);

        // Assert
        assertEquals(testUser.getId(), userId);
    }

    @Test
    void validateTokenAndGetUserId_withExpiredToken_throwsException() {
        // Arrange
        String expiredToken = Jwts.builder()
                .setSubject(testUser.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, TEST_SECRET)
                .compact();

        // Act & Assert
        TokenServiceException exception = assertThrows(TokenServiceException.class,
                () -> jwtService.validateTokenAndGetUserId(expiredToken));
        assertEquals("Token has expired", exception.getMessage());
    }

    @Test
    void validateTokenAndGetUserId_withInvalidToken_throwsException() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act & Assert
        assertThrows(TokenServiceException.class,
                () -> jwtService.validateTokenAndGetUserId(invalidToken));
    }

    @Test
    void isTokenExpired_withExpiredToken_returnsTrue() {
        // Arrange
        String expiredToken = Jwts.builder()
                .setSubject(testUser.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, TEST_SECRET)
                .compact();

        // Act & Assert
        assertTrue(jwtService.isTokenExpired(expiredToken));
    }

    @Test
    void isTokenExpired_withValidToken_returnsFalse() {
        // Arrange
        String validToken = jwtService.generateAccessToken(testUser);

        // Act & Assert
        assertFalse(jwtService.isTokenExpired(validToken));
    }

    @Test
    void getTokenExpiration_withValidToken_returnsCorrectExpiration() {
        // Arrange
        long now = System.currentTimeMillis();
        String token = jwtService.generateAccessToken(testUser);

        // Act
        long expiration = jwtService.getTokenExpiration(token);

        // Assert
        assertTrue(expiration > now);
        assertTrue(expiration <= now + ACCESS_TOKEN_EXPIRATION + 1000);
    }

    @Test
    void getTokenExpiration_withInvalidToken_throwsException() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act & Assert
        assertThrows(TokenServiceException.class,
                () -> jwtService.getTokenExpiration(invalidToken));
    }
}