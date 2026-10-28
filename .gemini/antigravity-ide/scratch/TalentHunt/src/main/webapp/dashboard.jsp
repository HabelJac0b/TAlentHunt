<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.talenthunt.model.User" %>
<%@ page import="com.talenthunt.dao.TalentDao" %>
<%@ page import="com.talenthunt.model.Models.TalentSubmission" %>
<%@ page import="com.talenthunt.model.Models.Competition" %>
<%@ page import="com.talenthunt.model.Models.SystemStats" %>
<%@ page import="com.talenthunt.util.JsonUtil" %>
<%@ page import="java.util.List" %>
<%
    // ========================================================
    // Phase 4: Restrict dashboard access without login
    // ========================================================
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login.jsp?error=unauthorized");
        return;
    }

    TalentDao dao = new TalentDao();
    SystemStats stats = dao.getSystemStats();
    List<Competition> competitions = dao.getCompetitions();
    List<TalentSubmission> submissions = dao.getSubmissions(null, null, null);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TalentHunt — User Dashboard (JSP & HttpSession)</title>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@600;700;800&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .jsp-dashboard-layout {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 24px;
        }
        .user-welcome-card {
            background: linear-gradient(135deg, rgba(30, 20, 50, 0.9), rgba(15, 8, 25, 0.95));
            border: 1px solid rgba(255, 215, 0, 0.25);
            border-radius: 20px;
            padding: 32px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.5);
        }
        .user-profile-meta {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        .user-avatar-circle {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            background: linear-gradient(135deg, #d4af37, #f59e0b);
            color: #0d0614;
            font-size: 1.8rem;
            font-weight: 800;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 0 20px rgba(212, 175, 55, 0.4);
        }
        .stats-grid-jsp {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 36px;
        }
        .stat-card-jsp {
            background: rgba(22, 16, 38, 0.7);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 16px;
            padding: 24px;
            backdrop-filter: blur(10px);
        }
        .stat-card-jsp h3 {
            font-size: 2rem;
            color: var(--gold);
            margin-bottom: 6px;
        }
        .session-info-badge {
            background: rgba(34, 197, 94, 0.15);
            border: 1px solid rgba(34, 197, 94, 0.3);
            color: #86efac;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 0.8rem;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        .table-wrap {
            background: rgba(22, 16, 38, 0.7);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 16px;
            padding: 24px;
            margin-bottom: 32px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 14px 16px;
            text-align: left;
            border-bottom: 1px solid rgba(255, 255, 255, 0.06);
        }
        th {
            color: var(--text-muted);
            font-size: 0.85rem;
            text-transform: uppercase;
        }
    </style>
</head>
<body style="background:#09040e;color:#fff;">

<div class="jsp-dashboard-layout">
    
    <!-- Top Navigation Header -->
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:30px;">
        <div style="display:flex;align-items:center;gap:12px;">
            <div class="login-brand-icon" style="width:40px;height:40px;font-size:1.2rem;"><i class="fa-solid fa-trophy"></i></div>
            <h2 style="font-family:'Syne',sans-serif;">Talent<span class="highlight">Hunt</span> <span style="font-size:0.9rem;color:var(--text-muted);font-weight:400;">(JSP Portal)</span></h2>
        </div>
        <div style="display:flex;align-items:center;gap:16px;">
            <a href="index.html" class="nav-link" style="color:#d4af37;text-decoration:none;"><i class="fa-solid fa-globe"></i> View Web SPA</a>
            <!-- Logout invoking LogoutServlet (session invalidation) -->
            <a href="logout" class="btn-logout" style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;padding:8px 16px;background:rgba(239,68,68,0.2);border:1px solid rgba(239,68,68,0.4);border-radius:8px;color:#fca5a5;">
                <i class="fa-solid fa-right-from-bracket"></i> Invalidate Session & Logout
            </a>
        </div>
    </div>

    <!-- User Profile Banner -->
    <div class="user-welcome-card">
        <div class="user-profile-meta">
            <div class="user-avatar-circle">
                <%= currentUser.getName() != null && !currentUser.getName().isEmpty() ? currentUser.getName().substring(0, 1).toUpperCase() : "U" %>
            </div>
            <div>
                <div style="display:flex;align-items:center;gap:12px;margin-bottom:6px;">
                    <h1 style="font-size:1.8rem;margin:0;"><%= currentUser.getName() %></h1>
                    <span class="nav-role-badge <%= currentUser.isAdmin() ? "admin" : "" %>"><%= currentUser.getRole() %></span>
                </div>
                <p style="color:var(--text-muted);font-size:0.9rem;margin:0 0 8px 0;"><i class="fa-regular fa-envelope"></i> <%= currentUser.getEmail() %></p>
                <div class="session-info-badge">
                    <i class="fa-solid fa-circle-dot"></i>
                    HttpSession Active (Session ID: <%= session.getId().substring(0, Math.min(10, session.getId().length())) %>...)
                </div>
            </div>
        </div>
        <% if (currentUser.isAdmin()) { %>
            <div>
                <a href="admin-dashboard.jsp" style="display:inline-block;padding:12px 20px;background:linear-gradient(135deg,#7b2ff7,#4a00b4);color:#fff;text-decoration:none;border-radius:10px;font-weight:600;">
                    <i class="fa-solid fa-gauge"></i> Switch to Admin Panel
                </a>
            </div>
        <% } %>
    </div>

    <!-- Phase 3: JSON Representation of User Data (CLO 2) -->
    <div class="table-wrap" style="border-left: 4px solid var(--gold); margin-bottom: 32px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
            <h3 style="margin:0; display:flex; align-items:center; gap:8px; font-family:'Syne',sans-serif;">
                <i class="fa-solid fa-code" style="color:var(--gold);"></i>
                User Data Format (JSON Representation)
            </h3>
            <span style="font-size:0.8rem; background:rgba(212,175,55,0.15); color:var(--gold); padding:4px 10px; border-radius:12px; border:1px solid rgba(212,175,55,0.3); font-weight:600;">
                Phase 3: JSON Integration (CLO 2)
            </span>
        </div>
        <p style="color:var(--text-muted); font-size:0.85rem; margin-bottom:14px;">
            Serialized using <code>com.talenthunt.util.JsonUtil.userToJson(currentUser)</code> for lightweight asynchronous data exchange and REST APIs:
        </p>
        <pre style="background:rgba(10,5,18,0.9); padding:16px; border-radius:10px; border:1px solid rgba(255,255,255,0.1); color:#38bdf8; font-family:monospace; font-size:0.88rem; overflow-x:auto; margin:0;"><%= JsonUtil.prettyPrint(currentUser.toJson()) %></pre>
    </div>

    <!-- Live Telemetry Stats -->
    <div class="stats-grid-jsp">
        <div class="stat-card-jsp">
            <h3><%= stats.totalTalents %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Registered Talents</p>
        </div>
        <div class="stat-card-jsp">
            <h3><%= stats.totalCompetitions %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Active Competitions</p>
        </div>
        <div class="stat-card-jsp">
            <h3><%= stats.totalVotes %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Audience Votes</p>
        </div>
        <div class="stat-card-jsp">
            <h3><%= stats.totalSubmissions %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Total Submissions</p>
        </div>
    </div>

    <!-- Active Competitions Section -->
    <div class="table-wrap">
        <h3 style="font-family:'Syne',sans-serif;margin-bottom:16px;"><i class="fa-solid fa-medal" style="color:var(--gold);"></i> Open Competitions</h3>
        <table>
            <thead>
                <tr>
                    <th>Competition</th>
                    <th>Category</th>
                    <th>Prize</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% for (Competition c : competitions) { %>
                    <tr>
                        <td><strong><%= c.title %></strong></td>
                        <td><%= c.categoryName != null ? c.categoryName : "Open" %></td>
                        <td style="color:var(--gold);"><%= c.prize != null ? c.prize : "Trophy" %></td>
                        <td><span class="status-pill open"><%= c.status %></span></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <!-- Talent Showcase Submissions -->
    <div class="table-wrap">
        <h3 style="font-family:'Syne',sans-serif;margin-bottom:16px;"><i class="fa-solid fa-star" style="color:var(--gold);"></i> Recent Talent Submissions</h3>
        <table>
            <thead>
                <tr>
                    <th>Performer</th>
                    <th>Performance Title</th>
                    <th>Category</th>
                    <th>Votes</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% for (TalentSubmission s : submissions) { %>
                    <tr>
                        <td><strong><%= s.userName %></strong></td>
                        <td><%= s.title %></td>
                        <td><%= s.categoryName %></td>
                        <td style="color:var(--gold);font-weight:700;"><%= s.votesCount %></td>
                        <td><span class="status-pill <%= s.status != null ? s.status.toLowerCase() : "approved" %>"><%= s.status %></span></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>
