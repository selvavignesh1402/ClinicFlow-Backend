package com.HospitalManagement.responsedto;

import com.HospitalManagement.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long apptId,
        Long patientId,
        String patientMrn,
        String patientName,
        Long clinicianId,
        String clinicianName,
        String department,
        String serviceType,
        LocalDateTime startAt,
        LocalDateTime endAt,
        AppointmentStatus status,
        Long createdById,
        String createdByName,
        LocalDateTime createdAt
) {
}