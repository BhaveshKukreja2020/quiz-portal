<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx=request.getContextPath(); String pageTitle="Manage Questions";
   com.quizportal.model.User u=(com.quizportal.model.User)session.getAttribute("loggedInUser"); %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %>
<style>.search-bar{background:var(--surface2);border:1px solid var(--border);color:var(--text);border-radius:var(--radius);padding:.45rem .875rem;font-size:.82rem;outline:none;width:220px;}
.search-bar:focus{border-color:var(--primary);}
</style>
</head><body>
<div class="app-sidebar">
  <div class="brand">Quiz-Portal<span class="dot"></span> <small style="font-size:.65rem;color:var(--muted);font-weight:400">Admin</small></div>
  <nav style="padding:.5rem 0;flex:1">
    <a href="<%=ctx%>/admin/dashboard"  class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/admin/analytics"  class="nav-item"><i class="bi bi-bar-chart-line"></i>Analytics</a>
    <a href="<%=ctx%>/admin/questions"  class="nav-item active"><i class="bi bi-question-circle"></i>Questions</a>
    <a href="<%=ctx%>/admin/tags"       class="nav-item"><i class="bi bi-tags"></i>Tags</a>
    <a href="<%=ctx%>/admin/reports"    class="nav-item"><i class="bi bi-file-earmark-text"></i>Reports</a>
    <a href="<%=ctx%>/leaderboard"      class="nav-item"><i class="bi bi-trophy"></i>Leaderboard</a>
  </nav>
  <div class="sidebar-footer"><a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a></div>
</div>
<main class="app-main">
  <div class="page-header" style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:1rem;">
    <div><h1>Questions</h1><p>${questions != null ? questions.size() : 0} questions in the bank</p></div>
    <a href="<%=ctx%>/admin/question/add" class="btn-primary-cq" style="display:flex;align-items:center;gap:.4rem;padding:.5rem 1rem"><i class="bi bi-plus-lg"></i>Add Question</a>
  </div>

  <% if(request.getAttribute("error")!=null){ %><div class="cq-alert cq-alert-error"><i class="bi bi-exclamation-circle"></i><span><%=request.getAttribute("error")%></span></div><% } %>

  <!-- Filter bar -->
  <div class="cq-card" style="margin-bottom:1rem;padding:.75rem 1.25rem;display:flex;align-items:center;gap:.75rem;flex-wrap:wrap;">
    <input type="text" id="searchInput" class="search-bar" placeholder="&#xF52A; Search questions..." oninput="filterTable()"/>
    <select id="diffFilter" class="cq-select" style="width:130px;" onchange="filterTable()">
      <option value="">All Difficulty</option>
      <option value="easy">Easy</option>
      <option value="medium">Medium</option>
      <option value="hard">Hard</option>
    </select>
    <span style="font-size:.75rem;color:var(--muted);margin-left:auto" id="rowCount"></span>
  </div>

  <div class="cq-card">
    <div style="overflow-x:auto;">
      <table class="cq-table" id="qTable">
        <thead><tr><th style="width:40px">#</th><th>Question</th><th>Difficulty</th><th>Tags</th><th style="width:140px;text-align:center">Actions</th></tr></thead>
        <tbody>
          <c:choose>
            <c:when test="${empty questions}">
              <tr><td colspan="5" style="text-align:center;padding:3rem;color:var(--muted)">
                <i class="bi bi-inbox" style="font-size:2rem;display:block;margin-bottom:.5rem"></i>
                No questions yet. <a href="<%=ctx%>/admin/question/add" style="color:var(--primary)">Add the first one</a>
              </td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="q" items="${questions}" varStatus="st">
              <tr>
                <td style="color:var(--muted);font-size:.75rem">${st.index+1}</td>
                <td style="max-width:400px">
                  <p style="margin:0;font-weight:500;font-size:.82rem;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;" title="${q.questionText}">${q.questionText}</p>
                </td>
                <td><span class="badge-${q.difficulty}">${q.difficulty}</span></td>
                <td style="font-size:.75rem;color:var(--muted)">
                  <c:forEach var="t" items="${q.tags}" varStatus="ts">${t.tagName}<c:if test="${!ts.last}">, </c:if></c:forEach>
                  <c:if test="${empty q.tags}">—</c:if>
                </td>
                <td style="text-align:center">
                  <div style="display:flex;align-items:center;justify-content:center;gap:.4rem">
                    <a href="<%=ctx%>/admin/question/edit?id=${q.id}" class="btn-ghost" style="padding:.3rem .6rem;font-size:.75rem"><i class="bi bi-pencil"></i></a>
                    <form method="post" action="<%=ctx%>/admin/question/delete" style="display:inline" onsubmit="return confirm('Delete this question?')">
                      <input type="hidden" name="id" value="${q.id}"/>
                      <button type="submit" class="btn-danger-cq" style="padding:.3rem .6rem;font-size:.75rem"><i class="bi bi-trash"></i></button>
                    </form>
                  </div>
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
<script>
function filterTable(){
  const q=(document.getElementById('searchInput').value||'').toLowerCase();
  const d=(document.getElementById('diffFilter').value||'').toLowerCase();
  const rows=document.querySelectorAll('#qTable tbody tr');
  let vis=0;
  rows.forEach(r=>{
    const txt=r.innerText.toLowerCase();
    const show=txt.includes(q)&&(d===''||txt.includes(d));
    r.style.display=show?'':'none';
    if(show)vis++;
  });
  document.getElementById('rowCount').textContent=vis+' shown';
}
filterTable();
</script>
</body></html>
