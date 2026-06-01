package com.HospitalManagement.responsedto;

import com.HospitalManagement.enums.LabResultFlag;

import java.time.LocalDateTime;

public record LabResultResponseDto(
        Long resultId,
        Long labOrderId,
        String testCode,
        String value,
        String units,
        String referenceRangeJson,
        LabResultFlag flag,
        LocalDateTime reportedAt,
        Long reportedById,
        String reportedByName
) {
}