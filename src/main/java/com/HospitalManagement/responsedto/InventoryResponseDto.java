package com.HospitalManagement.responsedto;

import com.HospitalManagement.enums.InventoryStatus;

import java.time.LocalDate;

public record InventoryResponseDto(
        Long inventoryId,
        Long medicationId,
        String medicationCode,
        String medicationName,
        String batchNumber,
        Integer quantity,
        String unit,
        LocalDate expiryDate,
        String location,
        Double costPrice,
        InventoryStatus status,
        Boolean expired
) {
}