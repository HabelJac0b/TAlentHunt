package com.talenthunt.service.impl;

import com.talenthunt.dao.TalentDao;
import com.talenthunt.exception.AuthenticationException;
import com.talenthunt.exception.UserAlreadyExistsException;
import com.talenthunt.exception.ValidationException;
import com.talenthunt.model.User;
import com.talenthunt.service.UserOperations;
import com.talenthunt.util.ValidationUtil;

import java.util.List;

/**
 * Implementation of UserOperations interface.
 * Implements Phase 2: Structured Programming and Reliability (CLO2).
 */
public class UserServiceImpl implements UserOperations {

    private final TalentDao talentDao;

    public UserServiceImpl() {
        this.talentDao = new TalentDao();
    }

    public UserServiceImpl(TalentDao talentDao) {
        this.talentDao = talentDao;
    }

    @Override
    public User authenticate(String emailOrUsername, String password, String targetRole)
            throws AuthenticationException, ValidationException {
        
        // 1. Validation Logic in Java (String Handling)
        ValidationUtil.validateLogin(emailOrUsername, password);

        // 2. Query Data Layer
        User user = talentDao.authenticateUser(emailOrUsername, password, targetRole);
        if (user == null) {
            throw new AuthenticationException("Invalid username/email or password.");
        }

        // 3. Role-based Access Check
        if (targetRole != null && "ADMIN".equalsIgnoreCase(targetRole) && !user.isAdmin()) {
            throw new AuthenticationException("Access Denied: Administrative privileges required.");
        }

        return user;
    }

    @Override
    public User register(User user) throws ValidationException, UserAlreadyExistsException {
        if (user == null) {
            throw new ValidationException("User object cannot be null.");
        }

        // 1. Basic validation logic in Java (string handling)
        validateUser(user);

        // 2. Check duplicate email existence
        if (!isEmailAvailable(user.getEmail())) {
            throw new UserAlreadyExistsException("The email address '" + user.getEmail() + "' is already registered.");
        }

        // 3. Execute persistence via DAO
        User savedUser = talentDao.registerUser(
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole() != null ? user.getRole() : "CONTESTANT"
        );

        if (savedUser == null) {
            throw new ValidationException("Registration could not be completed due to database error.");
        }

        return savedUser;
    }

    @Override
    public void validateUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User cannot be null.");
        }
        ValidationUtil.validateRegistration(user.getName(), user.getEmail(), user.getPassword());
    }

    @Override
    public boolean isEmailAvailable(String email) {
        if (ValidationUtil.isNullOrEmpty(email)) return false;
        return !talentDao.isEmailRegistered(email);
    }

    @Override
    public User getUserById(int id) {
        if (id <= 0) return null;
        return talentDao.getUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        if (ValidationUtil.isNullOrEmpty(email)) return null;
        return talentDao.getUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return talentDao.getUsers();
    }
}
