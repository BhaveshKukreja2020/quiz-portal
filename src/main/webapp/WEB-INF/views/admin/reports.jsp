<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx=request.getContextPath(); String pageTitle="Reports"; %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %></head>
<body>
<div class="app-sidebar">
  <div class="brand">Quiz-Portal<span class="dot"></span> <small style="font-size:.65rem;color:var(--muted)">Admin</small></div>
  <nav style="padding:.5rem 0;flex:1">
    <a href="<%=ctx%>/admin/dashboard" class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/admin/analytics" class="nav-item"><i class="bi bi-bar-chart-line"></i>Analytics</a>
    <a href="<%=ctx%>/admin/questions" class="nav-item"><i class="bi bi-question-circle"></i>Questions</a>
    <a href="<%=ctx%>/admin/tags"      class="nav-item"><i class="bi bi-tags"></i>Tags</a>
    <a href="<%=ctx%>/admin/reports"   class="nav-item active"><i class="bi bi-file-earmark-text"></i>Reports</a>
  </nav>
  <div class="sidebar-footer"><a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a></div>
</div>
<main class="app-main">
  <div class="page-header"><h1>Reports</h1><p>Platform usage summary</p></div>
  <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:1rem;margin-bottom:1.5rem;">
    <div class="stat-card"><div class="stat-val" style="color:var(--primary)">${totalUsers}</div><div class="stat-lbl">Registered Students</div></div>
    <div class="stat-card"><div class="stat-val" style="color:var(--info)">${totalQuestions}</div><div class="stat-lbl">Questions in Bank</div></div>
    <div class="stat-card"><div class="stat-val" style="color:var(--success)">${totalAttempts}</div><div class="stat-lbl">Total Attempts</div></div>
  </div>
  <div class="cq-card">
    <div class="cq-card-body" style="text-align:center;padding:3rem;">
      <i class="bi bi-bar-chart-line" style="font-size:3rem;color:var(--primary);display:block;margin-bottom:1rem"></i>
      <p style="font-weight:600;margin-bottom:.4rem">Detailed Analytics Available</p>
      <p style="color:var(--muted);font-size:.82rem;margin-bottom:1.25rem">View charts, score distributions, top performers, and question analytics</p>
      <a href="<%=ctx%>/admin/analytics" class="btn-primary-cq" style="padding:.6rem 1.5rem">Open Analytics Dashboard</a>
    </div>
  </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>
