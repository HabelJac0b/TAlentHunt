package com.talenthunt.util;

import com.talenthunt.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Utility class providing string handling and validation logic in Java.
 * Addressed in Phase 1 (Core Application Setup & Design) and Phase 2.
 */
public class ValidationUtil {

    // RFC 5322 compatible simplified email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    // Alphanumeric, spaces, dots, dashes allowed for names
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9 ._\\-()]{2,60}$"
    );

    /**
     * Checks if a string is null or empty after trimming.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates an email address.
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a password (minimum length: 6 characters).
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.trim().length() >= 6;
    }

    /**
     * Validates a username or display name.
     */
    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Sanitizes input to prevent basic HTML / script injection.
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }

    /**
     * Validates user registration fields and throws ValidationException if invalid.
     */
    public static void validateRegistration(String name, String email, String password) throws ValidationException {
        if (isNullOrEmpty(name)) {
            throw new ValidationException("name", "Full Name is required and cannot be blank.");
        }
        if (!isValidName(name)) {
            throw new ValidationException("name", "Name contains invalid characters or is too short (min 2 chars).");
        }
        if (isNullOrEmpty(email)) {
            throw new ValidationException("email", "Email address is required.");
        }
        if (!isValidEmail(email)) {
            throw new ValidationException("email", "Invalid email format. Expected example: user@domain.com");
        }
        if (isNullOrEmpty(password)) {
            throw new ValidationException("password", "Password cannot be empty.");
        }
        if (!isValidPassword(password)) {
            throw new ValidationException("password", "Password must be at least 6 characters long.");
        }
    }

    /**
     * Validates login input fields.
     */
    public static void validateLogin(String emailOrUsername, String password) throws ValidationException {
        if (isNullOrEmpty(emailOrUsername)) {
            throw new ValidationException("emailOrUsername", "Email or username is required.");
        }
        if (isNullOrEmpty(password)) {
            throw new ValidationException("password", "Password is required.");
        }
    }
}
