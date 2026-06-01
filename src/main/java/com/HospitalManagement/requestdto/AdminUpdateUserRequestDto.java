package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminUpdateUserRequestDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        String password,
        @NotBlank String phone,
        @NotBlank String role,
        @NotBlank String status
) {
}