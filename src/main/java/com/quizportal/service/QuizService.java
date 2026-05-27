package com.quizportal.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quizportal.config.AppConstants;
import com.quizportal.dao.QuestionDAO;
import com.quizportal.dao.QuizDAO;
import com.quizportal.dao.TagDAO;
import com.quizportal.dto.QuizResultDTO;
import com.quizportal.dto.QuizStartDTO;
import com.quizportal.exception.QuizException;
import com.quizportal.exception.ValidationException;
import com.quizportal.model.*;
import com.quizportal.validator.InputValidator;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * All quiz-flow business logic.
 *
 * Handles: starting, answering, auto-saving, submitting, resuming,
 * and result computation.  No HTTP objects — clean and testable.
 */
public class QuizService {

    private static final Logger LOG = Logger.getLogger(QuizService.class.getName());

    private static final Type LIST_INT  = new TypeToken<List<Integer>>(){}.getType();
    private static final Type MAP_STR_INT = new TypeToken<Map<String, Integer>>(){}.getType();

    private final QuestionDAO questionDAO;
    private final QuizDAO     quizDAO;
    private final TagDAO      tagDAO;
    private final Gson        gson;

    public QuizService() {
        this.questionDAO = new QuestionDAO();
        this.quizDAO     = new QuizDAO();
        this.tagDAO      = new TagDAO();
        this.gson        = new Gson();
    }

    // ── Tag selection page ─────────────────────────────────────────────────

    public List<Tag> getAllTags() throws SQLException {
        return tagDAO.getAll();
    }

    public QuizProgress getResumableProgress(int userId) throws SQLException {
        return quizDAO.getInProgressByUser(userId);
    }

    // ── Start quiz ─────────────────────────────────────────────────────────

    /**
     * Validate tag selection, build question pool, create session + progress.
     *
     * @return DTO with progress + first question
     */
    public QuizStartDTO startQuiz(int userId, String tagsParam, String[] tagValues)
            throws ValidationException, QuizException, SQLException {

        List<Integer> tagIds = parseTagIds(tagsParam, tagValues);
        if (tagIds.isEmpty()) {
            throw new ValidationException(AppConstants.MSG_NO_TAGS_SELECTED);
        }

        List<Question> pool = questionDAO.fetchByTagsAndDifficulty(
                tagIds, AppConstants.DIFFICULTY_MEDIUM,
                Collections.emptySet(),
                AppConstants.QUESTIONS_PER_QUIZ);

        if (pool.isEmpty()) {
            throw new QuizException(AppConstants.MSG_NO_QUESTIONS);
        }

        String sessionId = UUID.randomUUID().toString();
        List<Integer> questionOrder = new ArrayList<>();
        for (Question q : pool) questionOrder.add(q.getId());

        // Persist session
        QuizSession session = new QuizSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setSelectedTags(String.join(",", tagIds.stream()
                .map(String::valueOf).toArray(String[]::new)));
        session.setTotalQuestions(pool.size());
        session.setStatus(AppConstants.STATUS_IN_PROGRESS);
        quizDAO.createSession(session);

        // Persist initial progress
        QuizProgress progress = new QuizProgress();
        progress.setUserId(userId);
        progress.setSessionId(sessionId);
        progress.setCurrentQuestion(0);
        progress.setRemainingTime(AppConstants.QUIZ_DURATION_SECS);
        progress.setSavedAnswers("{}");
        progress.setQuestionOrder(gson.toJson(questionOrder));
        progress.setCurrentDifficulty(AppConstants.DIFFICULTY_MEDIUM);
        progress.setWarningCount(0);
        progress.setQuizStatus(AppConstants.STATUS_IN_PROGRESS);
        quizDAO.upsertProgress(progress);

        LOG.info("Quiz started: sessionId=" + sessionId + " userId=" + userId
                + " questions=" + pool.size());

        QuizStartDTO dto = new QuizStartDTO();
        dto.setProgress(progress);
        dto.setCurrentQuestion(pool.get(0));
        dto.setQuestionNumber(1);
        dto.setTotalQuestions(pool.size());
        return dto;
    }

