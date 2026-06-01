package com.HospitalManagement.enums;

public enum PrescriptionStatus {
    DRAFT("Draft"),
    ISSUED("Issued"),
    DISPENSED("Dispensed"),
    CANCELLED("Cancelled");

    private final String displayName;

    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
