package com.quizportal.dto;

import com.quizportal.model.Question;
import com.quizportal.model.QuizProgress;

/** Passed from QuizService.startQuiz / resumeQuiz to the servlet layer. */
public class QuizStartDTO {
    private QuizProgress progress;
    private Question     currentQuestion;
    private int          questionNumber;
    private int          totalQuestions;

    public QuizProgress getProgress()              { return progress; }
    public void setProgress(QuizProgress v)        { this.progress = v; }
    public Question getCurrentQuestion()           { return currentQuestion; }
    public void setCurrentQuestion(Question v)     { this.currentQuestion = v; }
    public int getQuestionNumber()                 { return questionNumber; }
    public void setQuestionNumber(int v)           { this.questionNumber = v; }
    public int getTotalQuestions()                 { return totalQuestions; }
    public void setTotalQuestions(int v)           { this.totalQuestions = v; }
}
