package com.talenthunt.exception;

/**
 * Exception thrown when user input validation fails.
 * Part of Phase 2: Structured Programming and Reliability.
 */
public class ValidationException extends Exception {
    private String fieldName;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
