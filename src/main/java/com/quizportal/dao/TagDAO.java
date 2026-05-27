package com.quizportal.dao;

import com.quizportal.model.Tag;
import com.quizportal.util.DBConnection;

import java.sql.*;
import java.util.*;

/** Data-access object for the {@code tags} table. */
public class TagDAO {

    public List<Tag> getAll() throws SQLException {
        List<Tag> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, tag_name FROM tags ORDER BY tag_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Tag(rs.getInt("id"), rs.getString("tag_name")));
        }
        return list;
    }

    public Tag findById(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, tag_name FROM tags WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Tag(rs.getInt("id"), rs.getString("tag_name")) : null;
            }
        }
    }

    /** Insert a new tag. Returns generated ID, or -1 on duplicate. */
    public int insert(String tagName) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO tags (tag_name) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tagName.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return -1;  // duplicate tag_name
        }
    }

    public boolean update(int id, String newName) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE tags SET tag_name = ? WHERE id = ?")) {
            ps.setString(1, newName.trim());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM tags WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT COUNT(*) FROM tags");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
