package com.HospitalManagement.responsedto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long userId,
        String name,
        String email,
        String phone,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}