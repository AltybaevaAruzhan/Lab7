package com.example.taskApplication.services;

import com.example.taskApplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUserPreservingPassword(User updatedUser);
    User saveUser(User user);
    User findByUsername(String username);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    void deleteUser(Long id);
//    void resetPassword(String email);
    List<User> searchUsers(String query);
    void updateUserProfile(User user);
    boolean isUsernameTaken(String username);
    Page<User> getPaginatedUsers(Pageable pageable);
    List<User> getAllUsers();
    User findByEmail(String email);
//    void sendPasswordEmail(String email, String password);
}

