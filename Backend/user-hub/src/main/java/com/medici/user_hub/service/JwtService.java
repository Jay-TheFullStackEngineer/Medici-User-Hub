package com.medici.user_hub.service;

import com.medici.user_hub.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service for generating and validating JSON Web Tokens (JWT) for user authentication.
 * Includes methods for creating both access and refresh tokens.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret; // Secret key for signing the JWT

    @Value("${jwt.accessTokenExpirationMs}")
    private int accessTokenExpirationMs; // Expiration time for the access token in milliseconds

    @Value("${jwt.refreshTokenExpirationMs}")
    private int refreshTokenExpirationMs; // Expiration time for the refresh token in milliseconds

    // Generate an access token with a shorter expiration time
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId()) // Set the user's ID as the subject
                .setIssuedAt(new Date()) // Set the issue date as the current date
                .setExpiration(new Date(new Date().getTime() + accessTokenExpirationMs)) // Set access token expiration
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Sign with HS512 algorithm and secret key
                .compact(); // Build and return the compact JWT string
    }

    // Generate a refresh token with a longer expiration time
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId()) // Set the user's ID as the subject
                .setIssuedAt(new Date()) // Set the issue date as the current date
                .setExpiration(new Date(new Date().getTime() + refreshTokenExpirationMs)) // Set refresh token expiration
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Sign with HS512 algorithm and secret key
                .compact(); // Build and return the compact JWT string
    }

    // Validate a JWT and return the user ID if the token is valid
    public String validateTokenAndGetUserId(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret) // Set the secret key for validation
                .parseClaimsJws(token) // Parse the token and validate its signature
                .getBody()
                .getSubject(); // Extract and return the subject (user ID)
    }

    // Check if a token is expired
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date()); // Return true if the token is expired
    }

    // Get expiration time in milliseconds from the token
    public long getTokenExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime(); // Return expiration time in milliseconds
    }
}
