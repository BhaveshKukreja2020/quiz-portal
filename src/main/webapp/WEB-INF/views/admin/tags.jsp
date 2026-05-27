<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx=request.getContextPath(); String pageTitle="Manage Tags"; %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %></head>
<body>
<div class="app-sidebar">
  <div class="brand">Code<span class="dot">Quiz</span> <small style="font-size:.65rem;color:var(--muted)">Admin</small></div>
  <nav style="padding:.5rem 0;flex:1">
    <a href="<%=ctx%>/admin/dashboard" class="nav-item"><i class="bi bi-grid-1x2"></i>Dashboard</a>
    <a href="<%=ctx%>/admin/questions" class="nav-item"><i class="bi bi-question-circle"></i>Questions</a>
    <a href="<%=ctx%>/admin/tags"      class="nav-item active"><i class="bi bi-tags"></i>Tags</a>
    <a href="<%=ctx%>/admin/analytics" class="nav-item"><i class="bi bi-bar-chart-line"></i>Analytics</a>
  </nav>
  <div class="sidebar-footer"><a href="<%=ctx%>/logout" class="nav-item" style="color:#f87171;margin:0"><i class="bi bi-box-arrow-left"></i>Sign out</a></div>
</div>
<main class="app-main">
  <div class="page-header"><h1>Tags / Topics</h1><p>Manage quiz categories and topics</p></div>
  <% if(request.getAttribute("error")!=null){ %><div class="cq-alert cq-alert-error"><i class="bi bi-exclamation-circle"></i><span><%=request.getAttribute("error")%></span></div><% } %>

  <div style="display:grid;grid-template-columns:340px 1fr;gap:1.5rem;align-items:start;">
    <!-- Add form -->
    <div class="cq-card">
      <div class="cq-card-header">Add New Tag</div>
      <div class="cq-card-body">
        <form method="post" action="<%=ctx%>/admin/tag/save">
          <input type="hidden" name="id" value=""/>
          <label class="cq-label">Tag Name *</label>
          <input type="text" name="tagName" class="cq-input" placeholder="e.g. Python, Data Science" required style="margin-bottom:.75rem"/>
          <button type="submit" class="btn-primary-cq" style="padding:.5rem 1.25rem;width:100%"><i class="bi bi-plus-lg"></i> Add Tag</button>
        </form>
      </div>
    </div>

    <!-- Tags list -->
    <div class="cq-card">
      <div class="cq-card-header">All Tags <span style="font-size:.75rem;color:var(--muted);font-weight:400">(${tags.size()} total)</span></div>
      <div style="overflow-x:auto;">
        <table class="cq-table">
          <thead><tr><th>#</th><th>Tag Name</th><th style="width:160px;text-align:center">Actions</th></tr></thead>
          <tbody>
            <c:choose>
              <c:when test="${empty tags}">
                <tr><td colspan="3" style="text-align:center;padding:2rem;color:var(--muted)">No tags yet. Add your first topic.</td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="t" items="${tags}" varStatus="st">
                <tr>
                  <td style="color:var(--muted);font-size:.75rem">${st.index+1}</td>
                  <td><span style="background:var(--primary-lt);color:var(--primary);padding:.2rem .7rem;border-radius:.375rem;font-size:.78rem;font-weight:600">${t.tagName}</span></td>
                  <td style="text-align:center">
                    <div style="display:flex;align-items:center;justify-content:center;gap:.4rem">
                      <button onclick="editTag(${t.id},'${t.tagName}')" class="btn-ghost" style="padding:.3rem .6rem;font-size:.75rem"><i class="bi bi-pencil"></i></button>
                      <form method="post" action="<%=ctx%>/admin/tag/delete" style="display:inline" onsubmit="return confirm('Delete tag: ${t.tagName}?')">
                        <input type="hidden" name="id" value="${t.id}"/>
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
  </div>
</main>

<!-- Edit modal -->
<div id="editModal" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,.6);z-index:999;align-items:center;justify-content:center;">
  <div style="background:var(--surface);border:1px solid var(--border);border-radius:1rem;padding:1.5rem;width:340px;">
    <h3 style="font-size:.95rem;font-weight:700;margin-bottom:1rem">Edit Tag</h3>
    <form method="post" action="<%=ctx%>/admin/tag/save">
      <input type="hidden" name="id" id="editId"/>
      <label class="cq-label">Tag Name</label>
      <input type="text" name="tagName" id="editName" class="cq-input" required style="margin-bottom:1rem"/>
      <div style="display:flex;gap:.5rem">
        <button type="submit" class="btn-primary-cq" style="flex:1;padding:.55rem">Save</button>
        <button type="button" onclick="closeModal()" class="btn-ghost" style="flex:1;padding:.55rem">Cancel</button>
      </div>
    </form>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
function editTag(id,name){
  document.getElementById('editId').value=id;
  document.getElementById('editName').value=name;
  document.getElementById('editModal').style.display='flex';
}
function closeModal(){ document.getElementById('editModal').style.display='none'; }
</script>
</body></html>
