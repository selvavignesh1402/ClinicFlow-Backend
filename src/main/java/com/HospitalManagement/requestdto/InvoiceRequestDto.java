package com.HospitalManagement.requestdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record InvoiceRequestDto(
        @NotNull Long patientId,
        Long encounterId,
        String lineItemsJson,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) Double subtotal,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) Double taxes,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) Double discounts,
        @DecimalMin(value = "0.0", inclusive = true) Double totalAmount,
        LocalDateTime issuedAt,
        LocalDateTime dueDate,
        @NotBlank String status
)
{
}