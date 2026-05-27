<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Root welcome file: redirect logged-in users to their dashboard,
     everyone else to the login page. --%>
<%
    com.quizportal.model.User user =
        (com.quizportal.model.User) session.getAttribute("loggedInUser");
    if (user != null) {
        response.sendRedirect(request.getContextPath() +
            (user.isAdmin() ? "/admin/dashboard" : "/student/dashboard"));
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
