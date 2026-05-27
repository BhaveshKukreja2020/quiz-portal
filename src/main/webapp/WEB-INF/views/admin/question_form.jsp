<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx=request.getContextPath();
   boolean isEdit=request.getAttribute("question")!=null;
   String pageTitle=isEdit?"Edit Question":"Add Question"; %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %></head>
<body>
<div class="app-sidebar">
  <div class="brand">Quiz-Portal<span class="dot"></span> <small style="font-size:.65rem;color:var(--muted);font-weight:400">Admin</small></div>
  <nav style="padding:.5rem 0;flex:1">
    <a href="<%=ctx%>/admin/dashboard"  class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/admin/questions"  class="nav-item active"><i class="bi bi-question-circle"></i>Questions</a>
    <a href="<%=ctx%>/admin/tags"       class="nav-item"><i class="bi bi-tags"></i>Tags</a>
    <a href="<%=ctx%>/admin/analytics"  class="nav-item"><i class="bi bi-bar-chart-line"></i>Analytics</a>
  </nav>
  <div class="sidebar-footer"><a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a></div>
</div>
<main class="app-main">
  <div class="page-header" style="display:flex;align-items:center;gap:1rem;">
    <a href="<%=ctx%>/admin/questions" class="btn-ghost" style="padding:.35rem .65rem"><i class="bi bi-arrow-left"></i></a>
    <div><h1><%=isEdit?"Edit Question":"Add New Question"%></h1><p><%=isEdit?"Update question details":"Add a new question to the bank"%></p></div>
  </div>
  <% if(request.getAttribute("error")!=null){ %><div class="cq-alert cq-alert-error"><i class="bi bi-exclamation-circle"></i><span><%=request.getAttribute("error")%></span></div><% } %>

  <div style="max-width:760px;">
    <form method="post" action="<%=ctx%>/admin/question/save" novalidate>
      <input type="hidden" name="id" value="${question.id}"/>

      <div class="cq-card" style="margin-bottom:1rem;">
        <div class="cq-card-header">Question Details</div>
        <div class="cq-card-body" style="display:flex;flex-direction:column;gap:1rem;">
          <div>
            <label class="cq-label">Question Text *</label>
            <textarea name="questionText" class="cq-textarea" rows="3" placeholder="Enter the question..." required>${question.questionText}</textarea>
          </div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem;">
            <div>
              <label class="cq-label">Difficulty *</label>
              <select name="difficulty" class="cq-select" required>
                <option value="easy"   ${question.difficulty=='easy'?'selected':''}>Easy</option>
                <option value="medium" ${question.difficulty=='medium'||question.difficulty==null?'selected':''}>Medium</option>
                <option value="hard"   ${question.difficulty=='hard'?'selected':''}>Hard</option>
              </select>
            </div>
            <div>
              <label class="cq-label">Correct Answer *</label>
              <select name="correctAnswer" class="cq-select" required>
                <option value="1" ${question.correctAnswer==1?'selected':''}>A – Option 1</option>
                <option value="2" ${question.correctAnswer==2?'selected':''}>B – Option 2</option>
                <option value="3" ${question.correctAnswer==3?'selected':''}>C – Option 3</option>
                <option value="4" ${question.correctAnswer==4?'selected':''}>D – Option 4</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <div class="cq-card" style="margin-bottom:1rem;">
        <div class="cq-card-header">Answer Options</div>
        <div class="cq-card-body" style="display:flex;flex-direction:column;gap:.75rem;">
          <c:forEach begin="1" end="4" var="i">
            <div>
              <label class="cq-label">Option ${i==1?'A':i==2?'B':i==3?'C':'D'} *</label>
              <input type="text" name="option${i}" class="cq-input" placeholder="Enter option ${i==1?'A':i==2?'B':i==3?'C':'D'}..." required
                value="${i==1?question.option1:i==2?question.option2:i==3?question.option3:question.option4}"/>
            </div>
          </c:forEach>
        </div>
      </div>

      <div class="cq-card" style="margin-bottom:1rem;">
        <div class="cq-card-header">Tags / Topics</div>
        <div class="cq-card-body">
          <label class="cq-label">Select topics (hold Ctrl/Cmd for multiple)</label>
          <select name="tagIds" multiple class="cq-select" style="height:140px;">
            <c:forEach var="t" items="${tags}">
              <option value="${t.id}"
                <c:forEach var="qt" items="${question.tags}"><c:if test="${qt.id==t.id}">selected</c:if></c:forEach>
              >${t.tagName}</option>
            </c:forEach>
          </select>
        </div>
      </div>

      <div class="cq-card" style="margin-bottom:1.5rem;">
        <div class="cq-card-header">Explanation <span style="font-size:.7rem;color:var(--muted);font-weight:400">optional</span></div>
        <div class="cq-card-body">
          <textarea name="explanation" class="cq-textarea" rows="3" placeholder="Explain why the correct answer is correct (shown to students after answering)...">${question.explanation}</textarea>
        </div>
      </div>

      <div style="display:flex;gap:.75rem;">
        <button type="submit" class="btn-primary-cq" style="padding:.6rem 1.5rem">
          <i class="bi bi-check2"></i> <%=isEdit?"Save Changes":"Add Question"%>
        </button>
        <a href="<%=ctx%>/admin/questions" class="btn-ghost" style="padding:.6rem 1.25rem">Cancel</a>
      </div>
    </form>
  </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>