    // ── Submit single answer ───────────────────────────────────────────────

    /**
     * Record the answer, apply adaptive difficulty, advance progress.
     *
     * @return true if the quiz is now complete (no more questions)
     */
    public boolean submitAnswer(String sessionId, int userId,
                                int questionId, int selected, int remainingTime)
            throws QuizException, SQLException {

        if (InputValidator.isBlank(sessionId)) throw new QuizException("Invalid session.");
        if (questionId < 1)                    throw new QuizException("Invalid question ID.");

        Question q = questionDAO.findById(questionId);
        if (q == null) throw new QuizException("Question not found.");

        boolean correct = (q.getCorrectAnswer() == selected);
        quizDAO.saveAttempt(sessionId, userId, questionId, selected, correct);

        QuizProgress progress = getProgressOrThrow(sessionId);

        // Adaptive difficulty
        String nextDiff = correct
                ? Question.harder(progress.getCurrentDifficulty())
                : Question.easier(progress.getCurrentDifficulty());

        List<Integer> order = parseList(progress.getQuestionOrder());
        Map<String, Integer> saved = parseAnswerMap(progress.getSavedAnswers());
        saved.put(String.valueOf(questionId), selected);

        int nextIdx = progress.getCurrentQuestion() + 1;
        boolean done = (nextIdx >= order.size());

        progress.setCurrentQuestion(nextIdx);
        progress.setRemainingTime(remainingTime);
        progress.setSavedAnswers(gson.toJson(saved));
        progress.setCurrentDifficulty(nextDiff);
        progress.setQuizStatus(done ? AppConstants.STATUS_COMPLETED : AppConstants.STATUS_IN_PROGRESS);
        quizDAO.upsertProgress(progress);

        if (done) finalizeSession(sessionId, order, saved, AppConstants.STATUS_COMPLETED);

        return done;
    }

    // ── Auto-save ──────────────────────────────────────────────────────────

    public void autoSave(String sessionId, int remaining, int warnings, String answersJson)
            throws SQLException {
        if (InputValidator.isBlank(sessionId)) return;
        QuizProgress p = quizDAO.getProgressBySession(sessionId);
        if (p != null) {
            p.setRemainingTime(remaining);
            p.setWarningCount(warnings);
            if (!InputValidator.isBlank(answersJson)) p.setSavedAnswers(answersJson);
            quizDAO.upsertProgress(p);
        }
    }

    // ── Manual submit / auto-submit ────────────────────────────────────────

    public void submitQuiz(String sessionId, String status)
            throws QuizException, SQLException {
        if (InputValidator.isBlank(sessionId)) throw new QuizException("Invalid session.");
        QuizProgress progress = getProgressOrThrow(sessionId);

        List<Integer> order = parseList(progress.getQuestionOrder());
        Map<String, Integer> saved = parseAnswerMap(progress.getSavedAnswers());

        String resolvedStatus = InputValidator.isBlank(status)
                ? AppConstants.STATUS_COMPLETED : status;

        finalizeSession(sessionId, order, saved, resolvedStatus);
        progress.setQuizStatus(resolvedStatus);
        quizDAO.upsertProgress(progress);

        LOG.info("Quiz submitted: sessionId=" + sessionId + " status=" + resolvedStatus);
    }

    // ── Resume ─────────────────────────────────────────────────────────────

    /**
     * Load the current question for an in-progress quiz.
     *
     * @return DTO with progress + current question, or null if no active quiz
     */
    public QuizStartDTO resumeQuiz(int userId) throws QuizException, SQLException {
        QuizProgress progress = quizDAO.getInProgressByUser(userId);
        if (progress == null) return null;

        List<Integer> order = parseList(progress.getQuestionOrder());
        int idx = progress.getCurrentQuestion();

        if (idx >= order.size()) {
            // Edge case: progress says in_progress but all answered
            LOG.warning("Resume called but all questions answered. sessionId=" + progress.getSessionId());
            return null;
        }

        Question q = questionDAO.findById(order.get(idx));
        if (q == null) throw new QuizException("Question data missing. Please start a new quiz.");

        QuizStartDTO dto = new QuizStartDTO();
        dto.setProgress(progress);
        dto.setCurrentQuestion(q);
        dto.setQuestionNumber(idx + 1);
        dto.setTotalQuestions(order.size());
        return dto;
    }

