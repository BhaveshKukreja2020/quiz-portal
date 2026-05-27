package com.quizportal.dao;

import com.quizportal.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Data-access for the {@code leaderboard} table.
 * Provides ranked student lists by period and topic.
 */
public class LeaderboardDAO {

    /** Single leaderboard row view model. */
    public static class LeaderboardEntry {
        private int    rank;
        private int    userId;
        private String userName;
        private int    totalScore;
        private int    totalQuizzes;
        private double avgAccuracy;
        private int    bestScore;

        // ── PUBLIC GETTERS (Required by JSP Expression Language) ──
        public int getRank() { return rank; }
        public int getUserId() { return userId; }
        public String getUserName() { return userName; }
        public int getTotalScore() { return totalScore; }
        public int getTotalQuizzes() { return totalQuizzes; }
        public double getAvgAccuracy() { return avgAccuracy; }
        public int getBestScore() { return bestScore; }

        // ── PUBLIC SETTERS ──
        public void setRank(int rank) { this.rank = rank; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setUserName(String userName) { this.userName = userName; }
        public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
        public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }
        public void setAvgAccuracy(double avgAccuracy) { this.avgAccuracy = avgAccuracy; }
        public void setBestScore(int bestScore) { this.bestScore = bestScore; }
    }

    /** Upsert leaderboard entry for one student (all_time, overall). */
    public void upsertOverall(int userId, String userName,
                              int totalScore, int totalQuizzes,
                              double avgAccuracy, int bestScore) throws SQLException {
        final String sql =
                "INSERT INTO leaderboard " +
                        "(user_id, user_name, total_score, total_quizzes, avg_accuracy, best_score, tag_id, period) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NULL, 'all_time') " +
                        "ON DUPLICATE KEY UPDATE " +
                        "  user_name    = VALUES(user_name), " +
                        "  total_score  = VALUES(total_score), " +
                        "  total_quizzes= VALUES(total_quizzes), " +
                        "  avg_accuracy = VALUES(avg_accuracy), " +
                        "  best_score   = VALUES(best_score)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, userName);
            ps.setInt(3, totalScore);
            ps.setInt(4, totalQuizzes);
            ps.setDouble(5, avgAccuracy);
            ps.setInt(6, bestScore);
            ps.executeUpdate();
        }
    }

    /** Top-N all-time leaderboard (overall). */
    public List<LeaderboardEntry> getTopAll(int limit) throws SQLException {
        return getTop(limit, null, "all_time");
    }

    /** Top-N for a specific tag. */
    public List<LeaderboardEntry> getTopByTag(int tagId, int limit) throws SQLException {
        return getTop(limit, tagId, "all_time");
    }

    private List<LeaderboardEntry> getTop(int limit, Integer tagId, String period)
            throws SQLException {
        String sql =
                "SELECT user_id, user_name, total_score, total_quizzes, avg_accuracy, best_score " +
                        "FROM leaderboard " +
                        "WHERE period = ? AND " + (tagId == null ? "tag_id IS NULL" : "tag_id = ?") + " " +
                        "ORDER BY total_score DESC, avg_accuracy DESC " +
                        "LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, period);
            if (tagId != null) ps.setInt(idx++, tagId);
            ps.setInt(idx, limit);
            List<LeaderboardEntry> list = new ArrayList<>();
            int rank = 1;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LeaderboardEntry e = new LeaderboardEntry();
                    e.setRank(rank++);
                    e.setUserId(rs.getInt("user_id"));
                    e.setUserName(rs.getString("user_name"));
                    e.setTotalScore(rs.getInt("total_score"));
                    e.setTotalQuizzes(rs.getInt("total_quizzes"));
                    e.setAvgAccuracy(rs.getDouble("avg_accuracy"));
                    e.setBestScore(rs.getInt("best_score"));
                    list.add(e);
                }
            }
            return list;
        }
    }

    /** Get the rank of a specific user (overall all_time). */
    public int getUserRank(int userId) throws SQLException {
        final String sql =
                "SELECT COUNT(*)+1 AS rnk FROM leaderboard " +
                        "WHERE period='all_time' AND tag_id IS NULL " +
                        "  AND total_score > (SELECT COALESCE(total_score,0) FROM leaderboard " +
                        "                     WHERE user_id=? AND period='all_time' AND tag_id IS NULL " +
                        "                     LIMIT 1)"; // Restricts subquery to 1 row to prevent database crashes
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("rnk") : 0;
            }
        }
    }
}