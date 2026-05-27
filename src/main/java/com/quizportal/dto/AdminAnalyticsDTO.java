package com.quizportal.dto;

import com.quizportal.dao.LeaderboardDAO;
import com.quizportal.dao.QuestionAnalyticsDAO;

import java.util.List;
import java.util.Map;

/** Aggregated analytics for the admin analytics dashboard. */
public class AdminAnalyticsDTO {

    private int    totalStudents;
    private int    totalQuestions;
    private int    totalAttempts;
    private int    completedAttempts;
    private double averageAccuracy;
    private double completionRate;

    private Map<String,Integer>                     scoreDistribution;
    private List<Map<String,Object>>                topScores;
    private List<QuestionAnalyticsDAO.QuestionStat> hardestQuestions;
    private List<QuestionAnalyticsDAO.QuestionStat> easiestQuestions;
    private List<LeaderboardDAO.LeaderboardEntry>   leaderboard;

    public int    getTotalStudents()                                      { return totalStudents; }
    public void   setTotalStudents(int v)                                 { this.totalStudents = v; }
    public int    getTotalQuestions()                                     { return totalQuestions; }
    public void   setTotalQuestions(int v)                                { this.totalQuestions = v; }
    public int    getTotalAttempts()                                      { return totalAttempts; }
    public void   setTotalAttempts(int v)                                 { this.totalAttempts = v; }
    public int    getCompletedAttempts()                                  { return completedAttempts; }
    public void   setCompletedAttempts(int v)                             { this.completedAttempts = v; }
    public double getAverageAccuracy()                                    { return averageAccuracy; }
    public void   setAverageAccuracy(double v)                            { this.averageAccuracy = v; }
    public double getCompletionRate()                                     { return completionRate; }
    public void   setCompletionRate(double v)                             { this.completionRate = v; }
    public Map<String,Integer> getScoreDistribution()                     { return scoreDistribution; }
    public void   setScoreDistribution(Map<String,Integer> v)             { this.scoreDistribution = v; }
    public List<Map<String,Object>> getTopScores()                        { return topScores; }
    public void   setTopScores(List<Map<String,Object>> v)                { this.topScores = v; }
    public List<QuestionAnalyticsDAO.QuestionStat> getHardestQuestions()  { return hardestQuestions; }
    public void   setHardestQuestions(List<QuestionAnalyticsDAO.QuestionStat> v){ this.hardestQuestions = v; }
    public List<QuestionAnalyticsDAO.QuestionStat> getEasiestQuestions()  { return easiestQuestions; }
    public void   setEasiestQuestions(List<QuestionAnalyticsDAO.QuestionStat> v){ this.easiestQuestions = v; }
    public List<LeaderboardDAO.LeaderboardEntry> getLeaderboard()         { return leaderboard; }
    public void   setLeaderboard(List<LeaderboardDAO.LeaderboardEntry> v) { this.leaderboard = v; }
}
