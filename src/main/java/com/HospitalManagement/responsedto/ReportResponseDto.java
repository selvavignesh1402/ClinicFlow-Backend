package com.HospitalManagement.responsedto;

import java.time.LocalDateTime;

public record ReportResponseDto(
        Long reportId,
        String scope,
        String parametersJson,
        String metricsJson,
        Long generatedById,
        String generatedByName,
        LocalDateTime generatedAt,
        String reportUri
) {
}