package com.ohms.exception;

/**
 * AuthException — thrown for authentication/authorization failures.
 *
 * Examples:
 *   - Wrong password
 *   - Expired JWT token
 *   - Unauthorized role access
 */
public class AuthException extends OhmsException {

    public static final int INVALID_CREDENTIALS  = 1001;
    public static final int TOKEN_EXPIRED        = 1002;
    public static final int TOKEN_INVALID        = 1003;
    public static final int ACCESS_DENIED        = 1004;
    public static final int ACCOUNT_DISABLED     = 1005;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, int errorCode) {
        super(message, errorCode);
    }

    public AuthException(String message, int errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
