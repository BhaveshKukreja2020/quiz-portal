package com.quizportal.exception;

/** Thrown when authentication or authorisation fails. */
public class AuthException extends AppException {
    public AuthException(String userMessage) { super(userMessage); }
    public AuthException(String userMessage, Throwable cause) { super(userMessage, cause); }
}
