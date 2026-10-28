<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.talenthunt.model.User" %>
<%@ page import="com.talenthunt.dao.TalentDao" %>
<%@ page import="com.talenthunt.model.Models.TalentSubmission" %>
<%@ page import="com.talenthunt.model.Models.SystemStats" %>
<%@ page import="java.util.List" %>
<%
    // ========================================================
    // Phase 4: Restrict dashboard access without login & Admin Check
    // ========================================================
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("login.jsp?error=unauthorized");
        return;
    }
    if (!currentUser.isAdmin()) {
        response.sendRedirect("dashboard.jsp?error=forbidden");
        return;
    }

    TalentDao dao = new TalentDao();
    SystemStats stats = dao.getSystemStats();
    List<User> users = dao.getUsers();
    List<TalentSubmission> submissions = dao.getSubmissions(null, null, null);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TalentHunt — Administrator Control Panel (JSP & HttpSession)</title>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@600;700;800&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .jsp-dashboard-layout {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 24px;
        }
        .admin-banner {
            background: linear-gradient(135deg, rgba(74, 0, 180, 0.4), rgba(22, 16, 38, 0.9));
            border: 1px solid rgba(168, 85, 247, 0.3);
            border-radius: 20px;
            padding: 32px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
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
<body style="background:#07020d;color:#fff;">

<div class="jsp-dashboard-layout">
    
    <!-- Header -->
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:30px;">
        <div style="display:flex;align-items:center;gap:12px;">
            <div class="login-brand-icon" style="background:linear-gradient(135deg,#7b2ff7,#4a00b4);width:40px;height:40px;font-size:1.2rem;">
                <i class="fa-solid fa-shield-halved"></i>
            </div>
            <h2 style="font-family:'Syne',sans-serif;">Talent<span class="highlight">Hunt</span> Admin Portal</h2>
        </div>
        <div style="display:flex;align-items:center;gap:16px;">
            <a href="dashboard.jsp" class="nav-link" style="color:#d4af37;text-decoration:none;"><i class="fa-solid fa-user"></i> Contestant View</a>
            <a href="logout" class="btn-logout" style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;padding:8px 16px;background:rgba(239,68,68,0.2);border:1px solid rgba(239,68,68,0.4);border-radius:8px;color:#fca5a5;">
                <i class="fa-solid fa-right-from-bracket"></i> Invalidate Session & Logout
            </a>
        </div>
    </div>

    <!-- Admin Profile Banner -->
    <div class="admin-banner">
        <div>
            <span class="nav-role-badge admin">ADMIN PRIVILEGED SESSION</span>
            <h1 style="font-size:2rem;margin:12px 0 6px 0;"><%= currentUser.getName() %></h1>
            <p style="color:var(--text-muted);margin:0;"><%= currentUser.getEmail() %> • Role: <%= currentUser.getRole() %></p>
        </div>
        <div style="text-align:right;">
            <span style="display:inline-block;padding:8px 16px;background:rgba(34,197,94,0.15);border:1px solid rgba(34,197,94,0.3);color:#86efac;border-radius:20px;font-size:0.85rem;">
                <i class="fa-solid fa-shield-check"></i> Dashboard Access Control: Enforced
            </span>
        </div>
    </div>

    <!-- Stats -->
    <div class="stats-grid-jsp">
        <div class="stat-card-jsp">
            <h3 style="color:#a855f7;"><%= stats.totalTalents %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Registered Users</p>
        </div>
        <div class="stat-card-jsp">
            <h3 style="color:#a855f7;"><%= stats.totalSubmissions %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Submissions</p>
        </div>
        <div class="stat-card-jsp">
            <h3 style="color:#a855f7;"><%= stats.totalVotes %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Audience Votes</p>
        </div>
        <div class="stat-card-jsp">
            <h3 style="color:#a855f7;"><%= stats.totalEvaluations %></h3>
            <p style="color:var(--text-muted);font-size:0.9rem;">Judge Evaluations</p>
        </div>
    </div>

    <!-- User Management Table -->
    <div class="table-wrap">
        <h3 style="font-family:'Syne',sans-serif;margin-bottom:16px;"><i class="fa-solid fa-users" style="color:#a855f7;"></i> Registered Users</h3>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                </tr>
            </thead>
            <tbody>
                <% for (User u : users) { %>
                    <tr>
                        <td>#<%= u.getId() %></td>
                        <td><strong><%= u.getName() %></strong></td>
                        <td><%= u.getEmail() %></td>
                        <td><span class="nav-role-badge <%= u.isAdmin() ? "admin" : "" %>"><%= u.getRole() %></span></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <!-- Submissions Approval Table -->
    <div class="table-wrap">
        <h3 style="font-family:'Syne',sans-serif;margin-bottom:16px;"><i class="fa-solid fa-list-check" style="color:#a855f7;"></i> Submissions Moderation Queue</h3>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Contestant</th>
                    <th>Title</th>
                    <th>Category</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% for (TalentSubmission s : submissions) { %>
                    <tr>
                        <td>#<%= s.id %></td>
                        <td><strong><%= s.userName %></strong></td>
                        <td><%= s.title %></td>
                        <td><%= s.categoryName %></td>
                        <td><span class="status-pill <%= s.status != null ? s.status.toLowerCase() : "approved" %>"><%= s.status %></span></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>
