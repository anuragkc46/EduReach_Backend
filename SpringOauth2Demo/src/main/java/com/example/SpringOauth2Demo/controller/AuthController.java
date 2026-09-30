package com.example.SpringOauth2Demo.controller;

import com.example.SpringOauth2Demo.dto.LoginRequest;
import com.example.SpringOauth2Demo.dto.RegisterRequest;
import com.example.SpringOauth2Demo.entity.User;
import com.example.SpringOauth2Demo.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest()
                    .body("Email already registered");
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());

        // Hash the password before storing it
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setPhone(request.phone());

        // Default role for registration
        user.setRole("STUDENT");

        user.setActive(true);

        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok("Registration successful");
    }


    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {

        User existingUser = userRepository
                .findByEmail(request.email())
                .orElse(null);

        if (existingUser == null) {
            return ResponseEntity.status(401)
                    .body("User not registered");
        }

        if (!existingUser.isActive()) {
            return ResponseEntity.status(403)
                    .body("Account is inactive");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                existingUser.getPasswordHash()
        );

        if (!passwordMatches) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        return ResponseEntity.ok(existingUser.getRole());
    }
}