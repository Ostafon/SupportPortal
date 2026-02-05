package com.ostafon.supportportal.auth.service;


import com.ostafon.supportportal.auth.dto.request.LoginRequest;
import com.ostafon.supportportal.auth.dto.request.RegisterRequest;
import com.ostafon.supportportal.auth.dto.response.AuthResponse;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.common.security.JwtService;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

         UserEntity user = UserEntity.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole() == null ? UserRole.USER : request.getRole())
                .build();

        user = userRepo.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        UserEntity user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}

