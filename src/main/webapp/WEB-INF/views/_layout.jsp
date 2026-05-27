<%-- Shared HTML <head> fragment. Include at top of every page.
     Required scriptlet before include:
       String pageTitle = "My Page";
--%>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title><%= pageTitle != null ? pageTitle + " – Quiz-Portal" : "Quiz-Portal" %></title>
<!-- Bootstrap 5.3 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
<!-- Bootstrap Icons 1.11 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"/>
<!-- Google Fonts -->
<link rel="preconnect" href="https://fonts.googleapis.com"/>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet"/>
<style>
/* ── Design tokens ────────────────────────────────────────── */
:root{
  --bg:         #0f1117;
  --surface:    #1a1d27;
  --surface2:   #21253a;
  --border:     #2a2e45;
  --text:       #e2e5f1;
  --muted:      #6b7280;
  --primary:    #4f63d2;
  --primary-lt: rgba(79,99,210,.15);
  --success:    #22c55e;
  --danger:     #ef4444;
  --warning:    #f59e0b;
  --info:       #06b6d4;
  --sidebar-w:  230px;
  --radius:     .625rem;
  --shadow:     0 1px 3px rgba(0,0,0,.4);
}
/* ── Base ─────────────────────────────────────────────────── */
*{box-sizing:border-box;margin:0;padding:0;}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;font-size:.9rem;}
.mono{font-family:'JetBrains Mono',monospace;}
a{color:inherit;text-decoration:none;}
/* ── Sidebar ──────────────────────────────────────────────── */
.app-sidebar{
  width:var(--sidebar-w);background:var(--surface);border-right:1px solid var(--border);
  position:fixed;top:0;left:0;height:100vh;display:flex;flex-direction:column;z-index:100;
  overflow-y:auto;
}
.app-main{margin-left:var(--sidebar-w);padding:2rem;min-height:100vh;}
.brand{padding:1.25rem 1.5rem;border-bottom:1px solid var(--border);font-weight:700;font-size:1.05rem;letter-spacing:-.01em;}
.brand .dot{color:var(--primary);}
.nav-section{padding:.75rem 1rem .25rem;font-size:.65rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em;color:var(--muted);}
.nav-item{display:flex;align-items:center;gap:.6rem;padding:.5rem 1rem;font-size:.8rem;font-weight:500;color:var(--muted);
  border-radius:var(--radius);margin:.1rem .5rem;transition:all .15s;cursor:pointer;}
