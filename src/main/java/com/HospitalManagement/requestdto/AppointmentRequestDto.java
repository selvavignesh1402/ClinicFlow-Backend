package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        @NotNull
        Long patientId,

        @NotNull
        Long clinicianId,

        @NotBlank
        @Size(max = 100)
        String department,

        @NotBlank
        @Size(max = 100)
        String serviceType,

        @NotNull
        @FutureOrPresent
        LocalDateTime startAt,

        @NotNull
        @FutureOrPresent
        LocalDateTime endAt,

        AppointmentStatus status,

        @NotNull
        Long createdById
) {
}