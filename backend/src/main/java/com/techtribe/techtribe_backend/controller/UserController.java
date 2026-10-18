package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.RegisterRequest;
import com.techtribe.techtribe_backend.dto.UserResponse;
import com.techtribe.techtribe_backend.model.User;
import com.techtribe.techtribe_backend.service.UserService;
import com.techtribe.techtribe_backend.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    // USER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        try {

            User savedUser = userService.registerUser(request);

            return ResponseEntity.ok(
                    new UserResponse(savedUser)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // USER LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User user) {

        Optional<User> loggedUser = userService.loginUser(
                user.getEmail(),
                user.getPassword()
        );

        if (loggedUser.isPresent()) {

            User authenticatedUser = loggedUser.get();

            String token = jwtService.generateToken(
                    authenticatedUser.getEmail()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "tokenType", "Bearer",
                            "user", new UserResponse(authenticatedUser)
                    )
            );
        }

        return ResponseEntity.status(401)
                .body("Invalid email or password");
    }

    // GET CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return ResponseEntity.status(401)
                    .body("Unauthorized");
        }

        String email = authentication.getName();

        Optional<User> user = userService.findByEmail(email);

        if (user.isPresent()) {

            return ResponseEntity.ok(
                    new UserResponse(user.get())
            );
        }

        return ResponseEntity.status(404)
                .body("User not found");
    }
}