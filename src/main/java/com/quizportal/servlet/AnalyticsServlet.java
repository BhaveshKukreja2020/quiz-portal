package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.dto.AdminAnalyticsDTO;
import com.quizportal.service.AnalyticsService;
import com.quizportal.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GET /admin/analytics → admin analytics + charts dashboard
 */
@WebServlet(urlPatterns = {"/admin/analytics"})
public class AnalyticsServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AnalyticsServlet.class.getName());
    private AnalyticsService analyticsService;

    @Override public void init() { analyticsService = new AnalyticsService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (SessionUtil.requireAdmin(req, res)) return;

        try {
            AdminAnalyticsDTO dto = analyticsService.getAdminAnalytics();
            req.setAttribute("analytics", dto);
            req.getRequestDispatcher("/WEB-INF/views/admin/analytics.jsp").forward(req, res);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error loading admin analytics", e);
            throw new ServletException("Error loading analytics.", e);
        }
    }
}
