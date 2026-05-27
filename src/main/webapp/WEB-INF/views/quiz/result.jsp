<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    // Safely extract from our encapsulated QuizReportDTO object wrapper
    com.quizportal.dto.QuizReportDTO rDTO = (com.quizportal.dto.QuizReportDTO) request.getAttribute("report");
    int    score = (rDTO != null) ? rDTO.getScore() : 0;
    int    total = (rDTO != null) ? rDTO.getTotalQuestions() : 0;
    double acc   = (rDTO != null) ? rDTO.getAccuracy() : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/><meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Result – Quiz-Portal</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;700&display=swap" rel="stylesheet"/>
  <style>
    *{font-family:'Space Grotesk',sans-serif;} .mono{font-family:'JetBrains Mono',monospace;}
    .sidebar{width:240px;} .nav-link{transition:all .15s;}
    .nav-link:hover,.nav-link.active{background:rgba(34,211,238,0.1);color:#22d3ee;}
    .nav-link.active{border-left:3px solid #22d3ee;}
    .correct-row{border-left:3px solid #34d399;} .wrong-row{border-left:3px solid #f87171;}
    .ring-base{stroke-dasharray:251.2;stroke-dashoffset:251.2;transition:stroke-dashoffset 1.5s ease;}
  </style>
</head>
<body class="bg-gray-950 text-gray-100 flex min-h-screen">
  <aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40">
    <div class="p-5 border-b border-gray-800"><span class="mono text-cyan-400 font-bold text-xl">&lt;QP/&gt;</span><span class="font-bold ml-2">Quiz-Portal</span></div>
    <nav class="flex-1 p-4 space-y-1">
      <a href="<%=ctx%>/student/dashboard" class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">📊 Dashboard</a>
      <a href="<%=ctx%>/quiz/tags"         class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">🎯 Take a Quiz</a>
      <a href="<%=ctx%>/quiz/history"      class="nav-link active flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium">📋 My Attempts</a>
    </nav>
    <div class="p-4 border-t border-gray-800"><a href="<%=ctx%>/logout" class="text-sm text-gray-400 hover:text-red-400 px-3 py-2 rounded-lg transition block">🚪 Logout</a></div>
  </aside>

  <main class="ml-[240px] flex-1 p-8">
    <div class="max-w-3xl mx-auto">

      <div class="text-center mb-10">
        <div class="relative w-36 h-36 mx-auto mb-6">
          <svg class="w-full h-full -rotate-90" viewBox="0 0 90 90">
            <circle cx="45" cy="45" r="40" fill="none" stroke="#1f2937" stroke-width="8"/>
            <circle id="scoreRing" cx="45" cy="45" r="40" fill="none" stroke="#22d3ee"
                    stroke-width="8" stroke-linecap="round" class="ring-base"/>
          </svg>
          <div class="absolute inset-0 flex flex-col items-center justify-center">
            <span class="mono font-bold text-3xl text-cyan-400"><%=score%>/<%=total%></span>
            <span class="text-xs text-gray-400">Score</span>
          </div>
        </div>

        <h1 class="text-3xl font-bold mb-2">
          <%=acc >= 80 ? "Excellent! 🎉" : acc >= 60 ? "Good Job! 👍" : "Keep Practicing! 💪"%>
        </h1>
        <p class="text-gray-400">
          Accuracy: <fmt:formatNumber value="<%=acc%>" maxFractionDigits="1"/>%
        </p>

        <div class="flex flex-wrap justify-center gap-6 mt-6">
          <div class="bg-gray-900 border border-gray-800 rounded-xl px-6 py-4 text-center">
            <p class="mono text-2xl font-bold text-green-400"><%=score%></p>
            <p class="text-xs text-gray-500 mt-1">Correct</p>
          </div>
          <div class="bg-gray-900 border border-gray-800 rounded-xl px-6 py-4 text-center">
            <p class="mono text-2xl font-bold text-red-400"><%=total - score%></p>
            <p class="text-xs text-gray-500 mt-1">Incorrect</p>
          </div>
          <div class="bg-gray-900 border border-gray-800 rounded-xl px-6 py-4 text-center">
            <p class="mono text-2xl font-bold text-cyan-400">
              <fmt:formatNumber value="<%=acc%>" maxFractionDigits="1"/>%
            </p>
            <p class="text-xs text-gray-500 mt-1">Accuracy</p>
          </div>
        </div>

        <div class="flex gap-4 justify-center mt-8">
          <a href="<%=ctx%>/quiz/tags" class="px-6 py-3 bg-cyan-500 hover:bg-cyan-400 text-gray-950 font-bold rounded-xl text-sm transition">🔁 Try Again</a>
          <a href="<%=ctx%>/student/dashboard" class="px-6 py-3 border border-gray-700 hover:border-cyan-400 hover:text-cyan-400 rounded-xl text-sm transition">← Dashboard</a>
        </div>
      </div>

      <h2 class="font-semibold text-lg mb-4">Question Breakdown</h2>
      <div class="space-y-3">
        <c:forEach var="r" items="${report.reviews}" varStatus="st">
          <div class="${r.isCorrect ? 'correct-row' : 'wrong-row'} bg-gray-900 border border-gray-800 rounded-xl p-4">
            <div class="flex items-start justify-between gap-4">
              <div class="flex items-start gap-3">
                <span class="mono text-xs text-gray-500 mt-0.5 shrink-0">Q${st.index+1}</span>
                <p class="text-sm font-medium">${r.questionText}</p>
              </div>
              <span class="text-lg shrink-0">${r.isCorrect ? '✅' : '❌'}</span>
            </div>
            <c:if test="${!r.isCorrect}">
              <div class="mt-3 ml-8 grid grid-cols-2 gap-2 text-xs">
                <div class="bg-red-500/10 border border-red-500/20 rounded-lg p-2">
                  <p class="text-gray-500 mb-0.5">Your answer</p>
                  <c:choose>
                    <c:when test="${r.selectedAnswer == null}">
                      <p class="text-gray-500 italic">Not answered</p>
                    </c:when>
                    <c:when test="${r.selectedAnswer == 1}"><p class="text-red-400 font-medium">A: ${r.option1}</p></c:when>
                    <c:when test="${r.selectedAnswer == 2}"><p class="text-red-400 font-medium">B: ${r.option2}</p></c:when>
                    <c:when test="${r.selectedAnswer == 3}"><p class="text-red-400 font-medium">C: ${r.option3}</p></c:when>
                    <c:when test="${r.selectedAnswer == 4}"><p class="text-red-400 font-medium">D: ${r.option4}</p></c:when>
                  </c:choose>
                </div>
                <div class="bg-green-500/10 border border-green-500/20 rounded-lg p-2">
                  <p class="text-gray-500 mb-0.5">Correct answer</p>
                  <c:choose>
                    <c:when test="${r.correctAnswer == 1}"><p class="text-green-400 font-medium">A: ${r.option1}</p></c:when>
                    <c:when test="${r.correctAnswer == 2}"><p class="text-green-400 font-medium">B: ${r.option2}</p></c:when>
                    <c:when test="${r.correctAnswer == 3}"><p class="text-green-400 font-medium">C: ${r.option3}</p></c:when>
                    <c:when test="${r.correctAnswer == 4}"><p class="text-green-400 font-medium">D: ${r.option4}</p></c:when>
                  </c:choose>
                </div>
              </div>
            </c:if>
          </div>
        </c:forEach>
      </div>

    </div>
  </main>

  <script>
    const score = <%=score%>, total = <%=total%>;
    if (total > 0) {
      setTimeout(() => {
        const c = 2 * Math.PI * 40;
        document.getElementById('scoreRing').style.strokeDashoffset = c * (1 - score / total);
      }, 300);
    }
  </script>
</body>
</html>