    // ── Result ─────────────────────────────────────────────────────────────

    public QuizResultDTO getResult(String sessionId) throws QuizException, SQLException {
        if (InputValidator.isBlank(sessionId)) throw new QuizException("Invalid session ID.");

        QuizProgress progress = quizDAO.getProgressBySession(sessionId);
        if (progress == null) throw new QuizException("Session not found.");

        List<Integer> order = parseList(progress.getQuestionOrder());
        Map<String, Integer> saved = parseAnswerMap(progress.getSavedAnswers());

        List<Map<String, Object>> breakdown = new ArrayList<>();
        int score = 0;

        for (int qId : order) {
            Question q = questionDAO.findById(qId);
            if (q == null) continue;
            Integer sel = saved.get(String.valueOf(qId));
            boolean correct = sel != null && sel == q.getCorrectAnswer();
            if (correct) score++;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("question",  q.getQuestionText());
            row.put("option1",   q.getOption1());
            row.put("option2",   q.getOption2());
            row.put("option3",   q.getOption3());
            row.put("option4",   q.getOption4());
            row.put("selected",  sel);
            row.put("correct",   q.getCorrectAnswer());
            row.put("isCorrect", correct);
            breakdown.add(row);
        }

        int total = order.size();
        double accuracy = total > 0 ? score * 100.0 / total : 0;

        QuizResultDTO dto = new QuizResultDTO();
        dto.setScore(score);
        dto.setTotal(total);
        dto.setAccuracy(accuracy);
        dto.setSessionId(sessionId);
        dto.setBreakdown(breakdown);
        return dto;
    }

    // ── History ────────────────────────────────────────────────────────────

    public List<QuizSession> getHistory(int userId) throws SQLException {
        return quizDAO.getSessionsByUser(userId);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private QuizProgress getProgressOrThrow(String sessionId)
            throws QuizException, SQLException {
        QuizProgress p = quizDAO.getProgressBySession(sessionId);
        if (p == null) throw new QuizException("Quiz session not found or expired.");
        return p;
    }

    private void finalizeSession(String sessionId, List<Integer> order,
                                  Map<String, Integer> saved, String status)
            throws SQLException {
        int score = 0;
        for (Map.Entry<String, Integer> e : saved.entrySet()) {
            try {
                Question q = questionDAO.findById(Integer.parseInt(e.getKey()));
                if (q != null && q.getCorrectAnswer() == e.getValue()) score++;
            } catch (NumberFormatException ignored) {}
        }
        int total = order.size();
        double accuracy = total > 0 ? score * 100.0 / total : 0;
        quizDAO.finalizeSession(sessionId, score, accuracy, status);
    }

    private List<Integer> parseTagIds(String tagsParam, String[] tagValues) {
        List<Integer> ids = new ArrayList<>();
        String[] raw;
        if (!InputValidator.isBlank(tagsParam)) {
            raw = tagsParam.split(",");
        } else if (tagValues != null) {
            raw = tagValues;
        } else {
            return ids;
        }
        for (String s : raw) {
            try {
                int id = Integer.parseInt(s.trim());
                if (id > 0) ids.add(id);
            } catch (NumberFormatException ignored) {}
        }
        return ids;
    }

    private List<Integer> parseList(String json) {
        if (InputValidator.isBlank(json)) return new ArrayList<>();
        try { return gson.fromJson(json, LIST_INT); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private Map<String, Integer> parseAnswerMap(String json) {
        if (InputValidator.isBlank(json)) return new HashMap<>();
        try {
            Map<String, Integer> m = gson.fromJson(json, MAP_STR_INT);
            return m != null ? m : new HashMap<>();
        } catch (Exception e) { return new HashMap<>(); }
    }
}
