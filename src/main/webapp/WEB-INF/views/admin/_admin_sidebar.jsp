<%-- =========================================================
     _admin_sidebar.jsp  —  shared admin navigation sidebar
     Include with:  <%@ include file="/WEB-INF/views/admin/_admin_sidebar.jsp" %>
     Requires: ctx variable defined by including page
     ========================================================= --%>
<aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40">
  <div class="p-5 border-b border-gray-800">
    <span class="mono text-purple-400 font-bold text-xl">&lt;QP/&gt;</span>
    <span class="font-bold ml-2">Quiz-Portal</span>
    <p class="text-xs text-purple-400 mt-1 font-medium">Admin Panel</p>
  </div>
  <nav class="flex-1 p-4 space-y-1">
    <a href="<%=ctx%>/admin/dashboard"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">📊 Dashboard</a>
    <a href="<%=ctx%>/admin/questions"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">❓ Questions</a>
    <a href="<%=ctx%>/admin/tags"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">🏷️ Tags</a>
    <a href="<%=ctx%>/admin/reports"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">📈 Reports</a>
  </nav>
  <div class="p-4 border-t border-gray-800">
    <a href="<%=ctx%>/logout"
       class="text-sm text-gray-400 hover:text-red-400 px-3 py-2 rounded-lg transition block">🚪 Logout</a>
  </div>
</aside>
