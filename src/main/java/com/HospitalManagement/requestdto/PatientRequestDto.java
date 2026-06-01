package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequestDto(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        @Past
        LocalDate dob,

        @NotBlank
        @Size(max = 30)
        String gender,

        @NotBlank
        String contactInfoJson,

        @NotBlank
        String addressJson,

        @NotBlank
        @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$")
        @Size(max = 150)
        String primaryContact,

        @Size(max = 100)
        String insuranceId,

        @Size(max = 30)
        String status
) {
}