.nav-item:hover{background:var(--surface2);color:var(--text);}
.nav-item.active{background:var(--primary-lt);color:var(--primary);font-weight:600;}
.nav-item i{font-size:.95rem;width:1.2rem;flex-shrink:0;}
.sidebar-footer{margin-top:auto;padding:.75rem;border-top:1px solid var(--border);}
/* ── Cards ────────────────────────────────────────────────── */
.cq-card{background:var(--surface);border:1px solid var(--border);border-radius:var(--radius);box-shadow:var(--shadow);}
.cq-card-header{padding:1rem 1.25rem;border-bottom:1px solid var(--border);font-weight:600;font-size:.875rem;display:flex;align-items:center;justify-content:space-between;}
.cq-card-body{padding:1.25rem;}
/* ── Stat cards ───────────────────────────────────────────── */
.stat-card{background:var(--surface);border:1px solid var(--border);border-radius:var(--radius);padding:1.25rem 1.5rem;}
.stat-card .stat-val{font-size:1.75rem;font-weight:700;font-family:'JetBrains Mono',monospace;line-height:1.1;}
.stat-card .stat-lbl{font-size:.7rem;color:var(--muted);margin-top:.2rem;text-transform:uppercase;letter-spacing:.04em;}
.stat-card .stat-icon{width:2.5rem;height:2.5rem;border-radius:.5rem;display:flex;align-items:center;justify-content:center;font-size:1.1rem;}
/* ── Buttons ──────────────────────────────────────────────── */
.btn-primary-cq{background:var(--primary);color:#fff;border:none;padding:.5rem 1.25rem;border-radius:var(--radius);font-size:.8rem;font-weight:600;cursor:pointer;transition:opacity .15s;}
.btn-primary-cq:hover{opacity:.88;}
.btn-ghost{background:transparent;color:var(--muted);border:1px solid var(--border);padding:.45rem 1rem;border-radius:var(--radius);font-size:.8rem;font-weight:500;cursor:pointer;transition:all .15s;}
.btn-ghost:hover{border-color:var(--text);color:var(--text);}
.btn-danger-cq{background:var(--danger);color:#fff;border:none;padding:.45rem 1rem;border-radius:var(--radius);font-size:.8rem;font-weight:600;cursor:pointer;}
/* ── Tables ───────────────────────────────────────────────── */
.cq-table{width:100%;font-size:.8rem;border-collapse:collapse;}
.cq-table th{text-transform:uppercase;font-size:.65rem;letter-spacing:.06em;color:var(--muted);font-weight:600;padding:.625rem 1rem;border-bottom:1px solid var(--border);text-align:left;background:var(--surface);}
.cq-table td{padding:.625rem 1rem;border-bottom:1px solid var(--border);vertical-align:middle;}
.cq-table tr:last-child td{border-bottom:none;}
.cq-table tbody tr:hover{background:rgba(255,255,255,.025);}
/* ── Badges ───────────────────────────────────────────────── */
.badge-easy{background:rgba(34,197,94,.15);color:#4ade80;border:1px solid rgba(34,197,94,.25);padding:.2rem .5rem;border-radius:.375rem;font-size:.68rem;font-weight:600;}
.badge-medium{background:rgba(245,158,11,.15);color:#fbbf24;border:1px solid rgba(245,158,11,.25);padding:.2rem .5rem;border-radius:.375rem;font-size:.68rem;font-weight:600;}
.badge-hard{background:rgba(239,68,68,.15);color:#f87171;border:1px solid rgba(239,68,68,.25);padding:.2rem .5rem;border-radius:.375rem;font-size:.68rem;font-weight:600;}
.badge-pass{background:rgba(34,197,94,.15);color:#4ade80;border:1px solid rgba(34,197,94,.25);padding:.2rem .6rem;border-radius:.375rem;font-size:.68rem;font-weight:700;}
.badge-fail{background:rgba(239,68,68,.15);color:#f87171;border:1px solid rgba(239,68,68,.25);padding:.2rem .6rem;border-radius:.375rem;font-size:.68rem;font-weight:700;}
/* ── Forms ────────────────────────────────────────────────── */
.cq-input{background:var(--surface2);border:1px solid var(--border);color:var(--text);border-radius:var(--radius);padding:.55rem .875rem;font-size:.85rem;width:100%;transition:border-color .15s;outline:none;}
.cq-input:focus{border-color:var(--primary);}
.cq-label{font-size:.78rem;font-weight:600;color:var(--muted);margin-bottom:.35rem;display:block;text-transform:uppercase;letter-spacing:.04em;}
.cq-select{background:var(--surface2);border:1px solid var(--border);color:var(--text);border-radius:var(--radius);padding:.55rem .875rem;font-size:.85rem;width:100%;outline:none;}
.cq-select:focus{border-color:var(--primary);}
.cq-textarea{background:var(--surface2);border:1px solid var(--border);color:var(--text);border-radius:var(--radius);padding:.55rem .875rem;font-size:.85rem;width:100%;resize:vertical;outline:none;}
.cq-textarea:focus{border-color:var(--primary);}
/* ── Alerts ───────────────────────────────────────────────── */
.cq-alert{padding:.75rem 1rem;border-radius:var(--radius);font-size:.82rem;display:flex;align-items:flex-start;gap:.5rem;margin-bottom:1rem;}
.cq-alert-error{background:rgba(239,68,68,.1);border:1px solid rgba(239,68,68,.25);color:#fca5a5;}
.cq-alert-success{background:rgba(34,197,94,.1);border:1px solid rgba(34,197,94,.25);color:#86efac;}
.cq-alert-warning{background:rgba(245,158,11,.1);border:1px solid rgba(245,158,11,.25);color:#fde68a;}
/* ── Progress bar ─────────────────────────────────────────── */
.cq-progress{background:var(--surface2);border-radius:99px;height:6px;overflow:hidden;}
.cq-progress-bar{height:100%;border-radius:99px;transition:width .6s ease;}
/* ── Avatar ───────────────────────────────────────────────── */
.avatar{width:2.25rem;height:2.25rem;border-radius:50%;background:linear-gradient(135deg,var(--primary),#7c3aed);display:flex;align-items:center;justify-content:center;font-weight:700;font-size:.8rem;color:#fff;flex-shrink:0;}
/* ── Page header ──────────────────────────────────────────── */
.page-header{margin-bottom:1.5rem;}
.page-header h1{font-size:1.4rem;font-weight:700;margin-bottom:.2rem;}
.page-header p{color:var(--muted);font-size:.82rem;}
/* ── Mobile ───────────────────────────────────────────────── */
@media(max-width:768px){
  .app-sidebar{transform:translateX(-100%);transition:transform .25s;}
  .app-sidebar.open{transform:translateX(0);}
  .app-main{margin-left:0;padding:1rem;}
}
</style>
