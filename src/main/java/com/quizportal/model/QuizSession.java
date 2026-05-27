package com.quizportal.model;

import java.time.LocalDateTime;

/**
 * Summary record for a completed (or in-progress) quiz session.
 */
public class QuizSession {
    private int id;
    private String sessionId;
    private int userId;
    private String selectedTags;       // comma-separated tag IDs
    private int totalQuestions;
    private int score;
    private double accuracy;
    private String status;             // "in_progress" | "completed" | "auto_submitted"
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // ---- Getters & Setters ----
    public int getId()                              { return id; }
    public void setId(int v)                        { this.id = v; }

    public String getSessionId()                    { return sessionId; }
    public void setSessionId(String v)              { this.sessionId = v; }

    public int getUserId()                          { return userId; }
    public void setUserId(int v)                    { this.userId = v; }

    public String getSelectedTags()                 { return selectedTags; }
    public void setSelectedTags(String v)           { this.selectedTags = v; }

    public int getTotalQuestions()                  { return totalQuestions; }
    public void setTotalQuestions(int v)            { this.totalQuestions = v; }

    public int getScore()                           { return score; }
    public void setScore(int v)                     { this.score = v; }

    public double getAccuracy()                     { return accuracy; }
    public void setAccuracy(double v)               { this.accuracy = v; }

    public String getStatus()                       { return status; }
    public void setStatus(String v)                 { this.status = v; }

    public LocalDateTime getStartedAt()                     { return startedAt; }
    public void setStartedAt(LocalDateTime v)               { this.startedAt = v; }

    public LocalDateTime getCompletedAt()                   { return completedAt; }
    public void setCompletedAt(LocalDateTime v)             { this.completedAt = v; }

    // ── Phase 3 additions ──────────────────────────────────────────────────
    private int    timeTakenSecs;
    private double weightedScore;

    public int    getTimeTakenSecs()      { return timeTakenSecs; }
    public void   setTimeTakenSecs(int v) { this.timeTakenSecs = v; }
    public double getWeightedScore()      { return weightedScore; }
    public void   setWeightedScore(double v){ this.weightedScore = v; }

    /** Formatted duration e.g. "4m 32s". */
    public String getFormattedTime() {
        if (timeTakenSecs <= 0) return "—";
        int m = timeTakenSecs / 60, s = timeTakenSecs % 60;
        return m > 0 ? m + "m " + s + "s" : s + "s";
    }

    /** Pass = accuracy >= 60%. */
    public boolean isPassed() { return accuracy >= 60.0; }
}
