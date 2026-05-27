package com.quizportal.servlet;

import com.quizportal.config.AppConstants;
import com.quizportal.dto.AdminDashboardDTO;
import com.quizportal.exception.ValidationException;
import com.quizportal.model.Question;
import com.quizportal.service.AdminService;
import com.quizportal.util.SessionUtil;
import com.quizportal.validator.InputValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin controller for all /admin/* routes.
 * Delegates all business logic to AdminService.
 */
@WebServlet(urlPatterns = {"/admin/*"})
public class AdminServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AdminServlet.class.getName());
    private AdminService adminService;

    @Override public void init() { adminService = new AdminService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (SessionUtil.requireAdmin(req, res)) return;
        try {
            switch (sub(req)) {
                case "/dashboard":    showDashboard(req, res);         break;
                case "/questions":    listQuestions(req, res);         break;
                case "/question/add": showQuestionForm(req, res, 0);   break;
                case "/question/edit":
                    showQuestionForm(req, res,
                        InputValidator.parseIntOrDefault(req.getParameter("id"), 0)); break;
                case "/tags":         listTags(req, res);              break;
                case "/reports":      showReports(req, res);           break;
                case "/analytics":    res.sendRedirect(req.getContextPath() + "/admin/analytics"); break;
                case "/leaderboard":  res.sendRedirect(req.getContextPath() + "/leaderboard"); break;
                default: res.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error in AdminServlet GET", e);
            throw new ServletException("A database error occurred.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (SessionUtil.requireAdmin(req, res)) return;
        req.setCharacterEncoding("UTF-8");
        try {
            switch (sub(req)) {
                case "/question/save":   saveQuestion(req, res);   break;
                case "/question/delete": deleteQuestion(req, res); break;
                case "/tag/save":        saveTag(req, res);        break;
                case "/tag/delete":      deleteTag(req, res);      break;
                default: res.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "DB error in AdminServlet POST", e);
            throw new ServletException("A database error occurred.", e);
        }
    }

    // ── GET handlers ───────────────────────────────────────────────────────

    private void showDashboard(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        AdminDashboardDTO dto = adminService.getDashboardStats();
        req.setAttribute("totalUsers",     dto.getTotalStudents());
        req.setAttribute("totalQuestions", dto.getTotalQuestions());
        req.setAttribute("totalAttempts",  dto.getTotalAttempts());
        req.setAttribute("totalTags",      dto.getTotalTags());
        forward(req, res, AppConstants.VIEW_ADMIN_DASH);
    }

    private void listQuestions(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        req.setAttribute("questions", adminService.getAllQuestions());
        req.setAttribute("tags",      adminService.getAllTags());
        forward(req, res, AppConstants.VIEW_ADMIN_QUESTIONS);
    }

    private void showQuestionForm(HttpServletRequest req, HttpServletResponse res, int id)
            throws ServletException, IOException, SQLException {
        if (id > 0) {
            Question q = adminService.getQuestionById(id);
            if (q == null) { res.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("question", q);
        }
        req.setAttribute("tags", adminService.getAllTags());
        forward(req, res, AppConstants.VIEW_ADMIN_QFORM);
    }

    private void listTags(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        req.setAttribute("tags", adminService.getAllTags());
        forward(req, res, AppConstants.VIEW_ADMIN_TAGS);
    }

    private void showReports(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        AdminDashboardDTO dto = adminService.getReportStats();
        req.setAttribute("totalUsers",     dto.getTotalStudents());
        req.setAttribute("totalQuestions", dto.getTotalQuestions());
        req.setAttribute("totalAttempts",  dto.getTotalAttempts());
        forward(req, res, AppConstants.VIEW_ADMIN_REPORTS);
    }

    // ── POST handlers ──────────────────────────────────────────────────────

    private void saveQuestion(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException, SQLException {
        try {
            Question q = new Question();
            q.setId(InputValidator.parseIntOrDefault(req.getParameter("id"), 0));
            q.setQuestionText(req.getParameter("questionText"));
            q.setOption1(req.getParameter("option1"));
            q.setOption2(req.getParameter("option2"));
            q.setOption3(req.getParameter("option3"));
            q.setOption4(req.getParameter("option4"));
            q.setCorrectAnswer(InputValidator.parseIntOrDefault(req.getParameter("correctAnswer"), 1));
            q.setDifficulty(InputValidator.isBlank(req.getParameter("difficulty"))
                    ? AppConstants.DIFFICULTY_MEDIUM : req.getParameter("difficulty"));
            q.setExplanation(req.getParameter("explanation"));

            List<Integer> tagIds = new ArrayList<>();
            String[] tp = req.getParameterValues("tagIds");
            if (tp != null) for (String s : tp) tagIds.add(Integer.parseInt(s.trim()));

            adminService.saveQuestion(q, tagIds);
            res.sendRedirect(req.getContextPath() + "/admin/questions");

        } catch (ValidationException e) {
            req.setAttribute("error", e.getUserMessage());
            int id = InputValidator.parseIntOrDefault(req.getParameter("id"), 0);
            showQuestionForm(req, res, id);
        }
    }

    private void deleteQuestion(HttpServletRequest req, HttpServletResponse res)
            throws IOException, SQLException {
        adminService.deleteQuestion(InputValidator.parseIntOrDefault(req.getParameter("id"), 0));
        res.sendRedirect(req.getContextPath() + "/admin/questions");
    }

    private void saveTag(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException, SQLException {
        try {
            adminService.saveTag(req.getParameter("id"), req.getParameter("tagName"));
            res.sendRedirect(req.getContextPath() + "/admin/tags");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getUserMessage());
            listTags(req, res);
        }
    }

    private void deleteTag(HttpServletRequest req, HttpServletResponse res)
            throws IOException, SQLException {
        adminService.deleteTag(InputValidator.parseIntOrDefault(req.getParameter("id"), 0));
        res.sendRedirect(req.getContextPath() + "/admin/tags");
    }

    // ── Utility ────────────────────────────────────────────────────────────

    private static String sub(HttpServletRequest req) {
        String info = req.getPathInfo();
        return info == null ? "/" : info;
    }

    private static void forward(HttpServletRequest req, HttpServletResponse res, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(view).forward(req, res);
    }
}
