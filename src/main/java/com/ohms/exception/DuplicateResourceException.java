package com.ohms.exception;

/**
 * DuplicateResourceException — thrown when a unique constraint is violated.
 *
 * Examples:
 *   - Email already registered
 *   - Phone number already in use
 *   - Doctor already has an appointment at the same slot
 */
public class DuplicateResourceException extends OhmsException {

    private final String field;   // which field caused the conflict
    private final String value;   // what value conflicted

    public DuplicateResourceException(String field, String value) {
        super(field + " '" + value + "' already exists.");
        this.field = field;
        this.value = value;
    }

    public String getField()  { return field; }
    public String getValue()  { return value; }
}
