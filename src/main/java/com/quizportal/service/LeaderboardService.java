package com.quizportal.service;

import com.quizportal.dao.LeaderboardDAO;
import com.quizportal.dao.QuizDAO;
import com.quizportal.model.QuizSession;
import com.quizportal.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Refreshes and queries the leaderboard.
 * Called after every quiz completion to keep rankings current.
 */
public class LeaderboardService {

    private static final Logger LOG = Logger.getLogger(LeaderboardService.class.getName());

    private final LeaderboardDAO lbDAO;
    private final QuizDAO        quizDAO;

    public LeaderboardService() {
        this.lbDAO   = new LeaderboardDAO();
        this.quizDAO = new QuizDAO();
    }

    /**
     * Recompute and upsert leaderboard entry for a student
     * after they finish a quiz.
     */
    public void refreshForUser(User user) throws SQLException {
        List<QuizSession> sessions = quizDAO.getCompletedByUser(user.getId());
        if (sessions.isEmpty()) return;

        int totalScore = 0, bestScore = 0, totalQuizzes = sessions.size();
        double totalAccuracy = 0;

        for (QuizSession s : sessions) {
            totalScore    += s.getScore();
            totalAccuracy += s.getAccuracy();
            if (s.getScore() > bestScore) bestScore = s.getScore();
        }

        double avgAccuracy = totalQuizzes > 0 ? totalAccuracy / totalQuizzes : 0;

        lbDAO.upsertOverall(user.getId(), user.getName(),
                totalScore, totalQuizzes, avgAccuracy, bestScore);

        LOG.info("Leaderboard refreshed for userId=" + user.getId()
                + " totalScore=" + totalScore + " avgAcc=" + String.format("%.1f", avgAccuracy));
    }

    public List<LeaderboardDAO.LeaderboardEntry> getTopAll(int limit) throws SQLException {
        return lbDAO.getTopAll(limit);
    }

    public int getUserRank(int userId) throws SQLException {
        return lbDAO.getUserRank(userId);
    }
}
