<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); String pageTitle = "Register"; %>
<!DOCTYPE html><html lang="en"><head>
<%@ include file="/WEB-INF/views/_layout.jsp" %>
<style>
.auth-wrap{min-height:100vh;display:flex;align-items:center;justify-content:center;padding:2rem;}
.auth-box{width:100%;max-width:420px;}
.auth-logo{font-size:1.5rem;font-weight:700;margin-bottom:2rem;text-align:center;}
.auth-logo span{color:var(--primary);}
.auth-card{background:var(--surface);border:1px solid var(--border);border-radius:1rem;padding:2rem;}
.auth-title{font-size:1.15rem;font-weight:700;margin-bottom:.4rem;}
.auth-sub{color:var(--muted);font-size:.82rem;margin-bottom:1.5rem;}
.form-group{margin-bottom:1rem;}
.input-icon-wrap{position:relative;}
.input-icon-wrap i{position:absolute;left:.75rem;top:50%;transform:translateY(-50%);color:var(--muted);font-size:.85rem;}
.input-icon-wrap input{padding-left:2.25rem;}
.auth-footer{text-align:center;margin-top:1.25rem;font-size:.8rem;color:var(--muted);}
.auth-footer a{color:var(--primary);font-weight:600;}
</style></head>
<body>
<div class="auth-wrap">
  <div class="auth-box">
    <div class="auth-logo">Quiz-Portal<span></span></div>
    <div class="auth-card">
      <h1 class="auth-title">Create account</h1>
      <p class="auth-sub">Join CodeQuiz and start testing your knowledge</p>
      <% if(request.getAttribute("error")!=null){ %>
      <div class="cq-alert cq-alert-error"><i class="bi bi-exclamation-circle"></i><span><%=request.getAttribute("error")%></span></div>
      <% } %>
      <form method="post" action="<%=ctx%>/register" novalidate>
        <div class="form-group">
          <label class="cq-label">Full name</label>
          <div class="input-icon-wrap">
            <i class="bi bi-person"></i>
            <input type="text" name="name" class="cq-input" placeholder="Your full name" required autocomplete="name"/>
          </div>
        </div>
        <div class="form-group">
          <label class="cq-label">Email address</label>
          <div class="input-icon-wrap">
            <i class="bi bi-envelope"></i>
            <input type="email" name="email" class="cq-input" placeholder="you@example.com" required autocomplete="email"/>
          </div>
        </div>
        <div class="form-group">
          <label class="cq-label">Password <span style="color:var(--muted);font-weight:400">(min 6 chars)</span></label>
          <div class="input-icon-wrap">
            <i class="bi bi-lock"></i>
            <input type="password" name="password" class="cq-input" placeholder="••••••••" required minlength="6" autocomplete="new-password"/>
          </div>
        </div>
        <button type="submit" class="btn-primary-cq w-100 mt-2" style="padding:.65rem">Create Account</button>
      </form>
      <div class="auth-footer">Already have an account? <a href="<%=ctx%>/login">Sign in</a></div>
    </div>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body></html>
