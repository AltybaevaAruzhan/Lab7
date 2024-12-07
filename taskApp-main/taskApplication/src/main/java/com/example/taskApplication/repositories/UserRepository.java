package com.example.taskApplication.repositories;

import com.example.taskApplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id); // Changed return type to Optional

    boolean existsByUsername(String username);

    List<User> findByRole(User.Role role);

    @Query("SELECT u FROM User u")
    List<User> getAllUsers();

    Optional<User> findByEmail(String email);
}

