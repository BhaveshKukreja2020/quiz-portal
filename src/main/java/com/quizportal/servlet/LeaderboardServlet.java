package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.dao.LeaderboardDAO;
import com.quizportal.model.User;
import com.quizportal.service.LeaderboardService;
import com.quizportal.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GET /leaderboard → global leaderboard page (students + admin can view)
 */
@WebServlet(urlPatterns = {"/leaderboard"})
public class LeaderboardServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(LeaderboardServlet.class.getName());
    private LeaderboardService lbService;

    @Override public void init() { lbService = new LeaderboardService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (SessionUtil.requireLogin(req, res)) return;

        User user = SessionUtil.getUser(req);

        try {
            List<LeaderboardDAO.LeaderboardEntry> top = lbService.getTopAll(20);
            int myRank = lbService.getUserRank(user.getId());
            req.setAttribute("leaderboard", top);
            req.setAttribute("myRank",      myRank);
            req.getRequestDispatcher("/WEB-INF/views/leaderboard.jsp").forward(req, res);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error loading leaderboard", e);
            throw new ServletException("Error loading leaderboard.", e);
        }
    }
}
