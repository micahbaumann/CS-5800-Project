package com.chachef.repository;

import com.chachef.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Find a user by their username
    public Optional<User> findByUsername(String username);

    // Check if a username already exists
    public boolean existsByUsername(String username);

    // Find a user by their userId (redundant but clear)
    public Optional<User> findByUserId(UUID userId);
}
