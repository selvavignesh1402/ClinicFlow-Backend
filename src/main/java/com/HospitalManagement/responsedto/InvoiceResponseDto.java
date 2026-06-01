package com.HospitalManagement.responsedto;

import com.HospitalManagement.enums.InvoiceStatus;

import java.time.LocalDateTime;

public record InvoiceResponseDto(
        Long invoiceId,
        Long patientId,
        String patientName,
        Long encounterId,
        String lineItemsJson,
        Double subtotal,
        Double taxes,
        Double discounts,
        Double totalAmount,
        LocalDateTime issuedAt,
        LocalDateTime dueDate,
        InvoiceStatus status
) {
}