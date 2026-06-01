package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PaymentRequestDto(
        @NotNull Long invoiceId,
        @NotNull Long patientId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) Double amount,
        @NotBlank String method,
        LocalDateTime paidAt,
        String status
) {
}