package com.HospitalManagement.requestdto;

import com.HospitalManagement.enums.MedicationStatus;

public record MedicationMasterRequestDto(
        String code,
        String name,
        String formulation,
        String strength,
        String atcCode,
        Boolean controlledFlag,
        MedicationStatus status   // ACTIVE / INACTIVE
) {}
