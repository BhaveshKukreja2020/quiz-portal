package com.quizportal.dao;

import com.quizportal.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Data-access for per-question difficulty analytics.
 */
public class QuestionAnalyticsDAO {

    public static class QuestionStat {
        private int    questionId;
        private String questionText;
        private int    timesShown;
        private int    timesCorrect;
        private double avgTimeSecs;
        private double difficultyIndex; // 0=hardest, 1=easiest
        private double correctRate;

        // ── PUBLIC GETTERS (Required by JSP Expression Language) ──
        public int getQuestionId() { return questionId; }
        public String getQuestionText() { return questionText; }
        public int getTimesShown() { return timesShown; }
        public int getTimesCorrect() { return timesCorrect; }
        public double getAvgTimeSecs() { return avgTimeSecs; }
        public double getDifficultyIndex() { return difficultyIndex; }
        public double getCorrectRate() { return correctRate; }

        // ── PUBLIC SETTERS ──
        public void setQuestionId(int questionId) { this.questionId = questionId; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public void setTimesShown(int timesShown) { this.timesShown = timesShown; }
        public void setTimesCorrect(int timesCorrect) { this.timesCorrect = timesCorrect; }
        public void setAvgTimeSecs(double avgTimeSecs) { this.avgTimeSecs = avgTimeSecs; }
        public void setDifficultyIndex(double difficultyIndex) { this.difficultyIndex = difficultyIndex; }
        public void setCorrectRate(double correctRate) { this.correctRate = correctRate; }
    }

    /** Upsert stats after a question is answered. */
    public void recordAnswer(int questionId, boolean correct, int timeSecs) throws SQLException {
        final String sql =
                "INSERT INTO question_analytics (question_id, times_shown, times_correct, avg_time_secs, difficulty_index) " +
                        "VALUES (?, 1, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "  times_shown    = times_shown + 1, " +
                        "  times_correct  = times_correct + VALUES(times_correct), " +
                        "  avg_time_secs  = ROUND((avg_time_secs * (times_shown - 1) + VALUES(avg_time_secs)) / times_shown, 1), " +
                        "  difficulty_index = ROUND(times_correct / GREATEST(times_shown, 1), 2)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.setInt(2, correct ? 1 : 0);
            ps.setInt(3, timeSecs);
            ps.setDouble(4, correct ? 1.0 : 0.0);
            ps.executeUpdate();
        }
    }

    /** Hardest questions (lowest difficulty_index). */
    public List<QuestionStat> getHardest(int limit) throws SQLException {
        return getOrdered("difficulty_index ASC", limit);
    }

    /** Easiest questions (highest difficulty_index). */
    public List<QuestionStat> getEasiest(int limit) throws SQLException {
        return getOrdered("difficulty_index DESC", limit);
    }

    /** Overall question analytics for admin. */
    public List<QuestionStat> getAll(int limit) throws SQLException {
        return getOrdered("times_shown DESC", limit);
    }

    private List<QuestionStat> getOrdered(String orderClause, int limit) throws SQLException {
        final String sql =
                "SELECT qa.question_id, q.question_text, qa.times_shown, qa.times_correct, " +
                        "       qa.avg_time_secs, qa.difficulty_index, " +
                        "       ROUND(qa.times_correct * 100.0 / GREATEST(qa.times_shown,1), 1) AS correct_rate " +
                        "FROM question_analytics qa " +
                        "JOIN questions q ON qa.question_id = q.id " +
                        "WHERE qa.times_shown > 0 " +
                        "ORDER BY " + orderClause + " LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            List<QuestionStat> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionStat s = new QuestionStat();
                    // Using the new setters to populate the object safely
                    s.setQuestionId(rs.getInt("question_id"));
                    s.setQuestionText(rs.getString("question_text"));
                    s.setTimesShown(rs.getInt("times_shown"));
                    s.setTimesCorrect(rs.getInt("times_correct"));
                    s.setAvgTimeSecs(rs.getDouble("avg_time_secs"));
                    s.setDifficultyIndex(rs.getDouble("difficulty_index"));
                    s.setCorrectRate(rs.getDouble("correct_rate"));
                    list.add(s);
                }
            }
            return list;
        }
    }
}