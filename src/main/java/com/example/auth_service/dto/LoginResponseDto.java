package com.example.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private UUID id;
    private String email;
    private String role;
}
