package com.quizportal.exception;

/** Thrown when input validation fails. */
public class ValidationException extends AppException {
    public ValidationException(String userMessage) { super(userMessage); }
}
