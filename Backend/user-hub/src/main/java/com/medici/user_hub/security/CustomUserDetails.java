package com.medici.user_hub.security;

import com.medici.user_hub.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
        logger.info("CustomUserDetails - Instantiated for user ID: {}", user.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        logger.info("CustomUserDetails - Fetching authorities for user ID: {}", user.getId());
        // Return authorities based on the user’s roles (for now, it’s empty, can be populated later)
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        logger.debug("CustomUserDetails - Fetching password hash for user ID: {}", user.getId());
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        logger.debug("CustomUserDetails - Fetching username for user ID: {}", user.getId());
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        logger.debug("CustomUserDetails - Checking if account is non-expired for user ID: {}", user.getId());
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        logger.debug("CustomUserDetails - Checking if account is non-locked for user ID: {}", user.getId());
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        logger.debug("CustomUserDetails - Checking if credentials are non-expired for user ID: {}", user.getId());
        return true;
    }

    @Override
    public boolean isEnabled() {
        logger.debug("CustomUserDetails - Checking if account is enabled for user ID: {}", user.getId());
        // Modify if you want to enable/disable based on certain user attributes.
        return true;
    }
}
