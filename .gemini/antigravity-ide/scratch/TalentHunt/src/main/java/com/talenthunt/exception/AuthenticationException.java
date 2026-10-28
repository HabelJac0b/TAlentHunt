package com.talenthunt.exception;

/**
 * Exception thrown when user credentials or role verification fails.
 * Part of Phase 2: Structured Programming and Reliability.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
