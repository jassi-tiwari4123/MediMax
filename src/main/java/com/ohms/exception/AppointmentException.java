package com.ohms.exception;

/**
 * AppointmentException — business rule violations specific to appointments.
 *
 * Examples:
 *   - Doctor not available at requested slot
 *   - Double booking attempt
 *   - Cancelling an already-completed appointment
 */
public class AppointmentException extends OhmsException {

    public static final int SLOT_UNAVAILABLE    = 2001;
    public static final int DOUBLE_BOOKING      = 2002;
    public static final int INVALID_STATUS_CHANGE = 2003;
    public static final int PAST_DATE           = 2004;

    public AppointmentException(String message) {
        super(message);
    }

    public AppointmentException(String message, int errorCode) {
        super(message, errorCode);
    }
}
