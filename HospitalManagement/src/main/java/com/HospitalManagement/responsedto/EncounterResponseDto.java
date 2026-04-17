package com.HospitalManagement.responsedto;

import java.time.LocalDateTime;

public record EncounterResponseDto(
        Long encounterId,
        Long patientId,
        String patientName,
        Long clinicianId,
        String clinicianName,
        String visitType,
        String chiefComplaint,
        String vitalsJson,
        String notesJson,
        String diagnosesJson,
        String ordersJson,
        String prescriptionsJson,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status,
        Long signedById,
        String signedByName,
        LocalDateTime signedAt
) {
}
