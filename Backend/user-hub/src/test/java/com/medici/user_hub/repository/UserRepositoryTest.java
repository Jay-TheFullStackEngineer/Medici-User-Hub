package com.medici.user_hub.repository;

import com.medici.user_hub.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a user for testing
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setPasswordHash("hashedPassword");

        // Clear the repository and save the test user
        userRepository.deleteAll();
        userRepository.save(testUser);
    }

    @Test
    void  shouldFindUserByEmail() {
        // Attempt to find the user by email
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Validate that the user is found and data matches
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.get().getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void shouldReturnTrueIfUserExistsByEmail() {
        // Check existence of a user by email
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Validate that the user exists
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfUserDoesNotExistByEmail() {
        // Check non-existence of a user by a different email
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Validate that the user does not exist
        assertThat(exists).isFalse();
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        // Create a new user
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setUsername("newUser");
        newUser.setPasswordHash("newPassword");

        // Save the user
        User savedUser = userRepository.save(newUser);

        // Retrieve the user by ID and validate data matches
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo(newUser.getEmail());
        assertThat(retrievedUser.get().getUsername()).isEqualTo(newUser.getUsername());
    }
}
