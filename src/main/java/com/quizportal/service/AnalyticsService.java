package com.quizportal.service;

import com.quizportal.dao.*;
import com.quizportal.dto.AdminAnalyticsDTO;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Aggregates analytics data for the admin panel:
 * completion rates, score distributions, top performers,
 * hardest/easiest questions, topic popularity.
 */
public class AnalyticsService {

    private static final Logger LOG = Logger.getLogger(AnalyticsService.class.getName());

    private final QuizDAO              quizDAO;
    private final UserDAO              userDAO;
    private final QuestionDAO          questionDAO;
    private final TagDAO               tagDAO;
    private final QuestionAnalyticsDAO qaDAO;
    private final LeaderboardDAO       lbDAO;

    public AnalyticsService() {
        this.quizDAO      = new QuizDAO();
        this.userDAO      = new UserDAO();
        this.questionDAO  = new QuestionDAO();
        this.tagDAO       = new TagDAO();
        this.qaDAO        = new QuestionAnalyticsDAO();
        this.lbDAO        = new LeaderboardDAO();
    }

    public AdminAnalyticsDTO getAdminAnalytics() throws SQLException {
        AdminAnalyticsDTO dto = new AdminAnalyticsDTO();

        dto.setTotalStudents(    userDAO.countStudents());
        dto.setTotalQuestions(   questionDAO.countAll());
        dto.setTotalAttempts(    quizDAO.countTotalAttempts());
        dto.setCompletedAttempts(quizDAO.countCompletedAttempts());
        dto.setAverageAccuracy(  quizDAO.getAverageScore());
        dto.setScoreDistribution(quizDAO.getScoreDistribution());
        dto.setTopScores(        quizDAO.getTopScores(10));
        dto.setHardestQuestions( qaDAO.getHardest(5));
        dto.setEasiestQuestions( qaDAO.getEasiest(5));
        dto.setLeaderboard(      lbDAO.getTopAll(10));

        // Completion rate
        int total = dto.getTotalAttempts();
        dto.setCompletionRate(total > 0
                ? dto.getCompletedAttempts() * 100.0 / total : 0);

        LOG.info("Admin analytics computed: students=" + dto.getTotalStudents()
                + " attempts=" + total);
        return dto;
    }
}
