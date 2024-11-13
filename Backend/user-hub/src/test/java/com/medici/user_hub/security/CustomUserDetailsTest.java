package com.medici.user_hub.security;

import com.medici.user_hub.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsTest {

    @Mock
    private User mockUser;

    private CustomUserDetails customUserDetails;
    private static final String TEST_ID = "test-id-123";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "hashedpassword123";

    @BeforeEach
    void setUp() {
        // Set up mock user behavior
        lenient().when(mockUser.getId()).thenReturn(TEST_ID);
        lenient().when(mockUser.getUsername()).thenReturn(TEST_USERNAME);
        lenient().when(mockUser.getPasswordHash()).thenReturn(TEST_PASSWORD);

        customUserDetails = new CustomUserDetails(mockUser);
    }

    @Test
    void constructor_whenUserIsNull_throwsException() {
        assertThrows(NullPointerException.class,
                () -> new CustomUserDetails(null));
    }

    @Test
    void getAuthorities_returnsEmptyList() {
        // Act
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getAuthorities
    }

    @Test
    void getPassword_returnsUserPassword() {
        // Act
        String password = customUserDetails.getPassword();

        // Assert
        assertEquals(TEST_PASSWORD, password);
        verify(mockUser).getPasswordHash();
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getPassword
    }

    @Test
    void getUsername_returnsUserUsername() {
        // Act
        String username = customUserDetails.getUsername();

        // Assert
        assertEquals(TEST_USERNAME, username);
        verify(mockUser).getUsername();
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getUsername
    }

    @Test
    void isAccountNonExpired_returnsTrue() {
        // Act
        boolean result = customUserDetails.isAccountNonExpired();

        // Assert
        assertTrue(result);
        verify(mockUser, times(2)).getId(); // Once for constructor, once for isAccountNonExpired
    }

    @Test
    void isAccountNonLocked_returnsTrue() {
        // Act
        boolean result = customUserDetails.isAccountNonLocked();

        // Assert
        assertTrue(result);
        verify(mockUser, times(2)).getId(); // Once for constructor, once for isAccountNonLocked
    }

    @Test
    void isCredentialsNonExpired_returnsTrue() {
        // Act
        boolean result = customUserDetails.isCredentialsNonExpired();

        // Assert
        assertTrue(result);
        verify(mockUser, times(2)).getId(); // Once for constructor, once for isCredentialsNonExpired
    }

    @Test
    void isEnabled_returnsTrue() {
        // Act
        boolean result = customUserDetails.isEnabled();

        // Assert
        assertTrue(result);
        verify(mockUser, times(2)).getId(); // Once for constructor, once for isEnabled
    }

    @Test
    void customUserDetails_preservesUserReference() {
        // Arrange
        String newUsername = "updatedUsername";
        when(mockUser.getUsername()).thenReturn(newUsername);

        // Act
        String username = customUserDetails.getUsername();

        // Assert
        assertEquals(newUsername, username);
        verify(mockUser, times(1)).getUsername(); // Invoked once in the test case
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getUsername
    }

    @Test
    void constructor_verifyLogging() {
        // This test is mainly for coverage of logging
        // Actual log output verification would require a more complex setup
        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        verify(mockUser, times(2)).getId(); // Invoked twice (once in setUp, once in constructor)
    }

    @Test
    void customUserDetails_handlesMultipleMethodCalls() {
        // Act
        customUserDetails.getUsername();
        customUserDetails.getPassword();
        customUserDetails.isEnabled();
        customUserDetails.getAuthorities();

        // Assert
        verify(mockUser, times(5)).getId(); // Once for constructor, once for each method call
        verify(mockUser, times(1)).getUsername(); // Invoked once in the test case
        verify(mockUser, times(1)).getPasswordHash(); // Invoked once in the test case
    }

    @Test
    void customUserDetails_handlesEmptyUsername() {
        // Arrange
        when(mockUser.getUsername()).thenReturn("");

        // Act
        String username = customUserDetails.getUsername();

        // Assert
        assertEquals("", username);
        verify(mockUser).getUsername();
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getUsername
    }

    @Test
    void customUserDetails_handlesNullUsername() {
        // Arrange
        when(mockUser.getUsername()).thenReturn(null);

        // Act
        String username = customUserDetails.getUsername();

        // Assert
        assertNull(username);
        verify(mockUser).getUsername();
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getUsername
    }

    @Test
    void customUserDetails_handlesEmptyPassword() {
        // Arrange
        when(mockUser.getPasswordHash()).thenReturn("");

        // Act
        String password = customUserDetails.getPassword();

        // Assert
        assertEquals("", password);
        verify(mockUser).getPasswordHash();
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getPassword
    }

    @Test
    void customUserDetails_handlesNullPassword() {
        // Arrange
        when(mockUser.getPasswordHash()).thenReturn(null);

        // Act
        String password = customUserDetails.getPassword();

        // Assert
        assertNull(password);
        verify(mockUser).getPasswordHash();
        verify(mockUser, times(2)).getId(); // Once for constructor, once for getPassword
    }

    @Test
    void getAuthorities_ensureImmutableCollection() {
        // Act
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        // Assert
        assertThrows(UnsupportedOperationException.class,
                () -> ((Collection<GrantedAuthority>) authorities).add(null));
    }
}