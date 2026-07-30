package com.ohms.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidationException — thrown when input data fails business-rule validation.
 *
 * INTERVIEW POINT:
 *   We store multiple field-level errors in a List so the UI can display
 *   all errors at once rather than one at a time.
 *
 *   Demonstrates: Generics (List<String>), Custom Exception, Encapsulation.
 */
public class ValidationException extends OhmsException {

    /** Holds individual field error messages */
    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public ValidationException(List<String> errors) {
        super("Validation failed with " + errors.size() + " error(s)");
        this.errors = new ArrayList<>(errors);
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors); // defensive copy
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
