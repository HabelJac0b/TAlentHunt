<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.talenthunt.model.User" %>
<%
    User loggedIn = (User) session.getAttribute("user");
    if (loggedIn != null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    String error = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TalentHunt — Register (JSP & Servlets)</title>
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
            max-width: 460px;
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
        <h2 style="font-family:'Syne',sans-serif;font-size:1.8rem;margin-bottom:4px;">Join Talent<span class="highlight">Hunt</span></h2>
        <p style="color:var(--text-muted);font-size:0.88rem;margin-bottom:24px;">Create your contestant profile</p>
    </div>

    <% if (error != null) { %>
        <div class="alert-box">
            <i class="fa-solid fa-triangle-exclamation"></i>
            <span><%= error %></span>
        </div>
    <% } %>

    <!-- Form processed by RegisterServlet via POST -->
    <form action="register" method="POST" id="jspRegisterForm" novalidate>
        <div class="form-group">
            <label for="nameInput">Full Name</label>
            <div class="input-wrap">
                <i class="fa-solid fa-user"></i>
                <input type="text" name="name" id="nameInput" placeholder="e.g. Alex Johnson"
                       value="<%= request.getAttribute("prevName") != null ? request.getAttribute("prevName") : "" %>" required>
            </div>
            <span class="field-err-text" id="nameErr">Name must be at least 2 characters</span>
        </div>

        <div class="form-group">
            <label for="emailInput">Email Address</label>
            <div class="input-wrap">
                <i class="fa-regular fa-envelope"></i>
                <input type="email" name="email" id="emailInput" placeholder="your@email.com"
                       value="<%= request.getAttribute("prevEmail") != null ? request.getAttribute("prevEmail") : "" %>" required>
            </div>
            <span class="field-err-text" id="emailErr">Please enter a valid email address</span>
        </div>

        <div class="form-group">
            <label for="passwordInput">Password (min 6 characters)</label>
            <div class="input-wrap">
                <i class="fa-solid fa-lock"></i>
                <input type="password" name="password" id="passwordInput" placeholder="••••••••" required>
            </div>
            <span class="field-err-text" id="passErr">Password must contain at least 6 characters</span>
        </div>

        <div class="form-group">
            <label for="roleInput">Account Role</label>
            <div class="input-wrap">
                <i class="fa-solid fa-tag"></i>
                <select name="role" id="roleInput" style="width:100%;background:transparent;border:none;color:#fff;padding:12px;outline:none;">
                    <option value="CONTESTANT" style="background:#161026;">Contestant</option>
                    <option value="AUDIENCE" style="background:#161026;">Audience</option>
                </select>
            </div>
        </div>

        <button type="submit" class="btn-login" style="margin-top: 10px;">
            <i class="fa-solid fa-user-plus"></i> Create Account
        </button>

        <p class="login-hint" style="margin-top:20px;text-align:center;">
            Already have an account? <a href="login.jsp" style="color:var(--gold);font-weight:600;text-decoration:none;">Sign in here</a>
        </p>
    </form>
</div>

<script>
    // Client-side JavaScript validation (Phase 3: empty fields, format checks)
    document.getElementById('jspRegisterForm').addEventListener('submit', function(e) {
        let valid = true;
        const nameEl = document.getElementById('nameInput');
        const emailEl = document.getElementById('emailInput');
        const passEl = document.getElementById('passwordInput');

        const nameErr = document.getElementById('nameErr');
        const emailErr = document.getElementById('emailErr');
        const passErr = document.getElementById('passErr');

        // Reset
        [nameEl, emailEl, passEl].forEach(el => el.classList.remove('input-error'));
        [nameErr, emailErr, passErr].forEach(el => el.style.display = 'none');

        const nameVal = nameEl.value.trim();
        const emailVal = emailEl.value.trim();
        const passVal = passEl.value;

        // Name validation
        if (!nameVal || nameVal.length < 2) {
            nameEl.classList.add('input-error');
            nameErr.textContent = 'Name is required (at least 2 characters).';
            nameErr.style.display = 'block';
            valid = false;
        }

        // Email regex format check
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailVal || !emailPattern.test(emailVal)) {
            emailEl.classList.add('input-error');
            emailErr.textContent = 'Enter a valid email address (e.g., name@domain.com).';
            emailErr.style.display = 'block';
            valid = false;
        }

        // Password length check
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
