<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    com.quizportal.dto.AdminAnalyticsDTO a =
        (com.quizportal.dto.AdminAnalyticsDTO) request.getAttribute("analytics");
    if (a == null) { response.sendRedirect(ctx + "/admin/dashboard"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>Analytics – Admin | Quiz-Portal</title>
<script src="https://cdn.tailwindcss.com"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;700&display=swap" rel="stylesheet"/>
<style>
*{font-family:'Space Grotesk',sans-serif;}
.mono{font-family:'JetBrains Mono',monospace;}
.sidebar{width:240px;}
.nav-link{display:flex;align-items:center;gap:.75rem;padding:.625rem .75rem;border-radius:.5rem;font-size:.875rem;font-weight:500;color:#9ca3af;transition:all .15s;}
.nav-link:hover,.nav-link.active{background:rgba(168,85,247,.1);color:#c084fc;}
.nav-link.active{border-left:3px solid #c084fc;}
.stat-card{background:#111827;border:1px solid #1f2937;border-radius:1rem;padding:1.5rem;}
</style>
</head>
<body class="bg-gray-950 text-gray-100 flex min-h-screen">

<aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40">
  <div class="p-5 border-b border-gray-800">
    <span class="mono text-purple-400 font-bold text-xl">&lt;QP/&gt;</span>
    <span class="font-bold ml-2">Admin</span>
  </div>
  <nav class="flex-1 p-4 space-y-1">
    <a href="<%=ctx%>/admin/dashboard"   class="nav-link">📊 Dashboard</a>
    <a href="<%=ctx%>/admin/questions"   class="nav-link">❓ Questions</a>
    <a href="<%=ctx%>/admin/tags"        class="nav-link">🏷️ Tags</a>
    <a href="<%=ctx%>/admin/analytics"   class="nav-link active">📈 Analytics</a>
    <a href="<%=ctx%>/admin/reports"     class="nav-link">📋 Reports</a>
    <a href="<%=ctx%>/leaderboard"       class="nav-link">🏆 Leaderboard</a>
  </nav>
  <div class="p-4 border-t border-gray-800">
    <a href="<%=ctx%>/logout" class="nav-link text-red-400">🚪 Logout</a>
  </div>
</aside>

<main class="ml-[240px] flex-1 p-8">
  <h1 class="text-2xl font-bold mb-6">Platform Analytics</h1>

  <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
    <div class="stat-card">
      <p class="text-xs text-gray-500 mb-1">Total Students</p>
      <p class="mono text-3xl font-bold text-purple-400"><%=a.getTotalStudents()%></p>
    </div>
    <div class="stat-card">
      <p class="text-xs text-gray-500 mb-1">Quiz Attempts</p>
      <p class="mono text-3xl font-bold text-cyan-400"><%=a.getTotalAttempts()%></p>
    </div>
    <div class="stat-card">
      <p class="text-xs text-gray-500 mb-1">Avg Accuracy</p>
      <p class="mono text-3xl font-bold text-green-400">
        <fmt:formatNumber value="<%=a.getAverageAccuracy()%>" maxFractionDigits="1"/>%
      </p>
    </div>
    <div class="stat-card">
      <p class="text-xs text-gray-500 mb-1">Completion Rate</p>
      <p class="mono text-3xl font-bold text-yellow-400">
        <fmt:formatNumber value="<%=a.getCompletionRate()%>" maxFractionDigits="1"/>%
      </p>
    </div>
  </div>

  <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">

    <div class="stat-card">
      <h2 class="font-semibold mb-4 text-gray-300">Score Distribution</h2>
      <canvas id="scoreDistChart" height="220"></canvas>
    </div>

    <div class="stat-card">
      <h2 class="font-semibold mb-4 text-gray-300">Top Performers</h2>
      <canvas id="topPerformersChart" height="220"></canvas>
    </div>
  </div>

  <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">

    <div class="stat-card">
      <h2 class="font-semibold mb-4 text-gray-300">🔴 Hardest Questions</h2>
      <c:choose>
        <c:when test="${empty analytics.hardestQuestions}">
          <p class="text-gray-500 text-sm">No data yet — questions will appear here after students attempt the quiz.</p>
        </c:when>
        <c:otherwise>
          <div class="space-y-3">
            <c:forEach var="q" items="${analytics.hardestQuestions}">
              <div class="border border-gray-800 rounded-lg p-3">
                <p class="text-sm font-medium text-gray-200 mb-2 line-clamp-2">${q.questionText}</p>
                <div class="flex gap-4 text-xs text-gray-500">
                  <span>Shown: <span class="text-gray-300">${q.timesShown}</span></span>
                  <span>Correct: <span class="text-red-400">${q.correctRate}%</span></span>
                  <span>Avg time: <span class="text-gray-300">
                    <fmt:formatNumber value="${q.avgTimeSecs}" maxFractionDigits="0"/>s
                  </span></span>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="stat-card">
      <h2 class="font-semibold mb-4 text-gray-300">🟢 Easiest Questions</h2>
      <c:choose>
        <c:when test="${empty analytics.easiestQuestions}">
          <p class="text-gray-500 text-sm">No data yet.</p>
        </c:when>
        <c:otherwise>
          <div class="space-y-3">
            <c:forEach var="q" items="${analytics.easiestQuestions}">
              <div class="border border-gray-800 rounded-lg p-3">
                <p class="text-sm font-medium text-gray-200 mb-2 line-clamp-2">${q.questionText}</p>
                <div class="flex gap-4 text-xs text-gray-500">
                  <span>Shown: <span class="text-gray-300">${q.timesShown}</span></span>
                  <span>Correct: <span class="text-green-400">${q.correctRate}%</span></span>
                  <span>Avg time: <span class="text-gray-300">
                    <fmt:formatNumber value="${q.avgTimeSecs}" maxFractionDigits="0"/>s
                  </span></span>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="stat-card">
    <div class="flex items-center justify-between mb-4">
      <h2 class="font-semibold text-gray-300">🏆 Top Students</h2>
      <a href="<%=ctx%>/leaderboard" class="text-xs text-cyan-400 hover:text-cyan-300">View full →</a>
    </div>
    <c:choose>
      <c:when test="${empty analytics.leaderboard}">
        <p class="text-gray-500 text-sm">No quiz completions yet.</p>
      </c:when>
      <c:otherwise>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-gray-500 text-xs border-b border-gray-800">
                <th class="pb-2">#</th>
                <th class="pb-2">Student</th>
                <th class="pb-2 text-right">Quizzes</th>
                <th class="pb-2 text-right">Best</th>
                <th class="pb-2 text-right">Avg Accuracy</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-800">
              <c:forEach var="entry" items="${analytics.leaderboard}">
                <tr class="hover:bg-gray-800/30 transition">
                  <td class="py-2 mono text-gray-400">${entry.rank}</td>
                  <td class="py-2 font-medium">${entry.userName}</td>
                  <td class="py-2 text-right mono text-gray-400">${entry.totalQuizzes}</td>
                  <td class="py-2 text-right mono text-cyan-400">${entry.bestScore}</td>
                  <td class="py-2 text-right mono
                    ${entry.avgAccuracy>=80?'text-green-400':entry.avgAccuracy>=60?'text-yellow-400':'text-red-400'}">
                    <fmt:formatNumber value="${entry.avgAccuracy}" maxFractionDigits="1"/>%
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</main>

<script>
// Score Distribution Bar
(function(){
  const el = document.getElementById('scoreDistChart');
  if(!el) return;
  new Chart(el, {
    type:'bar',
    data:{
      labels:['0-20%','21-40%','41-60%','61-80%','81-100%'],
      datasets:[{
        label:'Students',
        data:[
          <%=a.getScoreDistribution().getOrDefault("0-20",0)%>,
          <%=a.getScoreDistribution().getOrDefault("21-40",0)%>,
          <%=a.getScoreDistribution().getOrDefault("41-60",0)%>,
          <%=a.getScoreDistribution().getOrDefault("61-80",0)%>,
          <%=a.getScoreDistribution().getOrDefault("81-100",0)%>
        ],
        backgroundColor:['#f87171','#fb923c','#fbbf24','#34d399','#22d3ee'],
        borderRadius:6
      }]
    },
    options:{
      scales:{
        y:{beginAtZero:true,ticks:{color:'#6b7280'},grid:{color:'rgba(255,255,255,.05)'}},
        x:{ticks:{color:'#6b7280'},grid:{display:false}}
      },
      plugins:{legend:{display:false}}
    }
  });
})();

// Top Performers
(function(){
  const el = document.getElementById('topPerformersChart');
  if(!el) return;

  // Placed the terminating semicolons outside of the square brackets safely!
  const labels = [<c:forEach var="s" items="${analytics.topScores}" varStatus="st">'${s.name}'<c:if test="${!st.last}">,</c:if></c:forEach>];
  const data   = [<c:forEach var="s" items="${analytics.topScores}" varStatus="st">${s.accuracy}<c:if test="${!st.last}">,</c:if></c:forEach>];

  new Chart(el, {
    type:'bar',
    data:{
      labels: labels.slice(0,8),
      datasets:[{
        label:'Accuracy %',
        data: data.slice(0,8),
        backgroundColor:'rgba(168,85,247,.6)',
        borderColor:'#a855f7',
        borderWidth:1,
        borderRadius:4
      }]
    },
    options:{
      indexAxis:'y',
      scales:{
        x:{beginAtZero:true,max:100,ticks:{color:'#6b7280'},grid:{color:'rgba(255,255,255,.05)'}},
        y:{ticks:{color:'#6b7280'},grid:{display:false}}
      },
      plugins:{legend:{display:false}}
    }
  });
})();
</script>
</body>
</html>