package com.HospitalManagement.enums;

public enum AppointmentStatus {
    SCHEDULED("Scheduled"),
    CHECKED_IN("Checked In"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
