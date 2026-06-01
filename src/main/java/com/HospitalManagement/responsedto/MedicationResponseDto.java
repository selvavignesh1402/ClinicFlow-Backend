package com.HospitalManagement.responsedto;

import com.HospitalManagement.enums.MedicationStatus;

public record MedicationResponseDto(
        Long medId,
        String code,
        String name,
        String formulation,
        String strength,
        String atcCode,
        Boolean controlledFlag,
        MedicationStatus status
) {
}