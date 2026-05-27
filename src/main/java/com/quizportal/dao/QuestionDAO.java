package com.quizportal.dao;

import com.quizportal.model.Question;
import com.quizportal.model.Tag;
import com.quizportal.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Data-access object for {@code questions} and their tag associations.
 * All resources are closed via try-with-resources.
 */
public class QuestionDAO {

    // ── Read operations ────────────────────────────────────────────────────

    public List<Question> getAll() throws SQLException {
        List<Question> list = new ArrayList<>();
        final String sql = "SELECT * FROM questions ORDER BY created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        // Attach tags in a separate query for each question
        for (Question q : list) q.setTags(getTagsForQuestion(q.getId()));
        return list;
    }

    public Question findById(int id) throws SQLException {
        final String sql = "SELECT * FROM questions WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {   // FIX: was leaking ResultSet
                if (rs.next()) {
                    Question q = mapRow(rs);
                    q.setTags(getTagsForQuestion(q.getId()));
                    return q;
                }
            }
        }
        return null;
    }

    /**
     * Fetch questions for an adaptive quiz:
     * filtered by tags, difficulty, and already-answered IDs.
     * Uses a parameterised IN clause — no SQL injection risk.
     */
    public List<Question> fetchByTagsAndDifficulty(
            List<Integer> tagIds,
            String difficulty,
            Set<Integer> excludeIds,
            int limit) throws SQLException {

        if (tagIds == null || tagIds.isEmpty()) return Collections.emptyList();

        String inTags = placeholders(tagIds.size());
        // If nothing to exclude, use a value that will never match a real ID
        String inExclude = excludeIds.isEmpty()
                ? "0"
                : placeholders(excludeIds.size());

        final String sql =
            "SELECT DISTINCT q.* FROM questions q " +
            "JOIN question_tags qt ON q.id = qt.question_id " +
            "WHERE qt.tag_id IN (" + inTags + ") " +
            "  AND q.difficulty = ? " +
            "  AND q.id NOT IN (" + inExclude + ") " +
            "ORDER BY RAND() LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            for (int tid : tagIds)    ps.setInt(idx++, tid);
            ps.setString(idx++, difficulty);
            for (int eid : excludeIds) ps.setInt(idx++, eid);
            ps.setInt(idx, limit);

            List<Question> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        }
    }

    public int countAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM questions");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Write operations ───────────────────────────────────────────────────

    public int insert(Question q, List<Integer> tagIds) throws SQLException {
        final String sql =
            "INSERT INTO questions " +
            "(question_text, option1, option2, option3, option4, correct_answer, difficulty, explanation) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int newId;
                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
                    setQuestionParams(ps, q);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        newId = rs.next() ? rs.getInt(1) : -1;
                    }
                }
                if (newId > 0) insertTagLinks(con, newId, tagIds);
                con.commit();
                return newId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean update(Question q, List<Integer> tagIds) throws SQLException {
        final String sql =
            "UPDATE questions SET " +
            "question_text=?, option1=?, option2=?, option3=?, option4=?, " +
            "correct_answer=?, difficulty=?, explanation=? " +
            "WHERE id=?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    setQuestionParams(ps, q);
                    ps.setInt(9, q.getId());
                    rows = ps.executeUpdate();
                }
                deleteTagLinks(con, q.getId());
                insertTagLinks(con, q.getId(), tagIds);
                con.commit();
                return rows > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM questions WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Tag associations ───────────────────────────────────────────────────

    public List<Tag> getTagsForQuestion(int questionId) throws SQLException {
        List<Tag> tags = new ArrayList<>();
        final String sql =
            "SELECT t.id, t.tag_name " +
            "FROM tags t JOIN question_tags qt ON t.id = qt.tag_id " +
            "WHERE qt.question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    tags.add(new Tag(rs.getInt("id"), rs.getString("tag_name")));
            }
        }
        return tags;
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private void setQuestionParams(PreparedStatement ps, Question q)
            throws SQLException {
        ps.setString(1, q.getQuestionText());
        ps.setString(2, q.getOption1());
        ps.setString(3, q.getOption2());
        ps.setString(4, q.getOption3());
        ps.setString(5, q.getOption4());
        ps.setInt(6, q.getCorrectAnswer());
        ps.setString(7, q.getDifficulty());
        ps.setString(8, q.getExplanation());
    }

    private void insertTagLinks(Connection con, int questionId,
                                 List<Integer> tagIds) throws SQLException {
        if (tagIds == null || tagIds.isEmpty()) return;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT IGNORE INTO question_tags (question_id, tag_id) VALUES (?, ?)")) {
            for (int tid : tagIds) {
                ps.setInt(1, questionId);
                ps.setInt(2, tid);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteTagLinks(Connection con, int questionId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM question_tags WHERE question_id = ?")) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }
    }

    /** Build a comma-separated placeholder string for n parameters. */
    private static String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(',');
            sb.append('?');
        }
        return sb.toString();
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setOption1(rs.getString("option1"));
        q.setOption2(rs.getString("option2"));
        q.setOption3(rs.getString("option3"));
        q.setOption4(rs.getString("option4"));
        q.setCorrectAnswer(rs.getInt("correct_answer"));
        q.setDifficulty(rs.getString("difficulty"));
        q.setExplanation(rs.getString("explanation"));
        return q;
    }
}
