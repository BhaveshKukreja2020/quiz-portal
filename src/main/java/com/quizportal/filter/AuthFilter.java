package com.quizportal.filter;

import com.quizportal.config.AppConstants;
import com.quizportal.model.User;
import com.quizportal.util.SessionUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Authentication + authorisation guard filter.
 *
 * Enforces:
 *  - Unauthenticated users → /login
 *  - Students accessing /admin/* → /student/dashboard
 *  - Admins accessing /student/* → /admin/dashboard
 *
 * Registered in web.xml only (no @WebFilter to avoid double-registration).
 */
public class AuthFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest)  request;
        HttpServletResponse res = (HttpServletResponse) response;

        User user = SessionUtil.getUser(req);

        if (user == null) {
            LOG.fine("Unauthenticated request blocked: " + req.getRequestURI());
            res.sendRedirect(req.getContextPath() + AppConstants.URL_LOGIN);
            return;
        }

        String uri  = req.getRequestURI();
        String base = req.getContextPath();

        if (uri.startsWith(base + "/admin/") && !user.isAdmin()) {
            LOG.warning("Student attempted admin access: userId=" + user.getId());
            res.sendRedirect(base + AppConstants.URL_STUDENT_DASH);
            return;
        }

        if (uri.startsWith(base + "/student/") && user.isAdmin()) {
            res.sendRedirect(base + AppConstants.URL_ADMIN_DASH);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
