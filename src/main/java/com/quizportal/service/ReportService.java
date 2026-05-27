package com.quizportal.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quizportal.config.AppConstants;
import com.quizportal.dao.*;
import com.quizportal.dto.QuizReportDTO;
import com.quizportal.exception.QuizException;
import com.quizportal.model.Question;
import com.quizportal.model.QuizProgress;
import com.quizportal.model.Tag;
import com.quizportal.validator.InputValidator;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Generates detailed post-quiz reports with:
 *   - score breakdown, accuracy, weighted score
 *   - per-question review with explanation
 *   - topic-wise analysis
 *   - strengths / weaknesses
 *   - pass/fail determination
 */
public class ReportService {

    private static final Logger LOG = Logger.getLogger(ReportService.class.getName());

    private static final double PASS_THRESHOLD = 60.0;
    // Difficulty score weights
    private static final Map<String, Double> WEIGHTS = Map.of(
        AppConstants.DIFFICULTY_EASY,   1.0,
        AppConstants.DIFFICULTY_MEDIUM, 1.5,
        AppConstants.DIFFICULTY_HARD,   2.0
    );

    private static final Type LIST_INT    = new TypeToken<List<Integer>>(){}.getType();
    private static final Type MAP_STR_INT = new TypeToken<Map<String,Integer>>(){}.getType();

    private final QuestionDAO          questionDAO;
    private final QuizDAO              quizDAO;
    private final TopicPerformanceDAO  topicDAO;
    private final QuestionAnalyticsDAO analyticsDAO;
    private final Gson                 gson;

    public ReportService() {
        this.questionDAO  = new QuestionDAO();
        this.quizDAO      = new QuizDAO();
        this.topicDAO     = new TopicPerformanceDAO();
        this.analyticsDAO = new QuestionAnalyticsDAO();
        this.gson         = new Gson();
    }

