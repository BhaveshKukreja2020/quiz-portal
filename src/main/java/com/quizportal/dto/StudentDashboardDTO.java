package com.quizportal.dto;

import com.quizportal.dao.TopicPerformanceDAO;
import com.quizportal.model.QuizProgress;
import com.quizportal.model.QuizSession;

import java.util.List;

/** Phase 3 extended student dashboard DTO. */
public class StudentDashboardDTO {
    private QuizProgress                         resumable;
    private List<QuizSession>                    recentSessions;
    private List<TopicPerformanceDAO.TopicStat>  topicStats;
    private int                                  leaderboardRank;
    private int                                  totalQuizzes;
    private double                               avgAccuracy;
    private int                                  bestScore;

    public QuizProgress getResumable()                              { return resumable; }
    public void setResumable(QuizProgress v)                        { this.resumable = v; }
    public List<QuizSession> getRecentSessions()                    { return recentSessions; }
    public void setRecentSessions(List<QuizSession> v)              { this.recentSessions = v; }
    public List<TopicPerformanceDAO.TopicStat> getTopicStats()      { return topicStats; }
    public void setTopicStats(List<TopicPerformanceDAO.TopicStat> v){ this.topicStats = v; }
    public int getLeaderboardRank()                                 { return leaderboardRank; }
    public void setLeaderboardRank(int v)                           { this.leaderboardRank = v; }
    public int getTotalQuizzes()                                    { return totalQuizzes; }
    public void setTotalQuizzes(int v)                              { this.totalQuizzes = v; }
    public double getAvgAccuracy()                                  { return avgAccuracy; }
    public void setAvgAccuracy(double v)                            { this.avgAccuracy = v; }
    public int getBestScore()                                       { return bestScore; }
    public void setBestScore(int v)                                 { this.bestScore = v; }
}
