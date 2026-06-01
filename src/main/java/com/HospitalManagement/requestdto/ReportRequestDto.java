package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReportRequestDto(
        @NotBlank String scope,
        String parametersJson,
        String metricsJson,
        LocalDateTime generatedAt,
        @NotBlank String reportUri
) {
}