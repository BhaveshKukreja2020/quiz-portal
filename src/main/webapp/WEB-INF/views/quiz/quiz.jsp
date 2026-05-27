<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctx = request.getContextPath();
    com.quizportal.model.QuizProgress progress =
        (com.quizportal.model.QuizProgress) request.getAttribute("progress");
    com.quizportal.model.Question question =
        (com.quizportal.model.Question) request.getAttribute("question");
    int qNum   = request.getAttribute("questionNumber") != null
                 ? (int) request.getAttribute("questionNumber") : 1;
    int qTotal = request.getAttribute("totalQuestions") != null
                 ? (int) request.getAttribute("totalQuestions") : 1;

    if (progress == null || question == null) {
        response.sendRedirect(ctx + "/quiz/tags"); return;
    }
    int remainingTime = progress.getRemainingTime();
    int warningCount  = progress.getWarningCount();
    String sessionId  = progress.getSessionId();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>Q<%=qNum%>/<%=qTotal%> – Quiz-Portal</title>
<script src="https://cdn.tailwindcss.com"></script>
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;700&display=swap" rel="stylesheet"/>
<style>
*{font-family:'Space Grotesk',sans-serif;}
.mono{font-family:'JetBrains Mono',monospace;}
.option-btn{display:flex;align-items:flex-start;gap:.75rem;width:100%;padding:1rem 1.25rem;
  background:#111827;border:1.5px solid #1f2937;border-radius:.75rem;text-align:left;
  cursor:pointer;transition:all .15s;color:#e5e7eb;}
.option-btn:hover{border-color:#22d3ee;background:rgba(34,211,238,.05);}
.option-btn.selected{border-color:#22d3ee;background:rgba(34,211,238,.1);color:#22d3ee;}
.option-btn.selected .opt-letter{background:#22d3ee;color:#030712;}
.opt-letter{min-width:1.75rem;height:1.75rem;border-radius:.375rem;
  background:#1f2937;display:flex;align-items:center;justify-content:center;
  font-size:.75rem;font-weight:700;font-family:JetBrains Mono,monospace;flex-shrink:0;}
#timer{transition:color .3s;}
.timer-warn{color:#f87171!important;}
.timer-critical{color:#ef4444!important;animation:pulse 1s infinite;}
@keyframes pulse{0%,100%{opacity:1}50%{opacity:.5}}
.warning-banner{background:rgba(251,191,36,.1);border:1px solid rgba(251,191,36,.3);
  color:#fbbf24;padding:.5rem 1rem;border-radius:.5rem;font-size:.8rem;
  display:none;margin-bottom:1rem;}
</style>
</head>
<body class="bg-gray-950 text-gray-100 min-h-screen flex flex-col">

<!-- Top bar -->
<header class="bg-gray-900 border-b border-gray-800 px-6 py-3 flex items-center justify-between sticky top-0 z-30">
  <div class="flex items-center gap-4">
    <span class="mono text-cyan-400 font-bold">&lt;QP/&gt;</span>
    <div class="flex items-center gap-2">
      <span class="text-xs text-gray-500">Question</span>
      <span class="mono font-bold text-white"><%=qNum%>/<%=qTotal%></span>
    </div>
    <!-- Progress bar -->
    <div class="hidden sm:flex items-center gap-2">
      <div class="w-32 bg-gray-800 rounded-full h-1.5">
        <div class="bg-cyan-400 h-1.5 rounded-full transition-all"
             style="width:<%=(qNum * 100 / qTotal)%>%"></div>
      </div>
      <span class="text-xs text-gray-500"><%=(qNum * 100 / qTotal)%>%</span>
    </div>
  </div>
  <div class="flex items-center gap-4">
    <!-- Difficulty badge -->
    <span class="text-xs px-2 py-1 rounded-full
      <%="easy".equals(question.getDifficulty())?"bg-green-500/15 text-green-400":
         "hard".equals(question.getDifficulty())?"bg-red-500/15 text-red-400":
         "bg-yellow-500/15 text-yellow-400"%>">
      <%=question.getDifficulty()%>
    </span>
    <!-- Timer -->
    <div class="flex items-center gap-2 bg-gray-800 px-3 py-1.5 rounded-lg">
      <span class="text-gray-400">⏱</span>
      <span id="timer" class="mono font-bold text-white text-sm">--:--</span>
    </div>
    <!-- Warning count -->
    <% if (warningCount > 0) { %>
    <div class="flex items-center gap-1 text-yellow-400 text-xs">
      <span>⚠️</span><span><%=warningCount%>/3</span>
    </div>
    <% } %>
    <!-- Early submit -->
    <button id="submitBtn" onclick="confirmSubmit()"
      class="text-xs px-3 py-1.5 border border-gray-700 hover:border-red-400 hover:text-red-400 rounded-lg transition">
      Submit Quiz
    </button>
  </div>
</header>

<!-- Warning banner (tab switch) -->
<div id="warnBanner" class="warning-banner mx-6 mt-4">
  ⚠️ <strong>Warning:</strong> Do not switch tabs during the quiz. (<span id="warnCount"><%=warningCount%></span>/3 warnings)
</div>

<!-- Main -->
<main class="flex-1 flex items-center justify-center px-4 py-8">
  <div class="w-full max-w-2xl">

    <!-- Question -->
    <div class="bg-gray-900 border border-gray-800 rounded-2xl p-6 mb-6">
      <div class="flex items-start gap-3">
        <span class="mono text-xs text-gray-600 shrink-0 mt-1 pt-0.5">Q<%=qNum%></span>
        <p class="text-base sm:text-lg font-medium leading-relaxed text-gray-100">
          <%=question.getQuestionText()%>
        </p>
      </div>
    </div>

    <!-- Options form -->
    <form id="answerForm" method="post" action="<%=ctx%>/quiz/answer">
      <input type="hidden" name="sessionId"     value="<%=sessionId%>"/>
      <input type="hidden" name="questionId"    value="<%=question.getId()%>"/>
      <input type="hidden" id="remainingTime"   name="remainingTime" value="<%=remainingTime%>"/>
      <input type="hidden" id="selectedInput"   name="selected" value="0"/>

      <div class="space-y-3 mb-8">
        <% String[] opts = {question.getOption1(), question.getOption2(),
                             question.getOption3(), question.getOption4()};
           String[] letters = {"A","B","C","D"};
           for (int i = 0; i < 4; i++) { %>
        <button type="button" class="option-btn" data-val="<%=i+1%>" onclick="selectOption(this)">
          <span class="opt-letter"><%=letters[i]%></span>
          <span class="text-sm leading-relaxed"><%=opts[i]%></span>
        </button>
        <% } %>
      </div>

      <div class="flex justify-between items-center">
        <span class="text-xs text-gray-600">
          <%=qNum < qTotal ? "Next question loads automatically after submission" : "This is the last question"%>
        </span>
        <button type="submit" id="nextBtn"
          class="px-6 py-3 bg-cyan-500 hover:bg-cyan-400 text-gray-950 font-bold rounded-xl text-sm transition disabled:opacity-40 disabled:cursor-not-allowed"
          disabled>
          <%=qNum < qTotal ? "Next →" : "Finish Quiz ✓"%>
        </button>
      </div>
    </form>

  </div>
</main>

<!-- Auto-submit form -->
<form id="autoSubmitForm" method="post" action="<%=ctx%>/quiz/submit" style="display:none">
  <input type="hidden" name="sessionId" value="<%=sessionId%>"/>
  <input type="hidden" id="submitStatus" name="status" value="auto_submitted"/>
</form>

<script>
const REMAINING  = <%=remainingTime%>;
const SESSION_ID = '<%=sessionId%>';
const CTX        = '<%=ctx%>';
const MAX_WARN   = 3;
let timeLeft     = REMAINING;
let warnings     = <%=warningCount%>;
let saveInterval, timerInterval;

// ── Timer ─────────────────────────────────────────────────────────────
function fmtTime(s) {
  const m = Math.floor(s/60), sec = s%60;
  return String(m).padStart(2,'0') + ':' + String(sec).padStart(2,'0');
}
function tick() {
  timeLeft--;
  const el = document.getElementById('timer');
  el.textContent = fmtTime(timeLeft);
  document.getElementById('remainingTime').value = timeLeft;
  if (timeLeft <= 60)  el.classList.add('timer-critical');
  else if (timeLeft <= 120) el.classList.add('timer-warn');
  if (timeLeft <= 0) autoSubmit('auto_submitted');
}
document.getElementById('timer').textContent = fmtTime(timeLeft);
timerInterval = setInterval(tick, 1000);

// ── Auto-save every 15s ────────────────────────────────────────────────
saveInterval = setInterval(function() {
  fetch(CTX + '/quiz/save', {
    method:'POST',
    headers:{'Content-Type':'application/x-www-form-urlencoded'},
    body: 'sessionId=' + encodeURIComponent(SESSION_ID)
        + '&remainingTime=' + timeLeft
        + '&warningCount=' + warnings
  });
}, 15000);

// ── Option selection ───────────────────────────────────────────────────
function selectOption(btn) {
  document.querySelectorAll('.option-btn').forEach(b => b.classList.remove('selected'));
  btn.classList.add('selected');
  document.getElementById('selectedInput').value = btn.dataset.val;
  document.getElementById('nextBtn').disabled = false;
}

// ── Auto-submit ────────────────────────────────────────────────────────
function autoSubmit(status) {
  clearInterval(timerInterval);
  clearInterval(saveInterval);
  document.getElementById('submitStatus').value = status;
  document.getElementById('autoSubmitForm').submit();
}

// ── Manual submit ──────────────────────────────────────────────────────
function confirmSubmit() {
  if (confirm('Submit the quiz now? Your current answers will be saved.')) {
    autoSubmit('completed');
  }
}

// ── Anti-cheat: tab switch detection ──────────────────────────────────
document.addEventListener('visibilitychange', function() {
  if (document.hidden) {
    warnings++;
    document.getElementById('warnCount').textContent = warnings;
    document.getElementById('warnBanner').style.display = 'block';
    if (warnings >= MAX_WARN) {
      alert('Quiz auto-submitted: too many tab switches.');
      autoSubmit('auto_submitted');
    }
    // Persist warning count
    fetch(CTX + '/quiz/save', {
      method:'POST',
      headers:{'Content-Type':'application/x-www-form-urlencoded'},
      body: 'sessionId=' + encodeURIComponent(SESSION_ID)
          + '&remainingTime=' + timeLeft
          + '&warningCount=' + warnings
    });
  }
});

// ── Prevent back navigation ────────────────────────────────────────────
history.pushState(null, '', location.href);
window.addEventListener('popstate', function() {
  history.pushState(null, '', location.href);
});

// ── Block right-click ──────────────────────────────────────────────────
document.addEventListener('contextmenu', e => e.preventDefault());

// ── Prevent F12 / DevTools shortcuts ─────────────────────────────────
document.addEventListener('keydown', function(e) {
  if (e.key === 'F12' || (e.ctrlKey && e.shiftKey && e.key === 'I') ||
      (e.ctrlKey && e.shiftKey && e.key === 'J') || (e.ctrlKey && e.key === 'U')) {
    e.preventDefault();
  }
});
</script>
</body>
</html>
