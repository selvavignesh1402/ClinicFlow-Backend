package com.HospitalManagement.responsedto;

import com.HospitalManagement.enums.PrescriptionStatus;
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
        PrescriptionStatus status,
        LocalDateTime issuedAt
) {
}