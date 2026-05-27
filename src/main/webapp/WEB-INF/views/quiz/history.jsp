<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx=request.getContextPath(); String pageTitle="My Attempts"; %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %></head>
<body>
<div class="app-sidebar">
  <div class="brand">Quiz-Portal<span class="dot">Quiz</span></div>
  <nav style="padding:.5rem 0;flex:1">
    <a href="<%=ctx%>/student/dashboard" class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/quiz/tags"         class="nav-item"><i class="bi bi-play-circle"></i>Take a Quiz</a>
    <a href="<%=ctx%>/quiz/history"      class="nav-item active"><i class="bi bi-clock-history"></i>My Attempts</a>
    <a href="<%=ctx%>/leaderboard"       class="nav-item"><i class="bi bi-trophy"></i>Leaderboard</a>
  </nav>
  <div class="sidebar-footer"><a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a></div>
</div>
<main class="app-main">
  <div class="page-header" style="display:flex;align-items:center;justify-content:space-between;">
    <div><h1>My Attempts</h1><p>All your quiz history</p></div>
    <a href="<%=ctx%>/quiz/tags" class="btn-primary-cq" style="padding:.5rem 1.25rem"><i class="bi bi-play-fill"></i> New Quiz</a>
  </div>

  <div class="cq-card">
    <div style="overflow-x:auto;">
      <table class="cq-table">
        <thead><tr><th>#</th><th>Date</th><th>Score</th><th>Accuracy</th><th>Status</th><th style="text-align:center">Report</th></tr></thead>
        <tbody>
          <c:choose>
            <c:when test="${empty sessions}">
              <tr><td colspan="6" style="text-align:center;padding:3rem;color:var(--muted)">
                <i class="bi bi-inbox" style="font-size:2rem;display:block;margin-bottom:.5rem"></i>
                No quiz attempts yet. <a href="<%=ctx%>/quiz/tags" style="color:var(--primary)">Take your first quiz</a>
              </td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="s" items="${sessions}" varStatus="st">
              <tr>
                <td style="color:var(--muted);font-size:.75rem">${st.index+1}</td>
                <td style="font-size:.8rem;color:var(--muted)">
                  <c:if test="${s.startedAt != null}">
                      ${s.startedAt.toString().replace('T', ' ').substring(0, 16)}
                  </c:if>
                </td>
                <td><span class="mono" style="font-weight:700">${s.score}/${s.totalQuestions}</span></td>
                <td>
                  <div style="display:flex;align-items:center;gap:.5rem">
                    <div class="cq-progress" style="width:70px">
                      <div class="cq-progress-bar" style="width:${s.accuracy}%;background:${s.accuracy>=80?'var(--success)':s.accuracy>=60?'var(--warning)':'var(--danger)'}"></div>
                    </div>
                    <span class="mono" style="font-size:.78rem;color:${s.accuracy>=80?'#4ade80':s.accuracy>=60?'#fbbf24':'#f87171'}">
                      <fmt:formatNumber value="${s.accuracy}" maxFractionDigits="1"/>%
                    </span>
                  </div>
                </td>
                <td>
                  <span style="font-size:.73rem;padding:.25rem .6rem;border-radius:.375rem;
                    background:${s.status=='completed'?'rgba(34,197,94,.12)':s.status=='auto_submitted'?'rgba(245,158,11,.12)':'rgba(99,102,241,.12)'};
                    color:${s.status=='completed'?'#4ade80':s.status=='auto_submitted'?'#fbbf24':'#818cf8'}">
                    ${s.status=='completed'?'Completed':s.status=='auto_submitted'?'Auto-submitted':'In Progress'}
                  </span>
                </td>
                <td style="text-align:center">
                  <a href="<%=ctx%>/report?sessionId=${s.sessionId}" class="btn-ghost" style="padding:.3rem .75rem;font-size:.75rem"><i class="bi bi-file-text"></i> View</a>
                </td>
              </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>
