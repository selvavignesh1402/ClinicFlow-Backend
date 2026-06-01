package com.HospitalManagement.responsedto;

import java.time.LocalDateTime;

public record PaymentResponseDto(
        Long paymentId,
        Long invoiceId,
        Long patientId,
        String patientName,
        Double amount,
        String method,
        LocalDateTime paidAt,
        String status
) {
}