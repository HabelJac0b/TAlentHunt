package com.talenthunt.exception;

/**
 * Exception thrown when attempting to register an already existing email/username.
 * Part of Phase 2: Structured Programming and Reliability.
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
