package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PrescriptionRequestDto(
        @NotNull Long encounterId,
        @NotNull Long patientId,
        @NotNull Long clinicianId,
        @NotNull Long medicationId,
        @NotBlank String dosage,
        @NotBlank String frequency,
        @NotNull @Min(1) Integer durationDays,
        @NotNull @Min(1) Integer quantity,
        @NotNull @Min(0) Integer repeats,
        @NotBlank String route,
        String notes,
        @NotBlank String status,
        LocalDateTime issuedAt
) {
}
