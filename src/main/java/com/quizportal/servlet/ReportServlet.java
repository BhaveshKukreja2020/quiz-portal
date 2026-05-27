package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.dto.QuizReportDTO;
import com.quizportal.exception.QuizException;
import com.quizportal.model.User;
import com.quizportal.service.LeaderboardService;
import com.quizportal.service.ReportService;
import com.quizportal.util.SessionUtil;
import com.quizportal.validator.InputValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GET /report?sessionId=UUID  → full detailed report page
 * GET /report/export?sessionId=UUID → printable HTML report
 */
@WebServlet(urlPatterns = {"/report"})
public class ReportServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ReportServlet.class.getName());

    private ReportService      reportService;
    private LeaderboardService lbService;

    @Override
    public void init() {
        reportService = new ReportService();
        lbService     = new LeaderboardService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (SessionUtil.requireLogin(req, res)) return;

        User   user      = SessionUtil.getUser(req);
        String sessionId = req.getParameter("sessionId");

        if (InputValidator.isBlank(sessionId)) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_STUDENT_DASH);
            return;
        }

        try {
            QuizReportDTO report = reportService.buildReport(sessionId, user.getId());

            // Refresh leaderboard after every completed quiz
            lbService.refreshForUser(user);
            int rank = lbService.getUserRank(user.getId());

            req.setAttribute("report",      report);
            req.setAttribute("userRank",    rank);
            req.setAttribute("pageTitle",   "Quiz Report");
            req.getRequestDispatcher("/WEB-INF/views/quiz/report.jsp").forward(req, res);

        } catch (QuizException e) {
            req.setAttribute("error", e.getUserMessage());
            res.sendRedirect(req.getContextPath() + AppConstants.URL_STUDENT_DASH);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error building report for session " + sessionId, e);
            throw new ServletException("Error generating report.", e);
        }
    }
}
