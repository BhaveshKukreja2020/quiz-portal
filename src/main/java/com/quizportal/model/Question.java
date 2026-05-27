package com.quizportal.model;

import java.util.List;

/**
 * Model representing a quiz question with its options, answer, and tags.
 */
public class Question {
    private int id;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int correctAnswer;           // 1-4
    private String difficulty;           // "easy" | "medium" | "hard"
    private List<Tag> tags;

    public Question() {}

    // ---- Getters & Setters ----
    public int getId()                            { return id; }
    public void setId(int id)                     { this.id = id; }

    public String getQuestionText()               { return questionText; }
    public void setQuestionText(String t)         { this.questionText = t; }

    public String getOption1()                    { return option1; }
    public void setOption1(String o)              { this.option1 = o; }

    public String getOption2()                    { return option2; }
    public void setOption2(String o)              { this.option2 = o; }

    public String getOption3()                    { return option3; }
    public void setOption3(String o)              { this.option3 = o; }

    public String getOption4()                    { return option4; }
    public void setOption4(String o)              { this.option4 = o; }

    public int getCorrectAnswer()                 { return correctAnswer; }
    public void setCorrectAnswer(int a)           { this.correctAnswer = a; }

    public String getDifficulty()                 { return difficulty; }
    public void setDifficulty(String d)           { this.difficulty = d; }

    public List<Tag> getTags()                    { return tags; }
    public void setTags(List<Tag> tags)           { this.tags = tags; }

    // ── Phase 3 ───────────────────────────────────────────────
    private String explanation;
    public String getExplanation()               { return explanation; }
    public void setExplanation(String v)         { this.explanation = v; }

    /** Returns the next difficulty level upward (for adaptive logic). */
    public static String harder(String current) {
        return "easy".equals(current) ? "medium" : "hard";
    }

    /** Returns the next difficulty level downward (for adaptive logic). */
    public static String easier(String current) {
        return "hard".equals(current) ? "medium" : "easy";
    }
}
