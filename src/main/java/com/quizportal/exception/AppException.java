package com.quizportal.exception;

/**
 * Base checked exception for all application-level errors.
 * Keeps business exceptions distinct from infrastructure exceptions (SQLException etc.).
 */
public class AppException extends Exception {

    private final String userMessage;   // safe to display to end users

    public AppException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }

    public AppException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }

    /** User-safe message — never contains stack traces or internal details. */
    public String getUserMessage() { return userMessage; }
}
