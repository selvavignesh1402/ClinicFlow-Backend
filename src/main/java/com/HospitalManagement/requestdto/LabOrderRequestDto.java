package com.HospitalManagement.requestdto;


import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record LabOrderRequestDto(
        Long encounterId,
        @NotBlank String testsJson,
        String sampleId,
        LocalDateTime collectedAt
) {
}