package com.quizportal.config;

/**
 * Application-wide constants.
 *
 * Single source of truth — no hardcoded strings scattered across the codebase.
 * All values are package-visible so they can be used by any layer without
 * introducing circular dependencies.
 */
public final class AppConstants {

    private AppConstants() { /* utility class */ }

    // ── Session ────────────────────────────────────────────────────────────
    /** HttpSession attribute key that stores the logged-in User object. */
    public static final String SESSION_USER       = "loggedInUser";
    /** Session idle timeout in minutes (mirrors web.xml). */
    public static final int    SESSION_TIMEOUT_MIN = 30;

    // ── Roles ─────────────────────────────────────────────────────────────
    public static final String ROLE_ADMIN   = "admin";
    public static final String ROLE_STUDENT = "student";

    // ── Quiz settings ──────────────────────────────────────────────────────
    public static final int    QUESTIONS_PER_QUIZ  = 10;
    public static final int    QUIZ_DURATION_SECS  = 600;   // 10 minutes
    public static final int    MAX_WARNINGS        = 3;
    public static final String DIFFICULTY_EASY     = "easy";
    public static final String DIFFICULTY_MEDIUM   = "medium";
    public static final String DIFFICULTY_HARD     = "hard";

    // ── Quiz status ────────────────────────────────────────────────────────
    public static final String STATUS_IN_PROGRESS    = "in_progress";
    public static final String STATUS_COMPLETED      = "completed";
    public static final String STATUS_AUTO_SUBMITTED = "auto_submitted";

    // ── View paths ─────────────────────────────────────────────────────────
    public static final String VIEW_LOGIN          = "/WEB-INF/views/auth/login.jsp";
    public static final String VIEW_REGISTER       = "/WEB-INF/views/auth/register.jsp";
    public static final String VIEW_STUDENT_DASH   = "/WEB-INF/views/student/dashboard.jsp";
    public static final String VIEW_ADMIN_DASH     = "/WEB-INF/views/admin/dashboard.jsp";
    public static final String VIEW_ADMIN_QUESTIONS= "/WEB-INF/views/admin/questions.jsp";
    public static final String VIEW_ADMIN_QFORM    = "/WEB-INF/views/admin/question_form.jsp";
    public static final String VIEW_ADMIN_TAGS     = "/WEB-INF/views/admin/tags.jsp";
    public static final String VIEW_ADMIN_REPORTS  = "/WEB-INF/views/admin/reports.jsp";
    public static final String VIEW_QUIZ_TAGS      = "/WEB-INF/views/quiz/tags.jsp";
    public static final String VIEW_QUIZ           = "/WEB-INF/views/quiz/quiz.jsp";
    public static final String VIEW_QUIZ_RESULT    = "/WEB-INF/views/quiz/result.jsp";
    public static final String VIEW_QUIZ_HISTORY   = "/WEB-INF/views/quiz/history.jsp";
    public static final String VIEW_ERROR_404      = "/WEB-INF/views/error/404.jsp";
    public static final String VIEW_ERROR_500      = "/WEB-INF/views/error/500.jsp";

    // ── URL paths ──────────────────────────────────────────────────────────
    public static final String URL_LOGIN           = "/login";
    public static final String URL_REGISTER        = "/register";
    public static final String URL_LOGOUT          = "/logout";
    public static final String URL_STUDENT_DASH    = "/student/dashboard";
    public static final String URL_ADMIN_DASH      = "/admin/dashboard";
    public static final String URL_QUIZ_TAGS       = "/quiz/tags";
    public static final String URL_QUIZ_RESUME     = "/quiz/resume";
    public static final String URL_QUIZ_RESULT     = "/quiz/result";

    // ── Password rules ─────────────────────────────────────────────────────
    public static final int    PASSWORD_MIN_LENGTH = 6;
    public static final int    BCRYPT_ROUNDS       = 10;

    // ── Validation messages ────────────────────────────────────────────────
    public static final String MSG_LOGIN_FAILED      = "Invalid e-mail address or password.";
    public static final String MSG_EMAIL_TAKEN       = "That e-mail address is already registered.";
    public static final String MSG_REG_SUCCESS       = "Registration successful! Please log in.";
    public static final String MSG_FIELDS_REQUIRED   = "All fields are required.";
    public static final String MSG_PASSWORD_TOO_SHORT =
            "Password must be at least " + PASSWORD_MIN_LENGTH + " characters.";
    public static final String MSG_NO_TAGS_SELECTED  = "Please select at least one topic.";
    public static final String MSG_NO_QUESTIONS      =
            "No questions found for the selected topics. Ask an admin to add questions first.";
}
