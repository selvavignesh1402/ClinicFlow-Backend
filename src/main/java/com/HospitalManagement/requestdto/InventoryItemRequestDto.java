package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.InventoryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record InventoryItemRequestDto(
        @NotNull Long medicationId,
        @NotNull String batchNumber,
        @NotNull @Min(0) Integer quantity,
        @NotNull String unit,
        @NotNull LocalDate expiryDate,
        @NotNull String location,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) Double costPrice,
        @NotNull InventoryStatus status
) {
}