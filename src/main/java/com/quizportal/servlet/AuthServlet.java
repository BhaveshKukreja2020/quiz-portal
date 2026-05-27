package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.exception.AuthException;
import com.quizportal.exception.ValidationException;
import com.quizportal.model.User;
import com.quizportal.service.AuthService;
import com.quizportal.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Thin controller for /register, /login, /logout.
 * Zero business logic — delegates entirely to AuthService.
 */
@WebServlet(urlPatterns = {"/register", "/login", "/logout"})
public class AuthServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AuthServlet.class.getName());
    private AuthService authService;

    @Override public void init() { authService = new AuthService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/logout":
                LOG.info("Logout: " + userDesc(req));
                SessionUtil.invalidate(req);
                res.sendRedirect(req.getContextPath() + AppConstants.URL_LOGIN);
                break;
            case "/register":
                if (SessionUtil.getUser(req) != null) { redirectHome(req, res); return; }
                forward(req, res, AppConstants.VIEW_REGISTER);
                break;
            default:
                if (SessionUtil.getUser(req) != null) { redirectHome(req, res); return; }
                forward(req, res, AppConstants.VIEW_LOGIN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if ("/register".equals(req.getServletPath())) handleRegister(req, res);
        else                                           handleLogin(req, res);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        try {
            authService.register(req.getParameter("name"),
                                 req.getParameter("email"),
                                 req.getParameter("password"));
            req.setAttribute("success", AppConstants.MSG_REG_SUCCESS);
            forward(req, res, AppConstants.VIEW_LOGIN);
        } catch (ValidationException | AuthException e) {
            req.setAttribute("error", e.getUserMessage());
            forward(req, res, AppConstants.VIEW_REGISTER);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        try {
            User user = authService.login(req.getParameter("email"),
                                          req.getParameter("password"));
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            SessionUtil.setUser(req, user);
            res.sendRedirect(AuthService.getHomeUrl(user, req.getContextPath()));
        } catch (ValidationException | AuthException e) {
            req.setAttribute("error", e.getUserMessage());
            forward(req, res, AppConstants.VIEW_LOGIN);
        }
    }

    private void redirectHome(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.sendRedirect(AuthService.getHomeUrl(SessionUtil.getUser(req), req.getContextPath()));
    }

    private static void forward(HttpServletRequest req, HttpServletResponse res, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(view).forward(req, res);
    }

    private static String userDesc(HttpServletRequest req) {
        User u = SessionUtil.getUser(req);
        return u == null ? "anonymous" : "userId=" + u.getId();
    }
}
