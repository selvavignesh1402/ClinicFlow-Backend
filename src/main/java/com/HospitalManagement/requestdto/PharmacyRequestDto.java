package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.InventoryStatus;

import java.time.LocalDate;

public record PharmacyRequestDto(
        Long medicationId,
        String code,
        String name,
        String formulation,
        String strength,
        String atcCode,
        Boolean controlledFlag,
        String batchNumber,
        Integer quantity,
        String unit,
        LocalDate expiryDate,
        String location,
        Double costPrice,
        InventoryStatus status,
        Long prescriptionId,
        Long inventoryItemId,
        Long dispensedById,
        Integer quantityDelta,
        String notes
) {
}