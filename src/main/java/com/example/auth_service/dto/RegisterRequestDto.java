package com.example.auth_service.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String email;
    private String password;
    private String role; // "USER" or "ADMIN"
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
