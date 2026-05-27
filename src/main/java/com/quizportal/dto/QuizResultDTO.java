package com.quizportal.dto;

import java.util.List;
import java.util.Map;

/** Passed from QuizService.getResult → servlet → result.jsp. */
public class QuizResultDTO {
    private int                        score;
    private int                        total;
    private double                     accuracy;
    private String                     sessionId;
    private List<Map<String, Object>>  breakdown;

    public int getScore()                            { return score; }
    public void setScore(int v)                      { this.score = v; }
    public int getTotal()                            { return total; }
    public void setTotal(int v)                      { this.total = v; }
    public double getAccuracy()                      { return accuracy; }
    public void setAccuracy(double v)                { this.accuracy = v; }
    public String getSessionId()                     { return sessionId; }
    public void setSessionId(String v)               { this.sessionId = v; }
    public List<Map<String, Object>> getBreakdown()  { return breakdown; }
    public void setBreakdown(List<Map<String, Object>> v) { this.breakdown = v; }
}
