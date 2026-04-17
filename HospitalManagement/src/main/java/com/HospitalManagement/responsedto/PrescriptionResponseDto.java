package com.HospitalManagement.responsedto;

import java.time.LocalDateTime;

public record PrescriptionResponseDto(
        Long rxId,
        Long encounterId,
        Long patientId,
        String patientName,
        Long clinicianId,
        String clinicianName,
        Long medicationId,
        String medicationName,
        String dosage,
        String frequency,
        Integer durationDays,
        Integer quantity,
        Integer repeats,
        String route,
        String notes,
        String status,
        LocalDateTime issuedAt
) {
}
