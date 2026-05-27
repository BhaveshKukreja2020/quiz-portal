package com.quizportal.dao;

import com.quizportal.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Audit-trail logger for admin visibility.
 */
public class ActivityLogDAO {

    public static class LogEntry {
        public int           id;
        public int           userId;
        public String        action;
        public String        entityType;
        public int           entityId;
        public String        detail;
        public String        ipAddress;
        public LocalDateTime loggedAt;
    }

    public void log(Integer userId, String action, String entityType,
                    Integer entityId, String detail, String ip) {
        // Fire-and-forget — never let logging break the main flow
        final String sql =
            "INSERT INTO activity_log (user_id, action, entity_type, entity_id, detail, ip_address) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, action);
            ps.setString(3, entityType);
            if (entityId != null) ps.setInt(4, entityId); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, detail);
            ps.setString(6, ip);
            ps.executeUpdate();
        } catch (SQLException ignored) {
            // Never propagate — logging must not break business flow
        }
    }

    public List<LogEntry> getRecent(int limit) throws SQLException {
        final String sql =
            "SELECT al.*, u.name AS user_name " +
            "FROM activity_log al " +
            "LEFT JOIN users u ON al.user_id = u.id " +
            "ORDER BY al.logged_at DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            List<LogEntry> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LogEntry e = new LogEntry();
                    e.id         = rs.getInt("id");
                    e.userId     = rs.getInt("user_id");
                    e.action     = rs.getString("action");
                    e.entityType = rs.getString("entity_type");
                    e.entityId   = rs.getInt("entity_id");
                    e.detail     = rs.getString("detail");
                    e.ipAddress  = rs.getString("ip_address");
                    Timestamp ts = rs.getTimestamp("logged_at");
                    if (ts != null) e.loggedAt = ts.toLocalDateTime();
                    list.add(e);
                }
            }
            return list;
        }
    }
}
