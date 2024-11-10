package com.medici.user_hub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import com.medici.user_hub.model.User;

/**
 * Repository interface for User entities.
 * Extends MongoRepository to provide CRUD operations and custom queries on User documents.
 */
public interface UserRepository extends MongoRepository<User, String> {

    // Find a user by their email address
    Optional<User> findByEmail(String email);

    // Check if a user exists with the given email address
    boolean existsByEmail(String email);
}
