package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.LabResultFlag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record LabResultRequestDto(
        @NotNull Long labOrderId,
        @NotBlank String testCode,
        @NotBlank String value,
        String units,
        String referenceRangeJson,
        LabResultFlag flag,
        LocalDateTime reportedAt
) {
}