package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.EncounterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EncounterRequestDto(

        @NotNull
        Long patientId,

        @NotBlank
        String visitType,

        @NotBlank
        String chiefComplaint,

        @NotBlank
        String vitalsJson,

        @NotBlank
        String notesJson,

        @NotBlank
        String diagnosesJson,

        @NotBlank
        String ordersJson,

        String prescriptionsJson,
        EncounterStatus status
) {
}