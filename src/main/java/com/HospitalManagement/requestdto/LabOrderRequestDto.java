package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.LabOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record LabOrderRequestDto(
        Long encounterId,
        @NotBlank String testsJson,
        String sampleId,
        LocalDateTime collectedAt
) {
}