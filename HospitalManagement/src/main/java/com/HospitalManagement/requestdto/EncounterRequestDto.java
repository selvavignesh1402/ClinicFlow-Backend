package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EncounterRequestDto(
        @NotNull Long patientId,
        @NotNull Long clinicianId,
        @NotBlank String visitType,
        @NotBlank String chiefComplaint,
        String vitalsJson,
        String notesJson,
        String diagnosesJson,
        String ordersJson,
        String prescriptionsJson,
        @NotNull LocalDateTime startAt,
        LocalDateTime endAt,
        @NotBlank String status,
        Long signedById,
        LocalDateTime signedAt
) {
}
