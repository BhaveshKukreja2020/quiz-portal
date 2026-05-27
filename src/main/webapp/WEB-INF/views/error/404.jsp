<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<% String ctx=request.getContextPath(); String pageTitle="404 Not Found"; %>
<!DOCTYPE html><html lang="en"><head><%@ include file="/WEB-INF/views/_layout.jsp" %>
<style>.err-wrap{min-height:100vh;display:flex;align-items:center;justify-content:center;text-align:center;padding:2rem;}</style>
</head><body>
<div class="err-wrap"><div>
  <p class="mono" style="font-size:5rem;font-weight:700;color:var(--primary);line-height:1">404</p>
  <h1 style="font-size:1.4rem;font-weight:700;margin:.75rem 0 .4rem">Page Not Found</h1>
  <p style="color:var(--muted);font-size:.85rem;margin-bottom:1.5rem">The page you're looking for doesn't exist or has been moved.</p>
  <div style="display:flex;gap:.75rem;justify-content:center">
    <a href="<%=ctx%>/login" class="btn-primary-cq" style="padding:.55rem 1.25rem">Go Home</a>
    <button onclick="history.back()" class="btn-ghost" style="padding:.55rem 1.25rem">Go Back</button>
  </div>
</div></div>
</body></html>
