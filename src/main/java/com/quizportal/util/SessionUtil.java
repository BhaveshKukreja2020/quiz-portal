package com.quizportal.util;

import com.quizportal.config.AppConstants;
import com.quizportal.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Secure session management utilities.
 *
 * Centralised guards prevent scattered session logic across servlets.
 */
public final class SessionUtil {

    private SessionUtil() {}

    /** Store the authenticated user in a fresh session. */
    public static void setUser(HttpServletRequest req, User user) {
        // Always create new session after auth to avoid session fixation
        HttpSession session = req.getSession(true);
        session.setAttribute(AppConstants.SESSION_USER, user);
        session.setMaxInactiveInterval(AppConstants.SESSION_TIMEOUT_MIN * 60);
    }

    /** Retrieve user from session, or null if not authenticated. */
    public static User getUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object attr = session.getAttribute(AppConstants.SESSION_USER);
        return (attr instanceof User) ? (User) attr : null;
    }

    /** Safely invalidate session on logout. */
    public static void invalidate(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            try { session.invalidate(); }
            catch (IllegalStateException ignored) {}  // already invalidated
        }
    }

    /**
     * Guard: redirect to /login if not authenticated.
     * @return true if guard triggered (caller must return immediately)
     */
    public static boolean requireLogin(HttpServletRequest req,
                                       HttpServletResponse res) throws IOException {
        if (getUser(req) == null) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_LOGIN);
            return true;
        }
        return false;
    }

    /**
     * Guard: redirect if not an admin.
     * @return true if guard triggered
     */
    public static boolean requireAdmin(HttpServletRequest req,
                                       HttpServletResponse res) throws IOException {
        User u = getUser(req);
        if (u == null) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_LOGIN);
            return true;
        }
        if (!u.isAdmin()) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_STUDENT_DASH);
            return true;
        }
        return false;
    }
}
