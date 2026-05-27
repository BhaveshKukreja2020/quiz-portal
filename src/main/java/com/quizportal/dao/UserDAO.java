package com.quizportal.dao;

import com.quizportal.model.User;
import com.quizportal.util.DBConnection;
import com.quizportal.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data-access object for the users table.
 * All connections/statements/result-sets are closed in try-with-resources.
 */
public class UserDAO {

    /**
     * Register a new student.
     *
     * @return generated user ID, or -1 if the e-mail is already registered.
     */
    public int register(String name, String email, String plainPassword)
            throws SQLException {
        final String sql =
            "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'student')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(plainPassword));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return -1;  // duplicate e-mail
        }
    }

    /**
     * Authenticate a user by e-mail + password.
     *
     * @return populated {@link User}, or {@code null} on failure.
     */
    public User login(String email, String plainPassword) throws SQLException {
        if (email == null || plainPassword == null) return null;
        final String sql =
            "SELECT id, name, email, password, role, created_at " +
            "FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verify(plainPassword, storedHash)) {
                        return mapRow(rs);
                    }
                }
            }
        }
        return null;
    }

    /** Find user by primary key. */
    public User findById(int id) throws SQLException {
        final String sql =
            "SELECT id, name, email, password, role, created_at " +
            "FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Count registered students (non-admin users). */
    public int countStudents() throws SQLException {
        final String sql = "SELECT COUNT(*) FROM users WHERE role = 'student'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
