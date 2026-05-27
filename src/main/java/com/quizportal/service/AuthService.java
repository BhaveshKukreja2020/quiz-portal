package com.quizportal.service;

import com.quizportal.config.AppConstants;
import com.quizportal.dao.UserDAO;
import com.quizportal.exception.AuthException;
import com.quizportal.exception.ValidationException;
import com.quizportal.model.User;
import com.quizportal.validator.InputValidator;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business logic for authentication: register, login, role resolution.
 *
 * Servlets delegate here; this class talks to the DAO layer.
 * No HTTP types — fully testable without a container.
 */
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;

    public AuthService() { this.userDAO = new UserDAO(); }
    public AuthService(UserDAO userDAO) { this.userDAO = userDAO; }

    /**
     * Register a new student account.
     *
     * @return the newly created user's ID
     * @throws ValidationException if inputs are invalid
     * @throws AuthException       if e-mail is already taken
     */
    public int register(String name, String email, String password)
            throws ValidationException, AuthException {
        // Validate first
        InputValidator.validateRegistration(name, email, password);

        String normEmail = email.trim().toLowerCase();
        try {
            int id = userDAO.register(name.trim(), normEmail, password);
            if (id < 0) {
                LOG.info("Registration failed — duplicate email: " + normEmail);
                throw new AuthException(AppConstants.MSG_EMAIL_TAKEN);
            }
            LOG.info("New student registered: id=" + id + " email=" + normEmail);
            return id;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error during registration for " + normEmail, e);
            throw new AuthException("Registration failed due to a server error. Please try again.", e);
        }
    }

    /**
     * Authenticate and return the user.
     *
     * @throws ValidationException if inputs are blank
     * @throws AuthException       if credentials are wrong
     */
    public User login(String email, String password)
            throws ValidationException, AuthException {
        InputValidator.validateLogin(email, password);

        String normEmail = email.trim().toLowerCase();
        try {
            User user = userDAO.login(normEmail, password);
            if (user == null) {
                LOG.warning("Failed login attempt for: " + normEmail);
                throw new AuthException(AppConstants.MSG_LOGIN_FAILED);
            }
            LOG.info("Successful login: userId=" + user.getId() + " role=" + user.getRole());
            return user;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error during login for " + normEmail, e);
            throw new AuthException("Login failed due to a server error. Please try again.", e);
        }
    }

    /**
     * Returns the correct post-login redirect path for the given user's role.
     */
    public static String getHomeUrl(User user, String contextPath) {
        return contextPath + (user.isAdmin()
                ? AppConstants.URL_ADMIN_DASH
                : AppConstants.URL_STUDENT_DASH);
    }
}
