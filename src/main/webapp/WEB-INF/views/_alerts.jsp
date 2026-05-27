<%-- =========================================================
     _alerts.jsp  —  renders error/success banners
     Reads request attributes: "error" and "success"
     ========================================================= --%>
<% if (request.getAttribute("error") != null) { %>
  <div class="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-start gap-2" role="alert">
    <span class="shrink-0">⚠️</span>
    <span><%= request.getAttribute("error") %></span>
  </div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
  <div class="mb-4 p-3 rounded-lg bg-green-500/10 border border-green-500/30 text-green-400 text-sm flex items-start gap-2" role="alert">
    <span class="shrink-0">✅</span>
    <span><%= request.getAttribute("success") %></span>
  </div>
<% } %>
