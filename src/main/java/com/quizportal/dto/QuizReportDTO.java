package com.quizportal.dto;

import com.quizportal.model.Tag;

import java.util.List;

/**
 * Complete report for one quiz session.
 * Passed from ReportService → ReportServlet → result.jsp
 */
public class QuizReportDTO {

    // ── Summary ────────────────────────────────────────────────
    private String  sessionId;
    private int     totalQuestions;
    private int     correct;
    private int     incorrect;
    private int     skipped;
    private int     score;
    private double  accuracy;
    private double  weightedScore;
    private double  weightedScorePct;
    private int     timeTakenSecs;
    private boolean passed;

    // ── Difficulty breakdown ────────────────────────────────────
    private int easyCorrect, easyTotal;
    private int medCorrect,  medTotal;
    private int hardCorrect, hardTotal;

    // ── Per-question review ────────────────────────────────────
    private List<QuestionReview> reviews;

    // ── Topic analysis ─────────────────────────────────────────
    private List<TopicResult> topicResults;

    // ── Getters/setters ────────────────────────────────────────

    public String getSessionId()                  { return sessionId; }
    public void setSessionId(String v)            { this.sessionId = v; }
    public int getTotalQuestions()                { return totalQuestions; }
    public void setTotalQuestions(int v)          { this.totalQuestions = v; }
    public int getCorrect()                       { return correct; }
    public void setCorrect(int v)                 { this.correct = v; }
    public int getIncorrect()                     { return incorrect; }
    public void setIncorrect(int v)               { this.incorrect = v; }
    public int getSkipped()                       { return skipped; }
    public void setSkipped(int v)                 { this.skipped = v; }
    public int getScore()                         { return score; }
    public void setScore(int v)                   { this.score = v; }
    public double getAccuracy()                   { return accuracy; }
    public void setAccuracy(double v)             { this.accuracy = v; }
    public double getWeightedScore()              { return weightedScore; }
    public void setWeightedScore(double v)        { this.weightedScore = v; }
    public double getWeightedScorePct()           { return weightedScorePct; }
    public void setWeightedScorePct(double v)     { this.weightedScorePct = v; }
    public int getTimeTakenSecs()                 { return timeTakenSecs; }
    public void setTimeTakenSecs(int v)           { this.timeTakenSecs = v; }
    public boolean isPassed()                     { return passed; }
    public void setPassed(boolean v)              { this.passed = v; }
    public int getEasyCorrect()                   { return easyCorrect; }
    public void setEasyCorrect(int v)             { this.easyCorrect = v; }
    public int getEasyTotal()                     { return easyTotal; }
    public void setEasyTotal(int v)               { this.easyTotal = v; }
    public int getMedCorrect()                    { return medCorrect; }
    public void setMedCorrect(int v)              { this.medCorrect = v; }
    public int getMedTotal()                      { return medTotal; }
    public void setMedTotal(int v)                { this.medTotal = v; }
    public int getHardCorrect()                   { return hardCorrect; }
    public void setHardCorrect(int v)             { this.hardCorrect = v; }
    public int getHardTotal()                     { return hardTotal; }
    public void setHardTotal(int v)               { this.hardTotal = v; }
    public List<QuestionReview> getReviews()      { return reviews; }
    public void setReviews(List<QuestionReview> v){ this.reviews = v; }
    public List<TopicResult> getTopicResults()    { return topicResults; }
    public void setTopicResults(List<TopicResult> v){ this.topicResults = v; }

    /** Formatted time e.g. "4m 32s". */
    public String getFormattedTime() {
        if (timeTakenSecs <= 0) return "—";
        int m = timeTakenSecs / 60, s = timeTakenSecs % 60;
        return m > 0 ? m + "m " + s + "s" : s + "s";
    }

    // ── Inner classes ──────────────────────────────────────────

    public static class QuestionReview {
        public int         questionId;
        public String      questionText;
        public String      option1, option2, option3, option4;
        public Integer     selectedAnswer;
        public int         correctAnswer;
        public boolean     isCorrect;
        public boolean     wasSkipped;
        public String      difficulty;
        public String      explanation;
        public List<Tag>   tags;

        // Public Getters (Required by JSP Expression Language)
        public int getQuestionId() { return questionId; }
        public String getQuestionText() { return questionText; }
        public String getOption1() { return option1; }
        public String getOption2() { return option2; }
        public String getOption3() { return option3; }
        public String getOption4() { return option4; }
        public Integer getSelectedAnswer() { return selectedAnswer; }
        public int getCorrectAnswer() { return correctAnswer; }
        public boolean isCorrect() { return isCorrect; }

        // This direct naming getter explicitly maps to the exact layout ${r.isCorrect} requires!
        public boolean getIsCorrect() { return isCorrect; }

        public boolean isWasSkipped() { return wasSkipped; }
        public String getDifficulty() { return difficulty; }
        public String getExplanation() { return explanation; }
        public List<Tag> getTags() { return tags; }
    }

    public static class TopicResult {
        public int    tagId;
        public String tagName;
        public int    correct;
        public int    total;
        public double accuracy;

        // Public Getters (Required by JSP Expression Language to prevent crash)
        public int getTagId() { return tagId; }
        public String getTagName() { return tagName; }
        public int getCorrect() { return correct; }
        public int getTotal() { return total; }
        public double getAccuracy() { return accuracy; }

        public String getMasteryBadge() {
            if (accuracy >= 90) return "master";
            if (accuracy >= 70) return "advanced";
            if (accuracy >= 50) return "intermediate";
            return "beginner";
        }
    }
}