package com.example.taskApplication.services.Impl;

import com.example.taskApplication.models.User;
import com.example.taskApplication.repositories.UserRepository;
import com.example.taskApplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id)
                .or(() -> {
                    throw new IllegalArgumentException("User with ID " + id + " not found.");
                });
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .or(() -> {
                    throw new IllegalArgumentException("User with username " + username + " not found.");
                });
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with ID " + id + " does not exist.");
        }
        userRepository.deleteById(id);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
}