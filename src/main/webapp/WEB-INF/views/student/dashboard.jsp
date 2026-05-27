<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    com.quizportal.model.User u =
        (com.quizportal.model.User) session.getAttribute("loggedInUser");
    int rank     = request.getAttribute("leaderboardRank") != null ? (int)request.getAttribute("leaderboardRank") : 0;
    int total    = request.getAttribute("totalQuizzes")    != null ? (int)request.getAttribute("totalQuizzes")    : 0;
    double avgAcc= request.getAttribute("avgAccuracy")     != null ? (double)request.getAttribute("avgAccuracy")  : 0;
    int best     = request.getAttribute("bestScore")       != null ? (int)request.getAttribute("bestScore")       : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>Dashboard – Quiz-Portal</title>
<script src="https://cdn.tailwindcss.com"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;700&display=swap" rel="stylesheet"/>
<style>
*{font-family:'Space Grotesk',sans-serif;}
.mono{font-family:'JetBrains Mono',monospace;}
.sidebar{width:240px;}
.nav-link{display:flex;align-items:center;gap:.75rem;padding:.625rem .75rem;border-radius:.5rem;font-size:.875rem;font-weight:500;color:#9ca3af;transition:all .15s;}
.nav-link:hover,.nav-link.active{background:rgba(34,211,238,.1);color:#22d3ee;}
.nav-link.active{border-left:3px solid #22d3ee;}
.card{background:#111827;border:1px solid #1f2937;border-radius:1rem;}
</style>
</head>
<body class="bg-gray-950 text-gray-100 flex min-h-screen">

<!-- Sidebar -->
<aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40">
  <div class="p-5 border-b border-gray-800">
    <span class="mono text-cyan-400 font-bold text-xl">&lt;QP/&gt;</span>
    <span class="font-bold ml-2">Quiz-Portal</span>
  </div>
  <div class="p-4 border-b border-gray-800">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-full bg-gradient-to-br from-cyan-500 to-purple-500 flex items-center justify-center font-bold text-white text-sm">
        <%= u!=null&&u.getName()!=null&&!u.getName().isEmpty()?u.getName().substring(0,1).toUpperCase():"?" %>
      </div>
      <div>
        <p class="font-semibold text-sm"><%= u!=null?u.getName():"Student" %></p>
        <p class="text-xs text-gray-500">Student</p>
      </div>
    </div>
  </div>
  <nav class="flex-1 p-4 space-y-1">
    <a href="<%=ctx%>/student/dashboard" class="nav-link active">📊 Dashboard</a>
    <a href="<%=ctx%>/quiz/tags"         class="nav-link">🎯 Take a Quiz</a>
    <a href="<%=ctx%>/quiz/history"      class="nav-link">📋 My Attempts</a>
    <a href="<%=ctx%>/leaderboard"       class="nav-link">🏆 Leaderboard</a>
  </nav>
  <div class="p-4 border-t border-gray-800">
    <a href="<%=ctx%>/logout" class="nav-link text-red-400">🚪 Logout</a>
  </div>
</aside>

<main class="ml-[240px] flex-1 p-8">

  <!-- Header -->
  <div class="flex items-center justify-between mb-8">
    <div>
      <h1 class="text-2xl font-bold">Welcome back, <%= u!=null?u.getName():"Student" %> 👋</h1>
      <p class="text-gray-500 text-sm mt-1">Ready to test your knowledge?</p>
    </div>
    <a href="<%=ctx%>/quiz/tags"
       class="px-5 py-3 bg-cyan-500 hover:bg-cyan-400 text-gray-950 font-bold rounded-xl text-sm transition">
      🎯 Start Quiz
    </a>
  </div>

  <!-- Resume banner -->
  <c:if test="${resumable != null}">
    <div class="card p-5 mb-6 border-yellow-500/30 bg-yellow-500/5">
      <div class="flex items-center justify-between gap-4">
        <div class="flex items-center gap-3">
          <span class="text-2xl">⏸</span>
          <div>
            <p class="font-semibold text-yellow-300">Quiz in Progress</p>
            <p class="text-xs text-gray-400 mt-0.5">
              Question ${resumable.currentQuestion + 1} •
              <span class="text-yellow-400">${resumable.remainingTime}s remaining</span>
            </p>
          </div>
        </div>
        <a href="<%=ctx%>/quiz/resume"
           class="px-4 py-2 bg-yellow-500 hover:bg-yellow-400 text-gray-950 font-bold rounded-xl text-sm transition shrink-0">
          Resume →
        </a>
      </div>
    </div>
  </c:if>

  <!-- KPI row -->
  <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
    <div class="card p-5 text-center">
      <p class="mono text-3xl font-bold text-cyan-400"><%=total%></p>
      <p class="text-xs text-gray-500 mt-1">Quizzes Taken</p>
    </div>
    <div class="card p-5 text-center">
      <p class="mono text-3xl font-bold text-green-400">
        <fmt:formatNumber value="<%=avgAcc%>" maxFractionDigits="1"/>%
      </p>
      <p class="text-xs text-gray-500 mt-1">Avg Accuracy</p>
    </div>
    <div class="card p-5 text-center">
      <p class="mono text-3xl font-bold text-purple-400"><%=best%></p>
      <p class="text-xs text-gray-500 mt-1">Best Score</p>
    </div>
    <div class="card p-5 text-center">
      <p class="mono text-3xl font-bold text-yellow-400">
        <% if (rank > 0) { %>#<%=rank%><% } else { %>—<% } %>
      </p>
      <p class="text-xs text-gray-500 mt-1">Leaderboard Rank</p>
    </div>
  </div>

  <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

    <!-- Recent Attempts -->
    <div class="card p-5">
      <div class="flex items-center justify-between mb-4">
        <h2 class="font-semibold">Recent Attempts</h2>
        <a href="<%=ctx%>/quiz/history" class="text-xs text-cyan-400 hover:text-cyan-300">View all →</a>
      </div>
      <c:choose>
        <c:when test="${empty recentSessions}">
          <div class="text-center py-6">
            <p class="text-3xl mb-2">📝</p>
            <p class="text-gray-400 text-sm">No quizzes taken yet</p>
            <a href="<%=ctx%>/quiz/tags"
               class="mt-3 inline-block text-xs text-cyan-400 hover:text-cyan-300">
              Take your first quiz →
            </a>
          </div>
        </c:when>
        <c:otherwise>
          <div class="space-y-3">
            <c:forEach var="s" items="${recentSessions}">
              <div class="flex items-center justify-between p-3 rounded-lg bg-gray-800/50 hover:bg-gray-800 transition">
                <div class="flex items-center gap-3">
                  <span class="text-lg">${s.accuracy>=80?'🟢':s.accuracy>=60?'🟡':'🔴'}</span>
                  <div>
                    <p class="text-sm font-medium">${s.status=='completed'?'Completed':s.status=='auto_submitted'?'Auto-submitted':'In Progress'}</p>
                    <p class="text-xs text-gray-500">${s.score}/${s.totalQuestions} correct</p>
                  </div>
                </div>
                <div class="text-right">
                  <p class="mono text-sm font-bold ${s.accuracy>=80?'text-green-400':s.accuracy>=60?'text-yellow-400':'text-red-400'}">
                    <fmt:formatNumber value="${s.accuracy}" maxFractionDigits="1"/>%
                  </p>
                  <a href="<%=ctx%>/report?sessionId=${s.sessionId}"
                     class="text-xs text-cyan-400 hover:text-cyan-300">View report →</a>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- Topic Performance -->
    <div class="card p-5">
      <div class="flex items-center justify-between mb-4">
        <h2 class="font-semibold">Topic Performance</h2>
      </div>
      <c:choose>
        <c:when test="${empty topicStats}">
          <div class="text-center py-6">
            <p class="text-3xl mb-2">📊</p>
            <p class="text-gray-400 text-sm">Complete a quiz to see topic analytics</p>
          </div>
        </c:when>
        <c:otherwise>
          <div class="space-y-3">
            <c:forEach var="t" items="${topicStats}">
              <div>
                <div class="flex items-center justify-between mb-1">
                  <span class="text-sm text-gray-300">${t.tagName}</span>
                  <div class="flex items-center gap-2">
                    <span class="text-xs px-1.5 py-0.5 rounded-full
                      ${t.masteryLevel=='master'?'bg-yellow-500/15 text-yellow-400':
                        t.masteryLevel=='advanced'?'bg-cyan-500/15 text-cyan-400':
                        t.masteryLevel=='intermediate'?'bg-purple-500/15 text-purple-400':
                        'bg-gray-700 text-gray-400'}">${t.masteryLevel}</span>
                    <span class="mono text-xs text-gray-400">
                      <fmt:formatNumber value="${t.accuracy}" maxFractionDigits="0"/>%
                    </span>
                  </div>
                </div>
                <div class="bg-gray-800 rounded-full h-1.5">
                  <div class="h-1.5 rounded-full"
                       style="width:${t.accuracy}%;
                              background:${t.accuracy>=80?'#34d399':t.accuracy>=60?'#22d3ee':t.accuracy>=40?'#f59e0b':'#f87171'}">
                  </div>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

  </div>
</main>
</body>
</html>
