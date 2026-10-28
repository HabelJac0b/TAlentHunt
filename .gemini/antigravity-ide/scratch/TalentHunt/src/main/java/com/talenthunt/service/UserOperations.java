package com.talenthunt.service;

import com.talenthunt.model.User;
import com.talenthunt.exception.ValidationException;
import com.talenthunt.exception.AuthenticationException;
import com.talenthunt.exception.UserAlreadyExistsException;
import java.util.List;

/**
 * Interface defining user operations (validation, authentication, registration).
 * Fulfills Phase 2: Structured Programming and Reliability (CLO2).
 */
public interface UserOperations {

    /**
     * Authenticates a user with email/username, password, and expected role/portal.
     * @throws AuthenticationException if credentials are invalid or access unauthorized.
     * @throws ValidationException if input parameters are malformed.
     */
    User authenticate(String emailOrUsername, String password, String targetRole) 
        throws AuthenticationException, ValidationException;

    /**
     * Registers a new user with validation and duplicate prevention.
     * @throws ValidationException if input validation fails.
     * @throws UserAlreadyExistsException if email is already taken.
     */
    User register(User user) 
        throws ValidationException, UserAlreadyExistsException;

    /**
     * Validates a user entity's required fields and string constraints.
     * @throws ValidationException if any validation rule is violated.
     */
    void validateUser(User user) throws ValidationException;

    /**
     * Checks if an email is available for registration.
     */
    boolean isEmailAvailable(String email);

    /**
     * Retrieves user by numeric ID.
     */
    User getUserById(int id);

    /**
     * Retrieves user by email address.
     */
    User getUserByEmail(String email);

    /**
     * Lists all registered users.
     */
    List<User> getAllUsers();
}
