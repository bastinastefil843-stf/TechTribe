package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.RegisterRequest;
import com.techtribe.techtribe_backend.model.User;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER USER
    public User registerUser(RegisterRequest request) {

        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setRole("DEVELOPER");

        String hashedPassword = passwordEncoder.encode(
                request.getPassword()
        );

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    // LOGIN USER
    public Optional<User> loginUser(
            String email,
            String password) {

        if (email == null || password == null) {
            return Optional.empty();
        }

        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        Optional<User> user = userRepository.findByEmail(
                normalizedEmail
        );

        if (user.isPresent()) {

            boolean passwordMatches = passwordEncoder.matches(
                    password,
                    user.get().getPassword()
            );

            if (passwordMatches) {
                return user;
            }
        }

        return Optional.empty();
    }

    // FIND USER BY EMAIL
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }
}