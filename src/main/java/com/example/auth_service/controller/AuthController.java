package com.example.auth_service.controller;

import com.example.auth_service.dto.RegisterRequestDto;
import com.example.auth_service.dto.UserResponseDto;
import com.example.auth_service.dto.LoginResponseDto;
import com.example.auth_service.model.User;
import com.example.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        User user = authService.register(
                request.getEmail(),
                request.getPassword(),
                User.Role.valueOf(request.getRole())
        );
        authService.createUserProfileInUserService(user, request);
        return ResponseEntity.ok(
                new UserResponseDto(user.getId(), user.getEmail(), user.getRole().name())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.example.auth_service.dto.LoginRequestDto request) {
        User user = authService.getUserByEmail(request.getEmail());
        String token = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(
                new LoginResponseDto(token, user.getId(), user.getEmail(), user.getRole().name())
        );
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.getUserByEmail(email);
        return ResponseEntity.ok(
                new UserResponseDto(user.getId(), user.getEmail(), user.getRole().name())
        );
    }


    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.getUserByEmail(email);
        return ResponseEntity.ok(
                new UserResponseDto(user.getId(), user.getEmail(), user.getRole().name())
        );
    }
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        // Optional: restrict only to admins
        String requester = authentication.getName();
        User actingUser = authService.getUserByEmail(requester);
        if (!actingUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }
        String newRole = body.get("role");
        authService.updateRole(id, newRole); // You’ll write this service method below
        return ResponseEntity.ok().build();
    }
}
