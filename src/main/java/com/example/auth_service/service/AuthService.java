package com.example.auth_service.service;

import com.example.auth_service.dto.RegisterRequestDto;
import com.example.auth_service.dto.UserProfileRequestDto;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${user.service.url}")
    private String userServiceUrl;

    public User register(String email, String password, User.Role role) {
        if (userRepository.findByEmail(email).isPresent())
            throw new RuntimeException("Email already in use!");
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        return jwtUtil.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "userId", user.getId().toString())
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ---- New: Create profile in user-service ---
    public void createUserProfileInUserService(User user, RegisterRequestDto request) {
        UserProfileRequestDto profile = new UserProfileRequestDto();
        profile.setId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());

        restTemplate.postForEntity(
                userServiceUrl + "/api/v1/users",
                profile,
                Void.class
        );
    }
    public void updateRole(UUID userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(User.Role.valueOf(newRole));
        userRepository.save(user);
    }
}
