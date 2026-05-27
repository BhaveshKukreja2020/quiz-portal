package com.quizportal.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Forces UTF-8 encoding on every request and response.
 * Must be the first filter in web.xml so downstream code
 * always reads/writes UTF-8.
 */
public class CharacterEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Also set content-type header for HTTP responses
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpRes = (HttpServletResponse) response;
            // Only set if not already set (JSPs set their own content-type)
            if (httpRes.getContentType() == null) {
                httpRes.setContentType("text/html; charset=UTF-8");
            }
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}
