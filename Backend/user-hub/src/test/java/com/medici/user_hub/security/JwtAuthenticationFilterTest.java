package com.medici.user_hub.security;

import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private static final String VALID_TOKEN = "validToken";
    private static final String USER_ID = "userId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.validateTokenAndGetUserId(VALID_TOKEN)).thenReturn(USER_ID);
        when(userService.loadUserByUsername(USER_ID)).thenReturn(userDetails);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + "invalidToken");
        when(jwtService.validateTokenAndGetUserId("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_UserNotFound_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.validateTokenAndGetUserId(VALID_TOKEN)).thenReturn(USER_ID);
        when(userService.loadUserByUsername(USER_ID)).thenThrow(new ResourceNotFoundException("User not found"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AuthenticationAlreadyExists_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        Authentication existingAuthentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.validateTokenAndGetUserId(VALID_TOKEN)).thenReturn(USER_ID);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertSame(existingAuthentication, authentication);
        verify(filterChain).doFilter(request, response);
    }
}