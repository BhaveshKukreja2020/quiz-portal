<%-- =========================================================
     _student_sidebar.jsp  —  shared student navigation sidebar
     Requires: ctx and u (User) variables defined by including page
     ========================================================= --%>
<aside class="sidebar bg-gray-900 border-r border-gray-800 flex flex-col fixed h-full z-40">
  <div class="p-5 border-b border-gray-800">
    <span class="mono text-cyan-400 font-bold text-xl">&lt;QP/&gt;</span>
    <span class="font-bold ml-2">Quiz-Portal</span>
  </div>
  <div class="p-4 border-b border-gray-800">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-full bg-gradient-to-br from-cyan-500 to-purple-500
                  flex items-center justify-center font-bold text-white text-sm">
        <%= u != null && u.getName() != null && !u.getName().isEmpty()
              ? u.getName().substring(0,1).toUpperCase() : "?" %>
      </div>
      <div>
        <p class="font-semibold text-sm"><%= u != null ? u.getName() : "Student" %></p>
        <p class="text-xs text-gray-500">Student</p>
      </div>
    </div>
  </div>
  <nav class="flex-1 p-4 space-y-1">
    <a href="<%=ctx%>/student/dashboard"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">📊 Dashboard</a>
    <a href="<%=ctx%>/quiz/tags"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">🎯 Take a Quiz</a>
    <a href="<%=ctx%>/quiz/history"
       class="nav-link flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400">📋 My Attempts</a>
  </nav>
  <div class="p-4 border-t border-gray-800">
    <a href="<%=ctx%>/logout"
       class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-gray-400 hover:text-red-400 transition">🚪 Logout</a>
  </div>
</aside>
