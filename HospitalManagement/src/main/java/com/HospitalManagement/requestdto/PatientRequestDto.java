package com.HospitalManagement.requestdto;

import java.time.LocalDate;

public record PatientRequestDto(
    String mrn,
    String name,
    LocalDate dob,
    String gender,
    String contactInfoJson,
    String addressJson,
    String primaryContact,
    String insuranceId,
    String status
) {}
