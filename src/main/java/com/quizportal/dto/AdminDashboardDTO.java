package com.quizportal.dto;

/** Stats object passed from AdminService → AdminServlet → JSP. */
public class AdminDashboardDTO {
    private int totalStudents;
    private int totalQuestions;
    private int totalAttempts;
    private int totalTags;

    public int getTotalStudents()              { return totalStudents; }
    public void setTotalStudents(int v)        { this.totalStudents = v; }
    public int getTotalQuestions()             { return totalQuestions; }
    public void setTotalQuestions(int v)       { this.totalQuestions = v; }
    public int getTotalAttempts()              { return totalAttempts; }
    public void setTotalAttempts(int v)        { this.totalAttempts = v; }
    public int getTotalTags()                  { return totalTags; }
    public void setTotalTags(int v)            { this.totalTags = v; }
}
