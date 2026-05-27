<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    com.quizportal.dto.QuizReportDTO report =
        (com.quizportal.dto.QuizReportDTO) request.getAttribute("report");
    if (report == null) { response.sendRedirect(ctx + "/student/dashboard"); return; }
    int userRank = request.getAttribute("userRank") != null
                   ? (int) request.getAttribute("userRank") : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>Quiz Report – Quiz-Portal</title>
<script src="https://cdn.tailwindcss.com"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;700&display=swap" rel="stylesheet"/>
<style>
  *{font-family:'Space Grotesk',sans-serif;box-sizing:border-box;}
  .mono{font-family:'JetBrains Mono',monospace;}
  .sidebar{width:240px;}
  .nav-link{transition:all .15s;display:flex;align-items:center;gap:.75rem;padding:.625rem .75rem;border-radius:.5rem;font-size:.875rem;font-weight:500;color:#9ca3af;}
  .nav-link:hover{background:rgba(34,211,238,.1);color:#22d3ee;}
  .nav-link.active{background:rgba(34,211,238,.1);color:#22d3ee;border-left:3px solid #22d3ee;}
  .card{background:#111827;border:1px solid #1f2937;border-radius:1rem;}
  .badge-master{background:rgba(234,179,8,.15);color:#eab308;border:1px solid rgba(234,179,8,.3);}
  .badge-advanced{background:rgba(34,211,238,.15);color:#22d3ee;border:1px solid rgba(34,211,238,.3);}
  .badge-intermediate{background:rgba(99,102,241,.15);color:#818cf8;border:1px solid rgba(99,102,241,.3);}
  .badge-beginner{background:rgba(156,163,175,.1);color:#9ca3af;border:1px solid #374151;}
  @media print{.no-print{display:none!important;}.card{border:1px solid #ccc;background:#fff;color:#000;}}
</style>
</head>
<body class="bg-gray-950 text-gray-100 flex min-h-screen">

<!-- Sidebar -->
<aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40 no-print">
  <div class="p-5 border-b border-gray-800">
    <span class="mono text-cyan-400 font-bold text-xl">&lt;QP/&gt;</span>
    <span class="font-bold ml-2">Quiz-Portal</span>
  </div>
  <nav class="flex-1 p-4 space-y-1">
    <a href="<%=ctx%>/student/dashboard" class="nav-link">📊 Dashboard</a>
    <a href="<%=ctx%>/quiz/tags"         class="nav-link">🎯 Take a Quiz</a>
    <a href="<%=ctx%>/quiz/history"      class="nav-link active">📋 My Attempts</a>
    <a href="<%=ctx%>/leaderboard"       class="nav-link">🏆 Leaderboard</a>
  </nav>
  <div class="p-4 border-t border-gray-800">
    <a href="<%=ctx%>/logout" class="nav-link text-red-400 hover:text-red-300">🚪 Logout</a>
  </div>
</aside>

<main class="ml-[240px] flex-1 p-6 md:p-8 max-w-5xl">

  <!-- Header -->
  <div class="flex items-center justify-between mb-8 no-print">
    <div>
      <h1 class="text-2xl font-bold">Quiz Report</h1>
      <p class="text-gray-500 text-sm mt-1 mono">Session: <%=report.getSessionId().substring(0,8)%>...</p>
    </div>
    <div class="flex gap-3">
      <a href="<%=ctx%>/quiz/tags"
         class="px-4 py-2 border border-gray-700 hover:border-cyan-400 hover:text-cyan-400 rounded-xl text-sm transition">
        🔁 New Quiz
      </a>
      <button onclick="window.print()"
         class="px-4 py-2 bg-gray-800 hover:bg-gray-700 rounded-xl text-sm transition">
        🖨 Print / PDF
      </button>
    </div>
  </div>

  <!-- ── RESULT BANNER ─────────────────────────────────────── -->
  <div class="card p-6 mb-6 <%=report.isPassed() ? "border-green-500/30" : "border-red-500/30"%>">
    <div class="flex flex-col md:flex-row items-center gap-6">

      <!-- Ring -->
      <div class="relative w-32 h-32 shrink-0">
        <svg class="w-full h-full -rotate-90" viewBox="0 0 90 90">
          <circle cx="45" cy="45" r="38" fill="none" stroke="#1f2937" stroke-width="8"/>
          <circle id="accuracyRing" cx="45" cy="45" r="38" fill="none"
                  stroke="<%=report.isPassed() ? "#22d3ee" : "#f87171"%>"
                  stroke-width="8" stroke-linecap="round"
                  stroke-dasharray="238.76"
                  stroke-dashoffset="238.76"
                  style="transition:stroke-dashoffset 1.5s ease"/>
        </svg>
        <div class="absolute inset-0 flex flex-col items-center justify-center">
          <span class="mono font-bold text-2xl <%=report.isPassed() ? "text-cyan-400" : "text-red-400"%>">
            <fmt:formatNumber value="<%=report.getAccuracy()%>" maxFractionDigits="0"/>%
          </span>
          <span class="text-xs text-gray-500">accuracy</span>
        </div>
      </div>

      <!-- Stats grid -->
      <div class="flex-1 grid grid-cols-2 md:grid-cols-4 gap-4 w-full">
        <div class="text-center">
          <p class="mono text-2xl font-bold text-white"><%=report.getScore()%>/<%=report.getTotalQuestions()%></p>
          <p class="text-xs text-gray-500 mt-1">Score</p>
        </div>
        <div class="text-center">
          <p class="mono text-2xl font-bold text-green-400"><%=report.getCorrect()%></p>
          <p class="text-xs text-gray-500 mt-1">Correct</p>
        </div>
        <div class="text-center">
          <p class="mono text-2xl font-bold text-red-400"><%=report.getIncorrect()%></p>
          <p class="text-xs text-gray-500 mt-1">Wrong</p>
        </div>
        <div class="text-center">
          <p class="mono text-2xl font-bold text-gray-400"><%=report.getSkipped()%></p>
          <p class="text-xs text-gray-500 mt-1">Skipped</p>
        </div>
        <div class="text-center">
          <p class="mono text-lg font-bold text-purple-400">
            <fmt:formatNumber value="<%=report.getWeightedScorePct()%>" maxFractionDigits="1"/>%
          </p>
          <p class="text-xs text-gray-500 mt-1">Weighted</p>
        </div>
        <div class="text-center">
          <p class="mono text-lg font-bold text-cyan-400"><%=report.getFormattedTime()%></p>
          <p class="text-xs text-gray-500 mt-1">Time</p>
        </div>
        <div class="text-center">
          <p class="mono text-lg font-bold <%=report.isPassed() ? "text-green-400" : "text-red-400"%>">
            <%=report.isPassed() ? "PASS" : "FAIL"%>
          </p>
          <p class="text-xs text-gray-500 mt-1">Result</p>
        </div>
        <% if (userRank > 0) { %>
        <div class="text-center">
          <p class="mono text-lg font-bold text-yellow-400">#<%=userRank%></p>
          <p class="text-xs text-gray-500 mt-1">Your Rank</p>
        </div>
        <% } %>
      </div>
    </div>
  </div>

  <!-- ── CHARTS ROW ────────────────────────────────────────── -->
  <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">

    <!-- Difficulty breakdown doughnut -->
    <div class="card p-5">
      <h2 class="font-semibold mb-4 text-sm text-gray-300">Difficulty Breakdown</h2>
      <canvas id="diffChart" height="200"></canvas>
    </div>

    <!-- Topic accuracy bar -->
    <div class="card p-5">
      <h2 class="font-semibold mb-4 text-sm text-gray-300">Topic Performance</h2>
      <canvas id="topicChart" height="200"></canvas>
    </div>
  </div>

  <!-- ── TOPIC ANALYSIS ────────────────────────────────────── -->
  <c:if test="${not empty report.topicResults}">
  <div class="card p-5 mb-6">
    <h2 class="font-semibold mb-4">Topic Analysis</h2>
    <div class="space-y-3">
      <c:forEach var="t" items="${report.topicResults}">
        <div class="flex items-center gap-4">
          <div class="w-28 text-sm text-gray-300 truncate shrink-0">${t.tagName}</div>
          <div class="flex-1 bg-gray-800 rounded-full h-2 overflow-hidden">
            <div class="h-2 rounded-full transition-all duration-700"
                 style="width:${t.accuracy}%;background:${t.accuracy>=80?'#34d399':t.accuracy>=60?'#22d3ee':t.accuracy>=40?'#f59e0b':'#f87171'}">
            </div>
          </div>
          <span class="mono text-sm w-10 text-right text-gray-300">
            <fmt:formatNumber value="${t.accuracy}" maxFractionDigits="0"/>%
          </span>
          <span class="text-xs px-2 py-0.5 rounded-full badge-${t.masteryBadge}">${t.masteryBadge}</span>
        </div>
      </c:forEach>
    </div>
  </div>
  </c:if>

  <!-- ── QUESTION REVIEW ───────────────────────────────────── -->
  <div class="card p-5">
    <h2 class="font-semibold mb-4">Question Review (${report.totalQuestions} Questions)</h2>
    <div class="space-y-4">
      <c:forEach var="r" items="${report.reviews}" varStatus="st">
        <div class="border rounded-xl p-4 ${r.isCorrect ? 'border-green-500/25 bg-green-500/5' : r.wasSkipped ? 'border-gray-700 bg-gray-800/30' : 'border-red-500/25 bg-red-500/5'}">

          <!-- Question header -->
          <div class="flex items-start justify-between gap-3 mb-3">
            <div class="flex items-start gap-2">
              <span class="mono text-xs text-gray-500 shrink-0 mt-0.5">Q${st.index+1}</span>
              <p class="text-sm font-medium leading-relaxed">${r.questionText}</p>
            </div>
            <div class="flex items-center gap-2 shrink-0">
              <span class="text-xs px-2 py-0.5 rounded-full
                ${r.difficulty == 'easy' ? 'bg-green-500/15 text-green-400' :
                  r.difficulty == 'hard' ? 'bg-red-500/15 text-red-400' :
                  'bg-yellow-500/15 text-yellow-400'}">${r.difficulty}</span>
              <span class="text-base">${r.isCorrect ? '✅' : r.wasSkipped ? '⏭' : '❌'}</span>
            </div>
          </div>

          <!-- Options grid -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-2 mb-3 ml-5">
            <c:forEach begin="1" end="4" var="opt">
              <%-- Determine option text and state for each option 1-4 --%>
              <c:set var="optText" value="${opt==1?r.option1:opt==2?r.option2:opt==3?r.option3:r.option4}"/>
              <c:set var="isCorrectOpt"  value="${opt == r.correctAnswer}"/>
              <c:set var="isSelectedOpt" value="${opt == r.selectedAnswer}"/>
              <div class="text-xs px-3 py-2 rounded-lg border
                ${isCorrectOpt ? 'border-green-500/40 bg-green-500/10 text-green-300' :
                  (isSelectedOpt and not isCorrectOpt) ? 'border-red-500/40 bg-red-500/10 text-red-300' :
                  'border-gray-700 bg-gray-800/50 text-gray-400'}">
                <span class="font-bold mr-1">${opt==1?'A':opt==2?'B':opt==3?'C':'D'}.</span>${optText}
                <c:if test="${isCorrectOpt}"> ✓</c:if>
                <c:if test="${isSelectedOpt and not isCorrectOpt}"> ✗</c:if>
              </div>
            </c:forEach>
          </div>

          <!-- Explanation -->
          <c:if test="${not empty r.explanation}">
            <div class="ml-5 mt-2 text-xs text-cyan-300 bg-cyan-500/5 border border-cyan-500/20 rounded-lg px-3 py-2">
              💡 <strong>Explanation:</strong> ${r.explanation}
            </div>
          </c:if>

        </div>
      </c:forEach>
    </div>
  </div>

  <!-- Footer actions -->
  <div class="flex gap-3 mt-6 no-print">
    <a href="<%=ctx%>/quiz/tags"
       class="px-6 py-3 bg-cyan-500 hover:bg-cyan-400 text-gray-950 font-bold rounded-xl text-sm transition">
      🔁 Take Another Quiz
    </a>
    <a href="<%=ctx%>/leaderboard"
       class="px-6 py-3 border border-gray-700 hover:border-yellow-400 hover:text-yellow-400 rounded-xl text-sm transition">
      🏆 View Leaderboard
    </a>
    <a href="<%=ctx%>/student/dashboard"
       class="px-6 py-3 border border-gray-700 hover:border-gray-500 rounded-xl text-sm transition">
      ← Dashboard
    </a>
  </div>

</main>

<script>
// Accuracy ring animation
(function(){
  const circ = 2 * Math.PI * 38;
  const acc  = <%=report.getAccuracy()%>;
  setTimeout(function(){
    var el = document.getElementById('accuracyRing');
    if(el) el.style.strokeDashoffset = circ * (1 - acc/100);
  }, 300);
})();

// Difficulty doughnut
(function(){
  const ctx = document.getElementById('diffChart');
  if(!ctx) return;
  new Chart(ctx, {
    type:'doughnut',
    data:{
      labels:['Easy','Medium','Hard'],
      datasets:[{
        data:[<%=report.getEasyCorrect()%>, <%=report.getMedCorrect()%>, <%=report.getHardCorrect()%>],
        backgroundColor:['rgba(52,211,153,.7)','rgba(251,191,36,.7)','rgba(248,113,113,.7)'],
        borderColor:['#34d399','#fbbf24','#f87171'],
        borderWidth:1
      }]
    },
    options:{
      plugins:{legend:{labels:{color:'#9ca3af',font:{size:11}}}},
      cutout:'65%'
    }
  });
})();

// Topic bar chart
(function(){
  const ctx = document.getElementById('topicChart');
  if(!ctx) return;
  const labels = [<c:forEach var="t" items="${report.topicResults}" varStatus="st">'${t.tagName}'<c:if test="${!st.last}">,</c:if></c:forEach>];
  const data   = [<c:forEach var="t" items="${report.topicResults}" varStatus="st"><fmt:formatNumber value="${t.accuracy}" maxFractionDigits="1"/><c:if test="${!st.last}">,</c:if></c:forEach>];
  const colors = data.map(v => v>=80?'rgba(52,211,153,.7)':v>=60?'rgba(34,211,238,.7)':v>=40?'rgba(251,191,36,.7)':'rgba(248,113,113,.7)');
  new Chart(ctx, {
    type:'bar',
    data:{
      labels: labels,
      datasets:[{
        label:'Accuracy %',
        data: data,
        backgroundColor: colors,
        borderRadius: 6
      }]
    },
    options:{
      scales:{
        y:{beginAtZero:true,max:100,ticks:{color:'#6b7280'},grid:{color:'rgba(255,255,255,.05)'}},
        x:{ticks:{color:'#6b7280'},grid:{display:false}}
      },
      plugins:{legend:{display:false}}
    }
  });
})();
</script>
</body>
</html>
