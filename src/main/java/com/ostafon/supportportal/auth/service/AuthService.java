package com.ostafon.supportportal.auth.service;

import com.ostafon.supportportal.auth.dto.request.LoginRequest;
import com.ostafon.supportportal.auth.dto.request.RegisterRequest;
import com.ostafon.supportportal.auth.dto.response.AuthResponse;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.common.security.JwtService;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        log.info("Attempting to register new user with email: {}", email);

        if (userRepo.existsByEmailIgnoreCase(email)) {
            log.warn("Registration failed: email already in use: {}", email);
            throw new IllegalArgumentException("Email already in use");
        }

        validatePassword(request.getPassword());

        UserEntity user = UserEntity.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .role(request.getRole() != null ? request.getRole() : UserRole.USER)
                .build();

        user = userRepo.save(user);
        log.info("User registered successfully with ID: {} and role: {}", user.getId(), user.getRole());

        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        log.info("Login attempt for email: {}", email);

        UserEntity user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for email: {}", email);
                    return new BadCredentialsException("Invalid email or password");
                });

        if (user.getIsActive() == null || !user.getIsActive()) {
            log.warn("Login failed: account is disabled for email: {}", email);
            throw new BadCredentialsException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: invalid password for email: {}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", email);
        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return email.trim().toLowerCase();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (password.length() > 255) {
            throw new IllegalArgumentException("Password is too long");
        }
    }

    private AuthResponse buildAuthResponse(UserEntity user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}

