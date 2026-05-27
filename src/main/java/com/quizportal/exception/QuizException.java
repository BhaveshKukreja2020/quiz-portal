package com.quizportal.exception;

/** Thrown for quiz-flow errors (no questions found, invalid session, etc.). */
public class QuizException extends AppException {
    public QuizException(String userMessage) { super(userMessage); }
    public QuizException(String userMessage, Throwable cause) { super(userMessage, cause); }
}
