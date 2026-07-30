package com.ohms.enums;

/**
 * AppointmentStatus — lifecycle states of an appointment.
 *
 * INTERVIEW POINT:
 *   Using an Enum for status prevents invalid state values from ever
 *   entering the system. Compare this to storing "pendig" in a VARCHAR.
 *
 * Transitions:
 *   PENDING → CONFIRMED → COMPLETED
 *   PENDING → CANCELLED
 *   CONFIRMED → CANCELLED
 *   CONFIRMED → RESCHEDULED → CONFIRMED
 */
public enum AppointmentStatus {

    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    RESCHEDULED("Rescheduled");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AppointmentStatus fromString(String value) {
        for (AppointmentStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown appointment status: " + value);
    }
}
