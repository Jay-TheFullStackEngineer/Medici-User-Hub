package com.medici.user_hub.security;

import com.medici.user_hub.handler.ResourceNotFoundException;
import com.medici.user_hub.service.JwtService;
import com.medici.user_hub.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        // Check for Bearer token in Authorization header
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            logger.info("JwtAuthenticationFilter - Extracted JWT token from Authorization header");

            try {
                userId = jwtService.validateTokenAndGetUserId(token);
                logger.info("JwtAuthenticationFilter - Token validated for user ID: {}", userId);
            } catch (Exception e) {
                logger.error("JwtAuthenticationFilter - Invalid JWT token", e);
            }
        } else {
            logger.debug("JwtAuthenticationFilter - No Bearer token found in Authorization header");
        }

        // Set up the security context if the userId is valid and the user is not already authenticated
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userService.loadUserByUsername(userId); // Throws ResourceNotFoundException if user not found
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("JwtAuthenticationFilter - Security context set for user ID: {}", userId);
            } catch (ResourceNotFoundException ex) {
                logger.error("JwtAuthenticationFilter - User not found for ID: {}", userId, ex);
            }
        } else if (userId != null) {
            logger.debug("JwtAuthenticationFilter - Security context already exists for user ID: {}", userId);
        }

        chain.doFilter(request, response);
    }
}
