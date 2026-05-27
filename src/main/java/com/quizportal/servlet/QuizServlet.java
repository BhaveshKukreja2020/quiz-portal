package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.dto.QuizResultDTO;
import com.quizportal.dto.QuizStartDTO;
import com.quizportal.exception.QuizException;
import com.quizportal.exception.ValidationException;
import com.quizportal.model.User;
import com.quizportal.service.QuizService;
import com.quizportal.util.SessionUtil;
import com.quizportal.validator.InputValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin controller for all /quiz/* routes.
 * All quiz logic lives in QuizService.
 */
@WebServlet(urlPatterns = {"/quiz/*"})
public class QuizServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(QuizServlet.class.getName());
    private QuizService quizService;

    @Override public void init() { quizService = new QuizService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, res)) return;
        try {
            switch (sub(req)) {
                case "/tags":    showTagSelection(req, res); break;
                case "/resume":  resumeQuiz(req, res);       break;
                case "/result":  showResult(req, res);       break;
                case "/history": showHistory(req, res);      break;
                default: res.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error in QuizServlet GET", e);
            throw new ServletException("A database error occurred.", e);
        } catch (QuizException e) {
            req.setAttribute("error", e.getUserMessage());
            req.getRequestDispatcher(AppConstants.VIEW_QUIZ_TAGS).forward(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, res)) return;
        req.setCharacterEncoding("UTF-8");

        try { // <-- Added the opening try block here
            switch (sub(req)) {
                case "/start":  startQuiz(req, res);   break;
                case "/answer": submitAnswer(req, res); break;
                case "/save":   autoSave(req, res);    break;
                case "/submit": submitQuiz(req, res);  break;
                default: res.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error in QuizServlet POST", e);
            throw new ServletException("A database error occurred.", e);
        } catch (QuizException | ValidationException e) {
            req.setAttribute("error", e.getUserMessage());
            try {
                req.setAttribute("tags", quizService.getAllTags());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            req.getRequestDispatcher(AppConstants.VIEW_QUIZ_TAGS).forward(req, res);
        }
    }

    // ── GET handlers ───────────────────────────────────────────────────────

    private void showTagSelection(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        User user = SessionUtil.getUser(req);
        req.setAttribute("tags",         quizService.getAllTags());
        req.setAttribute("hasResumable", quizService.getResumableProgress(user.getId()) != null);
        forward(req, res, AppConstants.VIEW_QUIZ_TAGS);
    }

    private void resumeQuiz(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, QuizException, SQLException {
        User user = SessionUtil.getUser(req);
        QuizStartDTO dto = quizService.resumeQuiz(user.getId());
        if (dto == null) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_QUIZ_TAGS);
            return;
        }
        populateQuizRequest(req, dto);
        forward(req, res, AppConstants.VIEW_QUIZ);
    }

    private void showResult(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, QuizException, SQLException {
        String sessionId = req.getParameter("sessionId");
        if (InputValidator.isBlank(sessionId)) {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_STUDENT_DASH);
            return;
        }
        QuizResultDTO dto = quizService.getResult(sessionId);
        req.setAttribute("results",   dto.getBreakdown());
        req.setAttribute("score",     dto.getScore());
        req.setAttribute("total",     dto.getTotal());
        req.setAttribute("accuracy",  dto.getAccuracy());
        req.setAttribute("sessionId", dto.getSessionId());
        forward(req, res, AppConstants.VIEW_QUIZ_RESULT);
    }

    private void showHistory(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        User user = SessionUtil.getUser(req);
        req.setAttribute("sessions", quizService.getHistory(user.getId()));
        forward(req, res, AppConstants.VIEW_QUIZ_HISTORY);
    }

    // ── POST handlers ──────────────────────────────────────────────────────

    private void startQuiz(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, ValidationException, QuizException, SQLException {
        User user = SessionUtil.getUser(req);
        QuizStartDTO dto = quizService.startQuiz(
                user.getId(),
                req.getParameter("tags"),
                req.getParameterValues("tags"));
        populateQuizRequest(req, dto);
        forward(req, res, AppConstants.VIEW_QUIZ);
    }

    private void submitAnswer(HttpServletRequest req, HttpServletResponse res)
            throws IOException, QuizException, SQLException {
        User user = SessionUtil.getUser(req);
        String sessionId = req.getParameter("sessionId");
        int qId          = InputValidator.parseIntOrDefault(req.getParameter("questionId"), -1);
        int selected     = InputValidator.parseIntOrDefault(req.getParameter("selected"),    0);
        int remaining    = InputValidator.parseIntOrDefault(req.getParameter("remainingTime"),
                                                            AppConstants.QUIZ_DURATION_SECS);

        boolean done = quizService.submitAnswer(sessionId, user.getId(), qId, selected, remaining);

        if (done) {
            res.sendRedirect(req.getContextPath() + "/report?sessionId=" + sessionId);
        } else {
            res.sendRedirect(req.getContextPath() + AppConstants.URL_QUIZ_RESUME);
        }
    }

    private void autoSave(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = res.getWriter()) {
            try {
                quizService.autoSave(
                        req.getParameter("sessionId"),
                        InputValidator.parseIntOrDefault(req.getParameter("remainingTime"),
                                AppConstants.QUIZ_DURATION_SECS),
                        InputValidator.parseIntOrDefault(req.getParameter("warningCount"), 0),
                        req.getParameter("savedAnswers"));
                out.print("{\"saved\":true}");
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Auto-save failed", e);
                out.print("{\"saved\":false}");
            }
        }
    }

    private void submitQuiz(HttpServletRequest req, HttpServletResponse res)
            throws IOException, QuizException, SQLException {
        String sessionId = req.getParameter("sessionId");
        String status    = req.getParameter("status");
        quizService.submitQuiz(sessionId, status);
        res.sendRedirect(req.getContextPath() + "/report?sessionId=" + sessionId);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static void populateQuizRequest(HttpServletRequest req, QuizStartDTO dto) {
        req.setAttribute("progress",       dto.getProgress());
        req.setAttribute("question",       dto.getCurrentQuestion());
        req.setAttribute("questionNumber", dto.getQuestionNumber());
        req.setAttribute("totalQuestions", dto.getTotalQuestions());
    }

    private static String sub(HttpServletRequest req) {
        String info = req.getPathInfo();
        return info == null ? "/" : info;
    }

    private static void forward(HttpServletRequest req, HttpServletResponse res, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(view).forward(req, res);
    }
}
