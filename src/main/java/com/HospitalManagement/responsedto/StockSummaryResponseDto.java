package com.HospitalManagement.responsedto;

public record StockSummaryResponseDto(
        Long medicationId,
        String medicationName,
        Integer totalQuantity,
        String unit,
        Integer batchCount
) {
}