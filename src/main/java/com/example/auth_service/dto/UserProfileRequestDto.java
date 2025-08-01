package com.example.auth_service.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserProfileRequestDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
