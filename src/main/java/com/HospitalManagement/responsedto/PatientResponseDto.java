package com.HospitalManagement.responsedto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponseDto(
        Long patientId,
        String mrn,
        String name,
        LocalDate dob,
        String gender,
        String contactInfoJson,
        String addressJson,
        String primaryContact,
        String insuranceId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}