package com.ohms.exception;

/**
 * DatabaseException — wraps low-level SQLExceptions into a meaningful app error.
 *
 * INTERVIEW POINT:
 *   We never let raw SQLExceptions bubble up to the servlet layer.
 *   The DAO catches them and rethrows as DatabaseException, keeping
 *   the service layer clean and infrastructure-agnostic.
 */
public class DatabaseException extends OhmsException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
