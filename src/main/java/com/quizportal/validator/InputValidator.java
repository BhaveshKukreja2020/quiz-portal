package com.quizportal.validator;

import com.quizportal.config.AppConstants;
import com.quizportal.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Centralised input validation.
 *
 * All validation methods throw {@link ValidationException} with user-friendly
 * messages on failure, so callers can catch a single exception type.
 */
public final class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    private InputValidator() {}

    // ── Auth validation ────────────────────────────────────────────────────

    public static void validateRegistration(String name, String email, String password)
            throws ValidationException {
        requireNonBlank(name,     "Full name");
        validateEmail(email);
        validatePassword(password);
    }

    public static void validateLogin(String email, String password)
            throws ValidationException {
        if (isBlank(email) || isBlank(password)) {
            throw new ValidationException("Please enter your e-mail and password.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        requireNonBlank(email, "E-mail address");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Please enter a valid e-mail address.");
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        if (isBlank(password)) {
            throw new ValidationException(AppConstants.MSG_FIELDS_REQUIRED);
        }
        if (password.length() < AppConstants.PASSWORD_MIN_LENGTH) {
            throw new ValidationException(AppConstants.MSG_PASSWORD_TOO_SHORT);
        }
    }

    // ── Question validation ────────────────────────────────────────────────

    public static void validateQuestion(String questionText,
                                        String opt1, String opt2,
                                        String opt3, String opt4,
                                        String correctAnswer,
                                        String difficulty)
            throws ValidationException {
        requireNonBlank(questionText, "Question text");
        requireNonBlank(opt1, "Option A");
        requireNonBlank(opt2, "Option B");
        requireNonBlank(opt3, "Option C");
        requireNonBlank(opt4, "Option D");

        int ca = parseIntOrZero(correctAnswer);
        if (ca < 1 || ca > 4) {
            throw new ValidationException("Correct answer must be between 1 and 4.");
        }

        if (!("easy".equals(difficulty) || "medium".equals(difficulty) || "hard".equals(difficulty))) {
            throw new ValidationException("Difficulty must be easy, medium, or hard.");
        }
    }

    // ── Tag validation ─────────────────────────────────────────────────────

    public static void validateTagName(String tagName) throws ValidationException {
        requireNonBlank(tagName, "Tag name");
        if (tagName.trim().length() > 80) {
            throw new ValidationException("Tag name must be 80 characters or fewer.");
        }
    }

    // ── General helpers ────────────────────────────────────────────────────

    public static void requireNonBlank(String value, String fieldName)
            throws ValidationException {
        if (isBlank(value)) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static int parseIntRequired(String s, String fieldName)
            throws ValidationException {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            throw new ValidationException(fieldName + " must be a valid number.");
        }
    }

    public static int parseIntOrDefault(String s, int def) {
        if (isBlank(s)) return def;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    private static int parseIntOrZero(String s) {
        return parseIntOrDefault(s, 0);
    }
}
