package com.HospitalManagement.responsedto;

import java.time.LocalDateTime;

public record DispenseResponseDto(
        Long dispenseId,
        Long prescriptionId,
        Long inventoryItemId,
        String medicationName,
        String batchNumber,
        Long patientId,
        String patientName,
        Long dispensedById,
        String dispensedByName,
        Integer quantity,
        LocalDateTime dispensedAt,
        String status
) {
}