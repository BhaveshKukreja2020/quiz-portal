package com.quizportal.dao;

import com.quizportal.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Data-access for per-student per-tag performance tracking.
 */
public class TopicPerformanceDAO {

    public static class TopicStat {
        public int    tagId;
        public String tagName;
        public int    correctCount;
        public int    totalCount;
        public double accuracy;
        public String masteryLevel;

        // ── PUBLIC GETTERS (Required by JSP Expression Language to prevent crashes) ──
        public int getTagId() { return tagId; }
        public String getTagName() { return tagName; }
        public int getCorrectCount() { return correctCount; }
        public int getTotalCount() { return totalCount; }
        public double getAccuracy() { return accuracy; }
        public String getMasteryLevel() { return masteryLevel; }
    }

    /** Upsert topic performance after a quiz. */
    public void upsert(int userId, int tagId,
                       int correctDelta, int totalDelta) throws SQLException {
        final String sql =
                "INSERT INTO topic_performance " +
                        "(user_id, tag_id, correct_count, total_count, accuracy, mastery_level) " +
                        "VALUES (?, ?, ?, ?, ?, 'beginner') " +
                        "ON DUPLICATE KEY UPDATE " +
                        "  correct_count = correct_count + VALUES(correct_count), " +
                        "  total_count   = total_count   + VALUES(total_count), " +
                        "  accuracy      = CASE WHEN (total_count + VALUES(total_count)) > 0 " +
                        "                       THEN ROUND((correct_count + VALUES(correct_count)) * 100.0 " +
                        "                                  / (total_count + VALUES(total_count)), 2) " +
                        "                       ELSE 0 END, " +
                        "  mastery_level = CASE " +
                        "    WHEN ROUND((correct_count + VALUES(correct_count)) * 100.0 " +
                        "               / GREATEST(total_count + VALUES(total_count),1), 2) >= 90 THEN 'master' " +
                        "    WHEN ROUND((correct_count + VALUES(correct_count)) * 100.0 " +
                        "               / GREATEST(total_count + VALUES(total_count),1), 2) >= 70 THEN 'advanced' " +
                        "    WHEN ROUND((correct_count + VALUES(correct_count)) * 100.0 " +
                        "               / GREATEST(total_count + VALUES(total_count),1), 2) >= 50 THEN 'intermediate' " +
                        "    ELSE 'beginner' END";
        double acc = totalDelta > 0 ? correctDelta * 100.0 / totalDelta : 0;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tagId);
            ps.setInt(3, correctDelta);
            ps.setInt(4, totalDelta);
            ps.setDouble(5, acc);
            ps.executeUpdate();
        }
    }

    /** Get all topic stats for a student, ordered by accuracy desc. */
    public List<TopicStat> getByUser(int userId) throws SQLException {
        final String sql =
                "SELECT tp.tag_id, t.tag_name, tp.correct_count, tp.total_count, " +
                        "       tp.accuracy, tp.mastery_level " +
                        "FROM topic_performance tp " +
                        "JOIN tags t ON tp.tag_id = t.id " +
                        "WHERE tp.user_id = ? " +
                        "ORDER BY tp.accuracy DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            List<TopicStat> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopicStat s = new TopicStat();
                    s.tagId        = rs.getInt("tag_id");
                    s.tagName      = rs.getString("tag_name");
                    s.correctCount = rs.getInt("correct_count");
                    s.totalCount   = rs.getInt("total_count");
                    s.accuracy     = rs.getDouble("accuracy");
                    s.masteryLevel = rs.getString("mastery_level");
                    list.add(s);
                }
            }
            return list;
        }
    }
}