    /**
     * Build the full report for a completed quiz session.
     */
    public QuizReportDTO buildReport(String sessionId, int userId)
            throws QuizException, SQLException {

        if (InputValidator.isBlank(sessionId))
            throw new QuizException("Invalid session ID.");

        QuizProgress progress = quizDAO.getProgressBySession(sessionId);
        if (progress == null)
            throw new QuizException("Session not found.");

        List<Integer>        order  = parseList(progress.getQuestionOrder());
        Map<String,Integer>  saved  = parseAnswerMap(progress.getSavedAnswers());

        // ── Per-question breakdown ─────────────────────────────────────────
        List<QuizReportDTO.QuestionReview> reviews = new ArrayList<>();
        int correct = 0, incorrect = 0, skipped = 0;
        double weightedScore = 0, maxWeighted = 0;

        // topic tracking: tagId → [correct, total]
        Map<Integer,int[]> topicMap = new LinkedHashMap<>();

        for (int qId : order) {
            Question q = questionDAO.findById(qId);
            if (q == null) continue;

            Integer sel = saved.get(String.valueOf(qId));
            boolean answered  = sel != null;
            boolean isCorrect = answered && sel == q.getCorrectAnswer();

            double weight = WEIGHTS.getOrDefault(q.getDifficulty(), 1.0);
            maxWeighted += weight;
            if (isCorrect) { correct++; weightedScore += weight; }
            else if (!answered) skipped++;
            else incorrect++;

            // Update question analytics
            analyticsDAO.recordAnswer(qId, isCorrect, 0);

            // Accumulate topic stats
            if (q.getTags() != null) {
                for (Tag t : q.getTags()) {
                    topicMap.computeIfAbsent(t.getId(), k -> new int[]{0, 0});
                    topicMap.get(t.getId())[1]++;
                    if (isCorrect) topicMap.get(t.getId())[0]++;
                }
            }

            QuizReportDTO.QuestionReview rev = new QuizReportDTO.QuestionReview();
            rev.questionId      = qId;
            rev.questionText    = q.getQuestionText();
            rev.option1         = q.getOption1();
            rev.option2         = q.getOption2();
            rev.option3         = q.getOption3();
            rev.option4         = q.getOption4();
            rev.selectedAnswer  = sel;
            rev.correctAnswer   = q.getCorrectAnswer();
            rev.isCorrect       = isCorrect;
            rev.wasSkipped      = !answered;
            rev.difficulty      = q.getDifficulty();
            rev.explanation     = q.getExplanation();
            rev.tags            = q.getTags();
            reviews.add(rev);
        }

        int total      = order.size();
        double accuracy = total > 0 ? correct * 100.0 / total : 0;
        double wsPct    = maxWeighted > 0 ? weightedScore / maxWeighted * 100 : 0;
        boolean passed  = accuracy >= PASS_THRESHOLD;

        // ── Topic-wise analysis ────────────────────────────────────────────
        List<QuizReportDTO.TopicResult> topicResults = new ArrayList<>();
        for (Map.Entry<Integer,int[]> e : topicMap.entrySet()) {
            // Update persistent topic performance for this user
            topicDAO.upsert(userId, e.getKey(), e.getValue()[0], e.getValue()[1]);

            // Get tag name from questions
            String tagName = reviews.stream()
                .filter(r -> r.tags != null)
                .flatMap(r -> r.tags.stream())
                .filter(t -> t.getId() == e.getKey())
                .map(Tag::getTagName)
                .findFirst().orElse("Topic " + e.getKey());

            QuizReportDTO.TopicResult tr = new QuizReportDTO.TopicResult();
            tr.tagId    = e.getKey();
            tr.tagName  = tagName;
            tr.correct  = e.getValue()[0];
            tr.total    = e.getValue()[1];
            tr.accuracy = tr.total > 0 ? tr.correct * 100.0 / tr.total : 0;
            topicResults.add(tr);
        }

        // Sort: strongest first
        topicResults.sort((a, b) -> Double.compare(b.accuracy, a.accuracy));

        // ── Difficulty breakdown ───────────────────────────────────────────
        int easyC = 0, easyT = 0, medC = 0, medT = 0, hardC = 0, hardT = 0;
        for (QuizReportDTO.QuestionReview r : reviews) {
            switch (r.difficulty == null ? "medium" : r.difficulty) {
                case "easy":   easyT++; if (r.isCorrect) easyC++; break;
                case "hard":   hardT++; if (r.isCorrect) hardC++; break;
                default:       medT++;  if (r.isCorrect) medC++;
            }
        }

        // ── Finalise session in DB (weighted score + time) ─────────────────
        int timeTaken = AppConstants.QUIZ_DURATION_SECS - progress.getRemainingTime();
        quizDAO.finalizeSessionFull(sessionId, correct, weightedScore,
                accuracy, timeTaken, progress.getQuizStatus());

        // ── Build DTO ──────────────────────────────────────────────────────
        QuizReportDTO dto = new QuizReportDTO();
        dto.setSessionId(sessionId);
        dto.setTotalQuestions(total);
        dto.setCorrect(correct);
        dto.setIncorrect(incorrect);
        dto.setSkipped(skipped);
        dto.setScore(correct);
        dto.setAccuracy(accuracy);
        dto.setWeightedScore(weightedScore);
        dto.setWeightedScorePct(wsPct);
        dto.setTimeTakenSecs(timeTaken);
        dto.setPassed(passed);
        dto.setReviews(reviews);
        dto.setTopicResults(topicResults);
        dto.setEasyCorrect(easyC);  dto.setEasyTotal(easyT);
        dto.setMedCorrect(medC);    dto.setMedTotal(medT);
        dto.setHardCorrect(hardC);  dto.setHardTotal(hardT);

        LOG.info("Report built: sessionId=" + sessionId
                + " score=" + correct + "/" + total
                + " accuracy=" + String.format("%.1f", accuracy) + "%"
                + " passed=" + passed);
        return dto;
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private List<Integer> parseList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try { return gson.fromJson(json, LIST_INT); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private Map<String,Integer> parseAnswerMap(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            Map<String,Integer> m = gson.fromJson(json, MAP_STR_INT);
            return m != null ? m : new HashMap<>();
        } catch (Exception e) { return new HashMap<>(); }
    }
}
