package com.quizportal.service;

import com.quizportal.config.AppConstants;
import com.quizportal.dao.QuestionDAO;
import com.quizportal.dao.QuizDAO;
import com.quizportal.dao.TagDAO;
import com.quizportal.dao.UserDAO;
import com.quizportal.dto.AdminDashboardDTO;
import com.quizportal.exception.ValidationException;
import com.quizportal.model.Question;
import com.quizportal.model.Tag;
import com.quizportal.validator.InputValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business logic for admin operations:
 * dashboard stats, question CRUD, tag CRUD, reports.
 */
public class AdminService {

    private static final Logger LOG = Logger.getLogger(AdminService.class.getName());

    private final QuestionDAO questionDAO;
    private final TagDAO      tagDAO;
    private final UserDAO     userDAO;
    private final QuizDAO     quizDAO;

    public AdminService() {
        this.questionDAO = new QuestionDAO();
        this.tagDAO      = new TagDAO();
        this.userDAO     = new UserDAO();
        this.quizDAO     = new QuizDAO();
    }

    // ── Dashboard ──────────────────────────────────────────────────────────

    public AdminDashboardDTO getDashboardStats() throws SQLException {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalStudents(  userDAO.countStudents());
        dto.setTotalQuestions( questionDAO.countAll());
        dto.setTotalAttempts(  quizDAO.countTotalAttempts());
        dto.setTotalTags(      tagDAO.countAll());
        return dto;
    }

    // ── Questions ──────────────────────────────────────────────────────────

    public List<Question> getAllQuestions() throws SQLException {
        return questionDAO.getAll();
    }

    public Question getQuestionById(int id) throws SQLException {
        return questionDAO.findById(id);
    }

    /**
     * Save (insert or update) a question.
     *
     * @param q       question with fields populated
     * @param tagIds  list of associated tag IDs
     * @throws ValidationException if the question data is invalid
     */
    public void saveQuestion(Question q, List<Integer> tagIds)
            throws ValidationException, SQLException {
        InputValidator.validateQuestion(
                q.getQuestionText(),
                q.getOption1(), q.getOption2(),
                q.getOption3(), q.getOption4(),
                String.valueOf(q.getCorrectAnswer()),
                q.getDifficulty());

        if (tagIds == null) tagIds = new ArrayList<>();

        if (q.getId() > 0) {
            LOG.info("Updating question id=" + q.getId());
            questionDAO.update(q, tagIds);
        } else {
            int newId = questionDAO.insert(q, tagIds);
            LOG.info("Inserted new question id=" + newId);
        }
    }

    public void deleteQuestion(int id) throws SQLException {
        LOG.info("Deleting question id=" + id);
        questionDAO.delete(id);
    }

    // ── Tags ───────────────────────────────────────────────────────────────

    public List<Tag> getAllTags() throws SQLException {
        return tagDAO.getAll();
    }

    public void saveTag(String idStr, String tagName)
            throws ValidationException, SQLException {
        InputValidator.validateTagName(tagName);

        if (idStr != null && !idStr.isBlank()) {
            int id = InputValidator.parseIntOrDefault(idStr, 0);
            if (id > 0) {
                LOG.info("Updating tag id=" + id);
                tagDAO.update(id, tagName.trim());
            }
        } else {
            int newId = tagDAO.insert(tagName.trim());
            LOG.info("Inserted tag id=" + newId + " name=" + tagName);
        }
    }

    public void deleteTag(int id) throws SQLException {
        LOG.info("Deleting tag id=" + id);
        tagDAO.delete(id);
    }

    // ── Reports ────────────────────────────────────────────────────────────

    public AdminDashboardDTO getReportStats() throws SQLException {
        return getDashboardStats();   // same data, different view
    }
}
