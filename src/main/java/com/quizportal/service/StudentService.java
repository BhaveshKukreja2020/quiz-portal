package com.quizportal.service;

import com.quizportal.dao.LeaderboardDAO;
import com.quizportal.dao.QuizDAO;
import com.quizportal.dao.TopicPerformanceDAO;
import com.quizportal.dto.StudentDashboardDTO;
import com.quizportal.model.QuizProgress;
import com.quizportal.model.QuizSession;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for the student dashboard — Phase 3 extended.
 */
public class StudentService {

    private static final int RECENT_SESSIONS_LIMIT = 5;

    private final QuizDAO            quizDAO;
    private final TopicPerformanceDAO topicDAO;
    private final LeaderboardDAO     lbDAO;

    public StudentService() {
        this.quizDAO   = new QuizDAO();
        this.topicDAO  = new TopicPerformanceDAO();
        this.lbDAO     = new LeaderboardDAO();
    }

    public StudentDashboardDTO getDashboard(int userId) throws SQLException {
        QuizProgress      resumable    = quizDAO.getInProgressByUser(userId);
        List<QuizSession> all          = quizDAO.getSessionsByUser(userId);
        List<QuizSession> recent       = all.stream()
                .limit(RECENT_SESSIONS_LIMIT).collect(Collectors.toList());

        // Phase 3: topic stats + rank
        List<TopicPerformanceDAO.TopicStat> topicStats = topicDAO.getByUser(userId);
        int rank = lbDAO.getUserRank(userId);

        // Summary stats
        int    totalQuizzes  = all.size();
        double avgAccuracy   = all.stream().mapToDouble(QuizSession::getAccuracy).average().orElse(0);
        int    bestScore     = all.stream().mapToInt(QuizSession::getScore).max().orElse(0);

        StudentDashboardDTO dto = new StudentDashboardDTO();
        dto.setResumable(resumable);
        dto.setRecentSessions(recent);
        dto.setTopicStats(topicStats);
        dto.setLeaderboardRank(rank);
        dto.setTotalQuizzes(totalQuizzes);
        dto.setAvgAccuracy(avgAccuracy);
        dto.setBestScore(bestScore);
        return dto;
    }
}
