<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx=request.getContextPath(); String pageTitle="Admin Dashboard";
   com.quizportal.model.User u=(com.quizportal.model.User)session.getAttribute("loggedInUser"); %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %></head>
<body>
<div class="app-sidebar">
  <div class="brand">Quiz-Portal<span class="dot"></span> <small style="font-size:.65rem;color:var(--muted);font-weight:400">Admin</small></div>
  <nav style="padding:.5rem 0;flex:1">
    <div class="nav-section">Main</div>
    <a href="<%=ctx%>/admin/dashboard"  class="nav-item active"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/admin/analytics"  class="nav-item"><i class="bi bi-bar-chart-line"></i>Analytics</a>
    <a href="<%=ctx%>/admin/questions"  class="nav-item"><i class="bi bi-question-circle"></i>Questions</a>
    <a href="<%=ctx%>/admin/tags"       class="nav-item"><i class="bi bi-tags"></i>Tags</a>
    <a href="<%=ctx%>/admin/reports"    class="nav-item"><i class="bi bi-file-earmark-text"></i>Reports</a>
    <a href="<%=ctx%>/leaderboard"      class="nav-item"><i class="bi bi-trophy"></i>Leaderboard</a>
  </nav>
  <div class="sidebar-footer">
    <div style="display:flex;align-items:center;gap:.6rem;padding:.25rem .5rem;margin-bottom:.5rem;">
      <div class="avatar" style="width:1.75rem;height:1.75rem;font-size:.7rem;"><%=u!=null?u.getName().substring(0,1).toUpperCase():"A"%></div>
      <div><p style="font-size:.78rem;font-weight:600;margin:0"><%=u!=null?u.getName():"Admin"%></p><p style="font-size:.68rem;color:var(--muted);margin:0">Administrator</p></div>
    </div>
    <a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a>
  </div>
</div>

<main class="app-main">
  <div class="page-header">
    <h1>Dashboard</h1>
    <p>Platform overview and quick actions</p>
  </div>

  <!-- Stats row -->
  <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:1rem;margin-bottom:1.5rem;">
    <div class="stat-card">
      <div style="display:flex;align-items:flex-start;justify-content:space-between;">
        <div><div class="stat-val" style="color:var(--primary)">${totalUsers}</div><div class="stat-lbl">Students</div></div>
        <div class="stat-icon" style="background:var(--primary-lt);color:var(--primary)"><i class="bi bi-people"></i></div>
      </div>
    </div>
    <div class="stat-card">
      <div style="display:flex;align-items:flex-start;justify-content:space-between;">
        <div><div class="stat-val" style="color:var(--info)">${totalQuestions}</div><div class="stat-lbl">Questions</div></div>
        <div class="stat-icon" style="background:rgba(6,182,212,.15);color:var(--info)"><i class="bi bi-question-lg"></i></div>
      </div>
    </div>
    <div class="stat-card">
      <div style="display:flex;align-items:flex-start;justify-content:space-between;">
        <div><div class="stat-val" style="color:var(--success)">${totalAttempts}</div><div class="stat-lbl">Quiz Attempts</div></div>
        <div class="stat-icon" style="background:rgba(34,197,94,.15);color:var(--success)"><i class="bi bi-clipboard-check"></i></div>
      </div>
    </div>
    <div class="stat-card">
      <div style="display:flex;align-items:flex-start;justify-content:space-between;">
        <div><div class="stat-val" style="color:var(--warning)">${totalTags}</div><div class="stat-lbl">Topics/Tags</div></div>
        <div class="stat-icon" style="background:rgba(245,158,11,.15);color:var(--warning)"><i class="bi bi-tags"></i></div>
      </div>
    </div>
  </div>

  <!-- Quick actions -->
  <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem;">
    <div class="cq-card">
      <div class="cq-card-header"><span>Quick Actions</span></div>
      <div class="cq-card-body" style="display:flex;flex-direction:column;gap:.5rem;">
        <a href="<%=ctx%>/admin/question/add" class="btn-primary-cq" style="padding:.55rem 1rem;display:flex;align-items:center;gap:.5rem;width:fit-content"><i class="bi bi-plus-circle"></i> Add Question</a>
        <a href="<%=ctx%>/admin/tags"         class="btn-ghost"       style="padding:.55rem 1rem;display:flex;align-items:center;gap:.5rem;width:fit-content"><i class="bi bi-tag"></i> Manage Tags</a>
        <a href="<%=ctx%>/admin/analytics"    class="btn-ghost"       style="padding:.55rem 1rem;display:flex;align-items:center;gap:.5rem;width:fit-content"><i class="bi bi-graph-up"></i> View Analytics</a>
        <a href="<%=ctx%>/leaderboard"        class="btn-ghost"       style="padding:.55rem 1rem;display:flex;align-items:center;gap:.5rem;width:fit-content"><i class="bi bi-trophy"></i> Leaderboard</a>
      </div>
    </div>
    <div class="cq-card">
      <div class="cq-card-header"><span>System Status</span></div>
      <div class="cq-card-body">
        <div style="display:flex;flex-direction:column;gap:.75rem;font-size:.82rem;">
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="color:var(--muted)">Database</span>
            <span style="color:var(--success);display:flex;align-items:center;gap:.3rem"><i class="bi bi-circle-fill" style="font-size:.4rem"></i>Connected</span>
          </div>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="color:var(--muted)">Adaptive Engine</span>
            <span style="color:var(--success);display:flex;align-items:center;gap:.3rem"><i class="bi bi-circle-fill" style="font-size:.4rem"></i>Active</span>
          </div>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="color:var(--muted)">Anti-Cheat</span>
            <span style="color:var(--success);display:flex;align-items:center;gap:.3rem"><i class="bi bi-circle-fill" style="font-size:.4rem"></i>Enabled</span>
          </div>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="color:var(--muted)">Auto-Save</span>
            <span style="color:var(--success);display:flex;align-items:center;gap:.3rem"><i class="bi bi-circle-fill" style="font-size:.4rem"></i>Every 15s</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>
