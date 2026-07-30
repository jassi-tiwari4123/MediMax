package com.ohms.enums;

/**
 * DoctorStatus — Admin controls whether a doctor can receive appointments.
 *
 * PENDING   — newly registered, awaiting admin approval
 * APPROVED  — active and visible to patients
 * REJECTED  — rejected by admin
 * INACTIVE  — temporarily disabled by admin
 */
public enum DoctorStatus {

    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    INACTIVE("Inactive");

    private final String displayName;

    DoctorStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DoctorStatus fromString(String value) {
        for (DoctorStatus s : values()) {
            if (s.name().equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown doctor status: " + value);
    }
}
