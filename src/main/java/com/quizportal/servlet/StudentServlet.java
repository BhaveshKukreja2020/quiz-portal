package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.dto.StudentDashboardDTO;
import com.quizportal.model.User;
import com.quizportal.service.StudentService;
import com.quizportal.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin controller for /student/* routes.
 * Delegates to StudentService.
 */
@WebServlet(urlPatterns = {"/student/*"})
public class StudentServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(StudentServlet.class.getName());
    private StudentService studentService;

    @Override public void init() { studentService = new StudentService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, res)) return;

        User user = SessionUtil.getUser(req);
        if (user.isAdmin()) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_ADMIN_DASH);
            return;
        }

        String sub = req.getPathInfo();
        if (sub == null || sub.equals("/") || sub.equals("/dashboard")) {
            try {
                StudentDashboardDTO dto = studentService.getDashboard(user.getId());
                req.setAttribute("resumable",        dto.getResumable());
                req.setAttribute("recentSessions",   dto.getRecentSessions());
                req.setAttribute("topicStats",        dto.getTopicStats());
                req.setAttribute("leaderboardRank",   dto.getLeaderboardRank());
                req.setAttribute("totalQuizzes",      dto.getTotalQuizzes());
                req.setAttribute("avgAccuracy",       dto.getAvgAccuracy());
                req.setAttribute("bestScore",         dto.getBestScore());
                req.getRequestDispatcher(AppConstants.VIEW_STUDENT_DASH).forward(req, res);
            } catch (SQLException e) {
                LOG.log(Level.SEVERE, "DB error loading student dashboard", e);
                throw new ServletException("Error loading dashboard.", e);
            }
        } else {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
