<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx=request.getContextPath(); String pageTitle="Select Topics";
   com.quizportal.model.User u=(com.quizportal.model.User)session.getAttribute("loggedInUser"); %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %>
<style>
.tag-chip{display:flex;align-items:center;gap:.5rem;padding:.6rem 1rem;background:var(--surface2);
  border:1.5px solid var(--border);border-radius:var(--radius);cursor:pointer;transition:all .15s;user-select:none;font-size:.83rem;}
.tag-chip:hover{border-color:var(--primary);background:var(--primary-lt);}
.tag-chip.selected{border-color:var(--primary);background:var(--primary-lt);color:var(--primary);font-weight:600;}
.tag-chip input{display:none;}
.tag-chip .chk{width:1rem;height:1rem;border-radius:.25rem;border:1.5px solid var(--border);background:transparent;display:flex;align-items:center;justify-content:center;transition:all .15s;flex-shrink:0;}
.tag-chip.selected .chk{background:var(--primary);border-color:var(--primary);}
</style>
</head><body>
<div class="app-sidebar">
  <div class="brand">Quiz-Portal<span class="dot">Quiz</span></div>
  <nav style="padding:.5rem 0;flex:1">
    <a href="<%=ctx%>/student/dashboard" class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/quiz/tags"         class="nav-item active"><i class="bi bi-play-circle"></i>Take a Quiz</a>
    <a href="<%=ctx%>/quiz/history"      class="nav-item"><i class="bi bi-clock-history"></i>My Attempts</a>
    <a href="<%=ctx%>/leaderboard"       class="nav-item"><i class="bi bi-trophy"></i>Leaderboard</a>
  </nav>
  <div class="sidebar-footer"><a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a></div>
</div>
<main class="app-main">
  <div class="page-header"><h1>Start a Quiz</h1><p>Select one or more topics to begin. 10 adaptive questions, 10 minutes.</p></div>

  <% if(request.getAttribute("error")!=null){ %>
  <div class="cq-alert cq-alert-error"><i class="bi bi-exclamation-circle"></i><span><%=request.getAttribute("error")%></span></div>
  <% } %>

  <!-- Resume banner -->
  <% if(Boolean.TRUE.equals(request.getAttribute("hasResumable"))){ %>
  <div style="background:rgba(245,158,11,.08);border:1px solid rgba(245,158,11,.25);border-radius:var(--radius);padding:1rem 1.25rem;margin-bottom:1.5rem;display:flex;align-items:center;justify-content:space-between;gap:1rem;flex-wrap:wrap;">
    <div style="display:flex;align-items:center;gap:.75rem">
      <i class="bi bi-pause-circle" style="color:var(--warning);font-size:1.25rem"></i>
      <div><p style="font-weight:600;margin:0;font-size:.85rem">You have an unfinished quiz</p><p style="color:var(--muted);font-size:.78rem;margin:0">Your progress has been saved</p></div>
    </div>
    <a href="<%=ctx%>/quiz/resume" class="btn-primary-cq" style="background:var(--warning);padding:.5rem 1.25rem;white-space:nowrap"><i class="bi bi-play-fill"></i> Resume</a>
  </div>
  <% } %>

  <!-- Topic selection -->
  <form method="post" action="<%=ctx%>/quiz/start" id="quizForm">
    <div class="cq-card" style="margin-bottom:1.5rem;">
      <div class="cq-card-header">
        <span>Choose Topics</span>
        <span id="selectedCount" style="font-size:.75rem;color:var(--muted)">0 selected</span>
      </div>
      <div class="cq-card-body">
        <c:choose>
          <c:when test="${empty tags}">
            <div style="text-align:center;padding:2rem;color:var(--muted)">
              <i class="bi bi-tags" style="font-size:2rem;display:block;margin-bottom:.5rem"></i>
              No topics available. Ask an admin to add tags and questions.
            </div>
          </c:when>
          <c:otherwise>
            <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(160px,1fr));gap:.625rem;">
              <c:forEach var="t" items="${tags}">
                <label class="tag-chip" id="chip-${t.id}">
                  <input type="checkbox" name="tags" value="${t.id}" onchange="updateCount()"/>
                  <div class="chk"><i class="bi bi-check2" style="font-size:.65rem;color:#fff;display:none"></i></div>
                  <span>${t.tagName}</span>
                </label>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <!-- Quiz info -->
    <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:1rem;margin-bottom:1.5rem;">
      <div class="stat-card" style="text-align:center;">
        <div class="stat-val" style="color:var(--info);font-size:1.5rem">10</div>
        <div class="stat-lbl">Questions</div>
      </div>
      <div class="stat-card" style="text-align:center;">
        <div class="stat-val" style="color:var(--warning);font-size:1.5rem">10m</div>
        <div class="stat-lbl">Time Limit</div>
      </div>
      <div class="stat-card" style="text-align:center;">
        <div class="stat-val" style="color:var(--success);font-size:1.5rem">3</div>
        <div class="stat-lbl">Difficulty Levels</div>
      </div>
    </div>

    <button type="submit" id="startBtn" class="btn-primary-cq" style="padding:.7rem 2rem;font-size:.9rem" disabled>
      <i class="bi bi-play-fill"></i> Start Quiz
    </button>
    <p style="font-size:.73rem;color:var(--muted);margin-top:.75rem"><i class="bi bi-info-circle"></i> Questions adapt to your performance. Do not switch tabs during the quiz.</p>
  </form>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
function updateCount(){
  const chips=document.querySelectorAll('.tag-chip');
  let n=0;
  chips.forEach(chip=>{
    const cb=chip.querySelector('input[type=checkbox]');
    const chk=chip.querySelector('.chk i');
    if(cb.checked){chip.classList.add('selected');chk.style.display='';n++;}
    else{chip.classList.remove('selected');chk.style.display='none';}
  });
  document.getElementById('selectedCount').textContent=n+' selected';
  document.getElementById('startBtn').disabled=(n===0);
}
</script>
</body></html>
