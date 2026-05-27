package com.quizportal.dao;

import com.quizportal.model.QuizProgress;
import com.quizportal.model.QuizSession;
import com.quizportal.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Data-access for quiz_sessions, attempts, and quiz_progress.
 * All JDBC resources closed via try-with-resources.
 */
public class QuizDAO {

    // ═══════════════════════════════════════════════════════════
    // SESSIONS
    // ═══════════════════════════════════════════════════════════

    public void createSession(QuizSession s) throws SQLException {
        final String sql =
            "INSERT INTO quiz_sessions " +
            "(session_id, user_id, selected_tags, total_questions, status) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSessionId());
            ps.setInt(2, s.getUserId());
            ps.setString(3, s.getSelectedTags());
            ps.setInt(4, s.getTotalQuestions());
            ps.setString(5, s.getStatus());
            ps.executeUpdate();
        }
    }

    public void finalizeSession(String sessionId, int score, double accuracy,
                                 String status) throws SQLException {
        final String sql =
            "UPDATE quiz_sessions " +
            "SET score=?, accuracy=?, status=?, completed_at=NOW() " +
            "WHERE session_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setDouble(2, accuracy);
            ps.setString(3, status);
            ps.setString(4, sessionId);
            ps.executeUpdate();
        }
    }

    /** Full finalize with weighted score and time taken (Phase 3). */
    public void finalizeSessionFull(String sessionId, int score, double weightedScore,
                                     double accuracy, int timeTakenSecs,
                                     String status) throws SQLException {
        final String sql =
            "UPDATE quiz_sessions " +
            "SET score=?, weighted_score=?, accuracy=?, time_taken_secs=?, " +
            "    status=?, completed_at=NOW() " +
            "WHERE session_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setDouble(2, weightedScore);
            ps.setDouble(3, accuracy);
            ps.setInt(4, timeTakenSecs);
            ps.setString(5, status);
            ps.setString(6, sessionId);
            ps.executeUpdate();
        }
    }

    public QuizSession getSessionById(String sessionId) throws SQLException {
        final String sql = "SELECT * FROM quiz_sessions WHERE session_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSession(rs) : null;
            }
        }
    }

    public List<QuizSession> getSessionsByUser(int userId) throws SQLException {
        final String sql =
            "SELECT * FROM quiz_sessions WHERE user_id=? ORDER BY started_at DESC";
        List<QuizSession> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        }
        return list;
    }

    public List<QuizSession> getCompletedByUser(int userId) throws SQLException {
        final String sql =
            "SELECT * FROM quiz_sessions " +
            "WHERE user_id=? AND status IN ('completed','auto_submitted') " +
            "ORDER BY started_at DESC";
        List<QuizSession> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSession(rs));
            }
        }
        return list;
    }

    public int countTotalAttempts() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM quiz_sessions");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countCompletedAttempts() throws SQLException {
        final String sql =
            "SELECT COUNT(*) FROM quiz_sessions WHERE status IN ('completed','auto_submitted')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public double getAverageScore() throws SQLException {
        final String sql =
            "SELECT COALESCE(AVG(accuracy),0) FROM quiz_sessions " +
            "WHERE status IN ('completed','auto_submitted')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    /** Top-N scores for admin overview. */
    public List<Map<String,Object>> getTopScores(int limit) throws SQLException {
        final String sql =
            "SELECT u.name, qs.score, qs.accuracy, qs.completed_at " +
            "FROM quiz_sessions qs JOIN users u ON qs.user_id=u.id " +
            "WHERE qs.status IN ('completed','auto_submitted') " +
            "ORDER BY qs.score DESC, qs.accuracy DESC LIMIT ?";
        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> row = new LinkedHashMap<>();
                    row.put("name",       rs.getString("name"));
                    row.put("score",      rs.getInt("score"));
                    row.put("accuracy",   rs.getDouble("accuracy"));
                    row.put("completedAt",rs.getTimestamp("completed_at"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    /** Score distribution bucketed into ranges 0-20,21-40,...81-100. */
    public Map<String,Integer> getScoreDistribution() throws SQLException {
        final String sql =
            "SELECT " +
            "  SUM(CASE WHEN accuracy BETWEEN 0  AND 20  THEN 1 ELSE 0 END) AS b0_20, " +
            "  SUM(CASE WHEN accuracy BETWEEN 21 AND 40  THEN 1 ELSE 0 END) AS b21_40, " +
            "  SUM(CASE WHEN accuracy BETWEEN 41 AND 60  THEN 1 ELSE 0 END) AS b41_60, " +
            "  SUM(CASE WHEN accuracy BETWEEN 61 AND 80  THEN 1 ELSE 0 END) AS b61_80, " +
            "  SUM(CASE WHEN accuracy BETWEEN 81 AND 100 THEN 1 ELSE 0 END) AS b81_100 " +
            "FROM quiz_sessions WHERE status IN ('completed','auto_submitted')";
        Map<String,Integer> m = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                m.put("0-20",   rs.getInt("b0_20"));
                m.put("21-40",  rs.getInt("b21_40"));
                m.put("41-60",  rs.getInt("b41_60"));
                m.put("61-80",  rs.getInt("b61_80"));
                m.put("81-100", rs.getInt("b81_100"));
            }
        }
        return m;
    }

    // ═══════════════════════════════════════════════════════════
    // ATTEMPTS
    // ═══════════════════════════════════════════════════════════

    public void saveAttempt(String sessionId, int userId, int questionId,
                             int selectedAnswer, boolean isCorrect) throws SQLException {
        saveAttemptFull(sessionId, userId, questionId, selectedAnswer, isCorrect, 0);
    }

    public void saveAttemptFull(String sessionId, int userId, int questionId,
                                 int selectedAnswer, boolean isCorrect,
                                 int timeSpentSecs) throws SQLException {
        final String sql =
            "INSERT INTO attempts " +
            "(user_id, session_id, question_id, selected_answer, is_correct, time_spent_secs) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, sessionId);
            ps.setInt(3, questionId);
            ps.setInt(4, selectedAnswer);
            ps.setBoolean(5, isCorrect);
            ps.setInt(6, timeSpentSecs);
            ps.executeUpdate();
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PROGRESS
    // ═══════════════════════════════════════════════════════════

    public void upsertProgress(QuizProgress p) throws SQLException {
        final String sql =
            "INSERT INTO quiz_progress " +
            "(user_id, session_id, current_question, remaining_time, " +
            " saved_answers, question_order, current_difficulty, " +
            " warning_count, quiz_status) " +
            "VALUES (?,?,?,?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE " +
            "  current_question   = VALUES(current_question), " +
            "  remaining_time     = VALUES(remaining_time), " +
            "  saved_answers      = VALUES(saved_answers), " +
            "  question_order     = VALUES(question_order), " +
            "  current_difficulty = VALUES(current_difficulty), " +
            "  warning_count      = VALUES(warning_count), " +
            "  quiz_status        = VALUES(quiz_status), " +
            "  last_saved_time    = NOW()";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1,    p.getUserId());
            ps.setString(2, p.getSessionId());
            ps.setInt(3,    p.getCurrentQuestion());
            ps.setInt(4,    p.getRemainingTime());
            ps.setString(5, p.getSavedAnswers());
            ps.setString(6, p.getQuestionOrder());
            ps.setString(7, p.getCurrentDifficulty());
            ps.setInt(8,    p.getWarningCount());
            ps.setString(9, p.getQuizStatus());
            ps.executeUpdate();
        }
    }

    public QuizProgress getInProgressByUser(int userId) throws SQLException {
        final String sql =
            "SELECT * FROM quiz_progress " +
            "WHERE user_id=? AND quiz_status='in_progress' " +
            "ORDER BY last_saved_time DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapProgress(rs) : null;
            }
        }
    }

    public QuizProgress getProgressBySession(String sessionId) throws SQLException {
        if (sessionId == null || sessionId.isBlank()) return null;
        final String sql = "SELECT * FROM quiz_progress WHERE session_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapProgress(rs) : null;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // MAPPERS
    // ═══════════════════════════════════════════════════════════

    private QuizSession mapSession(ResultSet rs) throws SQLException {
        QuizSession s = new QuizSession();
        s.setId(rs.getInt("id"));
        s.setSessionId(rs.getString("session_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setSelectedTags(rs.getString("selected_tags"));
        s.setTotalQuestions(rs.getInt("total_questions"));
        s.setScore(rs.getInt("score"));
        s.setAccuracy(rs.getDouble("accuracy"));
        s.setStatus(rs.getString("status"));
        Timestamp sa = rs.getTimestamp("started_at");
        if (sa != null) s.setStartedAt(sa.toLocalDateTime());
        Timestamp ca = rs.getTimestamp("completed_at");
        if (ca != null) s.setCompletedAt(ca.toLocalDateTime());
        // time_taken_secs may not exist in older rows — guard
        try { s.setTimeTakenSecs(rs.getInt("time_taken_secs")); } catch (SQLException ignored) {}
        return s;
    }

    private QuizProgress mapProgress(ResultSet rs) throws SQLException {
        QuizProgress p = new QuizProgress();
        p.setProgressId(rs.getInt("progress_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setSessionId(rs.getString("session_id"));
        p.setCurrentQuestion(rs.getInt("current_question"));
        p.setRemainingTime(rs.getInt("remaining_time"));
        p.setSavedAnswers(rs.getString("saved_answers"));
        p.setQuestionOrder(rs.getString("question_order"));
        p.setCurrentDifficulty(rs.getString("current_difficulty"));
        p.setWarningCount(rs.getInt("warning_count"));
        p.setQuizStatus(rs.getString("quiz_status"));
        Timestamp ls = rs.getTimestamp("last_saved_time");
        if (ls != null) p.setLastSavedTime(ls.toLocalDateTime());
        return p;
    }
}
