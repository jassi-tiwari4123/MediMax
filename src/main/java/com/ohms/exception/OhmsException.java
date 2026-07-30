package com.ohms.exception;

/**
 * OhmsException — base checked exception for the entire application.
 *
 * INTERVIEW POINT:
 *   Custom exception hierarchy lets us catch application-level errors
 *   separately from infrastructure errors (like SQLException).
 *   All OHMS exceptions extend this class (Inheritance pattern).
 */
public class OhmsException extends Exception {

    private final int errorCode;

    public OhmsException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public OhmsException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OhmsException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 0;
    }

    public OhmsException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
