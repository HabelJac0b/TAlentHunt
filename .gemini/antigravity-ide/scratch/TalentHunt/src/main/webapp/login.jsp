<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.talenthunt.model.User" %>
<%
    // If already authenticated, redirect straight to dashboard
    User loggedIn = (User) session.getAttribute("user");
    if (loggedIn != null) {
        if (loggedIn.isAdmin()) {
            response.sendRedirect("admin-dashboard.jsp");
        } else {
            response.sendRedirect("dashboard.jsp");
        }
        return;
    }
    String error = (String) request.getAttribute("errorMessage");
    String urlErr = request.getParameter("error");
    String logoutMsg = request.getParameter("logout");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TalentHunt — Sign In (JSP & Servlets)</title>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .jsp-auth-body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: radial-gradient(circle at 50% 20%, #1a0f2e 0%, #0d0614 70%, #050208 100%);
            padding: 24px;
        }
        .jsp-card {
            background: rgba(22, 16, 38, 0.85);
            border: 1px solid rgba(255, 215, 0, 0.2);
            box-shadow: 0 25px 60px rgba(0,0,0,0.8), 0 0 40px rgba(212, 175, 55, 0.15);
            backdrop-filter: blur(16px);
            border-radius: 20px;
            width: 100%;
            max-width: 440px;
            padding: 36px 32px;
        }
        .banner-badge {
            display: inline-block;
            background: rgba(255, 215, 0, 0.15);
            color: #d4af37;
            border: 1px solid rgba(255, 215, 0, 0.3);
            border-radius: 20px;
            padding: 4px 12px;
            font-size: 0.75rem;
            font-weight: 600;
            margin-bottom: 16px;
        }
        .alert-box {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.4);
            color: #fca5a5;
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 0.88rem;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .success-box {
            background: rgba(34, 197, 94, 0.15);
            border: 1px solid rgba(34, 197, 94, 0.4);
            color: #86efac;
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 0.88rem;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .input-error {
            border-color: #ef4444 !important;
            box-shadow: 0 0 10px rgba(239, 68, 68, 0.25) !important;
        }
        .field-err-text {
            color: #f87171;
            font-size: 0.75rem;
            margin-top: 4px;
            display: none;
        }
    </style>
</head>
<body class="jsp-auth-body">

<div class="jsp-card">
    <div style="text-align: center;">
        <div class="login-brand-icon" style="margin: 0 auto 12px;"><i class="fa-solid fa-trophy"></i></div>
        <h2 style="font-family:'Syne',sans-serif;font-size:1.8rem;margin-bottom:4px;">Talent<span class="highlight">Hunt</span></h2>
        <p style="color:var(--text-muted);font-size:0.88rem;margin-bottom:24px;">Sign in to access your talent dashboard</p>
    </div>

    <% if (error != null) { %>
        <div class="alert-box">
            <i class="fa-solid fa-triangle-exclamation"></i>
            <span><%= error %></span>
        </div>
    <% } else if ("unauthorized".equals(urlErr)) { %>
        <div class="alert-box">
            <i class="fa-solid fa-lock"></i>
            <span>Session expired or login required to view dashboard.</span>
        </div>
    <% } else if ("true".equals(logoutMsg)) { %>
        <div class="success-box">
            <i class="fa-solid fa-check"></i>
            <span>Logged out successfully!</span>
        </div>
    <% } %>

    <!-- Tabs -->
    <div class="auth-tabs" style="margin-bottom:20px;">
        <button type="button" class="auth-tab active" id="tabUser" onclick="setPortal('USER')">
            <i class="fa-solid fa-user"></i> Contestant
        </button>
        <button type="button" class="auth-tab" id="tabAdmin" onclick="setPortal('ADMIN')">
            <i class="fa-solid fa-shield-halved"></i> Admin
        </button>
    </div>

    <!-- Login Form processed by LoginServlet via POST -->
    <form action="login" method="POST" id="jspLoginForm" novalidate>
        <input type="hidden" name="portal" id="portalField" value="USER">

        <div class="form-group">
            <label for="emailInput">Email Address / Username</label>
            <div class="input-wrap">
                <i class="fa-regular fa-envelope"></i>
                <input type="text" name="email" id="emailInput" placeholder="alex@talenthunt.com"
                       value="<%= request.getAttribute("prevEmail") != null ? request.getAttribute("prevEmail") : "" %>" required>
            </div>
            <span class="field-err-text" id="emailErr">Please enter a valid email or username</span>
        </div>

        <div class="form-group">
            <label for="passwordInput">Password</label>
            <div class="input-wrap">
                <i class="fa-solid fa-lock"></i>
                <input type="password" name="password" id="passwordInput" placeholder="••••••••" required>
            </div>
            <span class="field-err-text" id="passErr">Password must be at least 6 characters</span>
        </div>

        <button type="submit" class="btn-login" id="submitBtn" style="margin-top: 10px;">
            <i class="fa-solid fa-arrow-right-to-bracket"></i> Sign In as Contestant
        </button>

        <p class="login-hint" style="margin-top:20px;text-align:center;">
            New participant? <a href="register.jsp" style="color:var(--gold);font-weight:600;text-decoration:none;">Create account</a>
        </p>

        <p class="demo-hint" style="margin-top:12px;text-align:center;font-size:0.8rem;color:var(--text-muted);">
            Demo: <span style="cursor:pointer;color:var(--gold);" onclick="fillDemo('alex@talenthunt.com', 'user123', 'USER')">alex@talenthunt.com / user123</span> |
            <span style="cursor:pointer;color:var(--crimson-l);" onclick="fillDemo('admin@talenthunt.com', 'admin123', 'ADMIN')">Admin</span>
        </p>
    </form>
</div>

<script>
    function setPortal(portal) {
        document.getElementById('portalField').value = portal;
        document.getElementById('tabUser').classList.toggle('active', portal === 'USER');
        document.getElementById('tabAdmin').classList.toggle('active', portal === 'ADMIN');
        const submitBtn = document.getElementById('submitBtn');
        if (portal === 'ADMIN') {
            submitBtn.style.background = 'linear-gradient(135deg, #7b2ff7, #4a00b4)';
            submitBtn.innerHTML = '<i class="fa-solid fa-shield-halved"></i> Admin Sign In';
        } else {
            submitBtn.style.background = '';
            submitBtn.innerHTML = '<i class="fa-solid fa-arrow-right-to-bracket"></i> Sign In with HttpSession';
        }
    }

    function fillDemo(e, p, portal) {
        setPortal(portal);
        document.getElementById('emailInput').value = e;
        document.getElementById('passwordInput').value = p;
    }

    // Client-side JavaScript validation (Phase 3: empty fields, format checks)
    document.getElementById('jspLoginForm').addEventListener('submit', function(e) {
        let valid = true;
        const emailEl = document.getElementById('emailInput');
        const passEl = document.getElementById('passwordInput');
        const emailErr = document.getElementById('emailErr');
        const passErr = document.getElementById('passErr');

        // Reset
        emailEl.classList.remove('input-error');
        passEl.classList.remove('input-error');
        emailErr.style.display = 'none';
        passErr.style.display = 'none';

        const emailVal = emailEl.value.trim();
        const passVal = passEl.value;

        if (!emailVal) {
            emailEl.classList.add('input-error');
            emailErr.textContent = 'Email or username is required.';
            emailErr.style.display = 'block';
            valid = false;
        }

        if (!passVal || passVal.length < 6) {
            passEl.classList.add('input-error');
            passErr.textContent = 'Password must be at least 6 characters.';
            passErr.style.display = 'block';
            valid = false;
        }

        if (!valid) {
            e.preventDefault();
        }
    });
</script>

</body>
</html>
