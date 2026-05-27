<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    com.quizportal.model.User u =
        (com.quizportal.model.User) session.getAttribute("loggedInUser");
    int myRank = request.getAttribute("myRank") != null ? (int)request.getAttribute("myRank") : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>Leaderboard – Quiz-Portal</title>
<script src="https://cdn.tailwindcss.com"></script>
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;700&display=swap" rel="stylesheet"/>
<style>
*{font-family:'Space Grotesk',sans-serif;}
.mono{font-family:'JetBrains Mono',monospace;}
.sidebar{width:240px;}
.nav-link{display:flex;align-items:center;gap:.75rem;padding:.625rem .75rem;border-radius:.5rem;font-size:.875rem;font-weight:500;color:#9ca3af;transition:all .15s;}
.nav-link:hover,.nav-link.active{background:rgba(234,179,8,.1);color:#fbbf24;}
.nav-link.active{border-left:3px solid #fbbf24;}
.rank-1{background:linear-gradient(135deg,rgba(234,179,8,.15),rgba(234,179,8,.05));border-color:rgba(234,179,8,.4);}
.rank-2{background:linear-gradient(135deg,rgba(156,163,175,.1),rgba(156,163,175,.05));border-color:rgba(156,163,175,.3);}
.rank-3{background:linear-gradient(135deg,rgba(180,83,9,.1),rgba(180,83,9,.05));border-color:rgba(180,83,9,.3);}
.my-row{background:rgba(34,211,238,.05);border-color:rgba(34,211,238,.3)!important;}
</style>
</head>
<body class="bg-gray-950 text-gray-100 flex min-h-screen">

<aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40">
  <div class="p-5 border-b border-gray-800">
    <span class="mono text-cyan-400 font-bold text-xl">&lt;QP/&gt;</span>
    <span class="font-bold ml-2">Quiz-Portal</span>
  </div>
  <nav class="flex-1 p-4 space-y-1">
    <% if (u != null && u.isAdmin()) { %>
      <a href="<%=ctx%>/admin/dashboard"  class="nav-link">📊 Dashboard</a>
      <a href="<%=ctx%>/admin/analytics"  class="nav-link">📈 Analytics</a>
    <% } else { %>
      <a href="<%=ctx%>/student/dashboard" class="nav-link">📊 Dashboard</a>
      <a href="<%=ctx%>/quiz/tags"         class="nav-link">🎯 Take a Quiz</a>
      <a href="<%=ctx%>/quiz/history"      class="nav-link">📋 My Attempts</a>
    <% } %>
    <a href="<%=ctx%>/leaderboard" class="nav-link active">🏆 Leaderboard</a>
  </nav>
  <div class="p-4 border-t border-gray-800">
    <a href="<%=ctx%>/logout" class="nav-link text-red-400">🚪 Logout</a>
  </div>
</aside>

<main class="ml-[240px] flex-1 p-8 max-w-3xl">

  <div class="flex items-center gap-3 mb-8">
    <span class="text-4xl">🏆</span>
    <div>
      <h1 class="text-2xl font-bold">Leaderboard</h1>
      <p class="text-gray-500 text-sm">Top performers across all topics</p>
    </div>
    <% if (myRank > 0) { %>
    <div class="ml-auto bg-cyan-500/10 border border-cyan-500/30 rounded-xl px-4 py-2 text-center">
      <p class="mono font-bold text-cyan-400 text-xl">#<%=myRank%></p>
      <p class="text-xs text-gray-500">Your Rank</p>
    </div>
    <% } %>
  </div>

  <!-- Podium for top 3 -->
  <c:if test="${not empty leaderboard}">
  <div class="flex items-end justify-center gap-4 mb-8">
    <!-- 2nd place -->
    <c:if test="${leaderboard.size() >= 2}">
      <c:set var="s2" value="${leaderboard[1]}"/>
      <div class="text-center flex-1 max-w-[160px]">
        <div class="w-12 h-12 rounded-full bg-gray-600 flex items-center justify-center text-lg font-bold mx-auto mb-2">
          ${s2.userName.substring(0,1).toUpperCase()}
        </div>
        <p class="font-semibold text-sm truncate">${s2.userName}</p>
        <p class="mono text-xs text-gray-500">${s2.totalScore} pts</p>
        <div class="mt-2 bg-gray-500/20 border border-gray-500/30 rounded-t-lg py-4 text-2xl">🥈</div>
      </div>
    </c:if>
    <!-- 1st place -->
    <c:if test="${leaderboard.size() >= 1}">
      <c:set var="s1" value="${leaderboard[0]}"/>
      <div class="text-center flex-1 max-w-[180px]">
        <div class="w-14 h-14 rounded-full bg-gradient-to-br from-yellow-400 to-orange-500 flex items-center justify-center text-xl font-bold text-gray-900 mx-auto mb-2">
          ${s1.userName.substring(0,1).toUpperCase()}
        </div>
        <p class="font-bold truncate">${s1.userName}</p>
        <p class="mono text-sm text-yellow-400">${s1.totalScore} pts</p>
        <div class="mt-2 bg-yellow-500/20 border border-yellow-500/30 rounded-t-xl py-6 text-3xl">🥇</div>
      </div>
    </c:if>
    <!-- 3rd place -->
    <c:if test="${leaderboard.size() >= 3}">
      <c:set var="s3" value="${leaderboard[2]}"/>
      <div class="text-center flex-1 max-w-[160px]">
        <div class="w-12 h-12 rounded-full bg-amber-700/40 flex items-center justify-center text-lg font-bold mx-auto mb-2">
          ${s3.userName.substring(0,1).toUpperCase()}
        </div>
        <p class="font-semibold text-sm truncate">${s3.userName}</p>
        <p class="mono text-xs text-gray-500">${s3.totalScore} pts</p>
        <div class="mt-2 bg-amber-700/20 border border-amber-700/30 rounded-t-lg py-3 text-2xl">🥉</div>
      </div>
    </c:if>
  </div>
  </c:if>

  <!-- Full table -->
  <div class="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
    <c:choose>
      <c:when test="${empty leaderboard}">
        <div class="p-8 text-center">
          <p class="text-4xl mb-3">🏅</p>
          <p class="text-gray-400 font-medium">No rankings yet</p>
          <p class="text-gray-600 text-sm mt-1">Complete a quiz to appear on the leaderboard!</p>
          <a href="<%=ctx%>/quiz/tags"
             class="mt-4 inline-block px-5 py-2 bg-cyan-500 text-gray-950 font-bold rounded-xl text-sm">
            Take a Quiz
          </a>
        </div>
      </c:when>
      <c:otherwise>
        <table class="w-full text-sm">
          <thead>
            <tr class="text-left text-xs text-gray-500 border-b border-gray-800 bg-gray-900/50">
              <th class="px-4 py-3">Rank</th>
              <th class="px-4 py-3">Student</th>
              <th class="px-4 py-3 text-right">Quizzes</th>
              <th class="px-4 py-3 text-right">Best Score</th>
              <th class="px-4 py-3 text-right">Avg Accuracy</th>
              <th class="px-4 py-3 text-right">Points</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-800">
            <c:forEach var="entry" items="${leaderboard}">
              <tr class="hover:bg-gray-800/40 transition
                ${entry.rank==1?'rank-1':entry.rank==2?'rank-2':entry.rank==3?'rank-3':''}
                ${entry.userId == pageContext.session.getAttribute('loggedInUser').id ? 'my-row' : ''}">
                <td class="px-4 py-3 mono font-bold
                  ${entry.rank==1?'text-yellow-400':entry.rank==2?'text-gray-300':entry.rank==3?'text-amber-600':'text-gray-500'}">
                  ${entry.rank==1?'🥇':entry.rank==2?'🥈':entry.rank==3?'🥉':'#'+=entry.rank}
                </td>
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <div class="w-7 h-7 rounded-full bg-gradient-to-br from-cyan-500 to-purple-500 flex items-center justify-center text-xs font-bold text-white shrink-0">
                      ${entry.userName.substring(0,1).toUpperCase()}
                    </div>
                    <span class="font-medium">${entry.userName}</span>
                  </div>
                </td>
                <td class="px-4 py-3 text-right mono text-gray-400">${entry.totalQuizzes}</td>
                <td class="px-4 py-3 text-right mono text-cyan-400">${entry.bestScore}</td>
                <td class="px-4 py-3 text-right mono
                  ${entry.avgAccuracy>=80?'text-green-400':entry.avgAccuracy>=60?'text-yellow-400':'text-red-400'}">
                  <fmt:formatNumber value="${entry.avgAccuracy}" maxFractionDigits="1"/>%
                </td>
                <td class="px-4 py-3 text-right mono font-bold text-white">${entry.totalScore}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </div>

</main>
</body>
</html>
