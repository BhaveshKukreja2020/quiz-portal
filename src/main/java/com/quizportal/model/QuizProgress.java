package com.quizportal.model;

import java.time.LocalDateTime;

/**
 * Stores auto-save state so a student can resume a quiz.
 */
public class QuizProgress {
    private int progressId;
    private int userId;
    private String sessionId;
    private int currentQuestion;        // index of next question to serve
    private int remainingTime;          // seconds
    private String savedAnswers;        // JSON: {"questionId": selectedOption, ...}
    private String questionOrder;       // JSON array of question IDs
    private String currentDifficulty;  // "easy" | "medium" | "hard"
    private int warningCount;
    private String quizStatus;          // "in_progress" | "completed" | "auto_submitted"
    private LocalDateTime lastSavedTime;

    // ---- Getters & Setters ----
    public int getProgressId()                          { return progressId; }
    public void setProgressId(int v)                    { this.progressId = v; }

    public int getUserId()                              { return userId; }
    public void setUserId(int v)                        { this.userId = v; }

    public String getSessionId()                        { return sessionId; }
    public void setSessionId(String v)                  { this.sessionId = v; }

    public int getCurrentQuestion()                     { return currentQuestion; }
    public void setCurrentQuestion(int v)               { this.currentQuestion = v; }

    public int getRemainingTime()                       { return remainingTime; }
    public void setRemainingTime(int v)                 { this.remainingTime = v; }

    public String getSavedAnswers()                     { return savedAnswers; }
    public void setSavedAnswers(String v)               { this.savedAnswers = v; }

    public String getQuestionOrder()                    { return questionOrder; }
    public void setQuestionOrder(String v)              { this.questionOrder = v; }

    public String getCurrentDifficulty()               { return currentDifficulty; }
    public void setCurrentDifficulty(String v)         { this.currentDifficulty = v; }

    public int getWarningCount()                        { return warningCount; }
    public void setWarningCount(int v)                  { this.warningCount = v; }

    public String getQuizStatus()                       { return quizStatus; }
    public void setQuizStatus(String v)                 { this.quizStatus = v; }

    public LocalDateTime getLastSavedTime()                     { return lastSavedTime; }
    public void setLastSavedTime(LocalDateTime v)               { this.lastSavedTime = v; }
}
