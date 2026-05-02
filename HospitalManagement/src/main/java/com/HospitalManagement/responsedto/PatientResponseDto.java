package com.HospitalManagement.responsedto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponseDto(
        Long patientId,
        String mrn,
        String name,
        String primaryContact,
        LocalDate dob,
        String gender,
        String addressJson,
        String insuranceId,
        String status,
        LocalDateTime createdAt
) {}
