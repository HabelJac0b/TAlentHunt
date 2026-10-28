package com.talenthunt.servlet;

import com.talenthunt.dao.TalentDao;
import com.talenthunt.model.Models.Competition;
import com.talenthunt.model.Models.SystemStats;
import com.talenthunt.model.Models.TalentSubmission;
import com.talenthunt.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet handling protected User and Admin Dashboards.
 * Fulfills Phase 4: Restrict dashboard access without login.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/admin-dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TalentDao talentDao;

    @Override
    public void init() throws ServletException {
        this.talentDao = new TalentDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Session tracking check - Restrict access without login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String uri = request.getRequestURI();

        // Admin dashboard security gate
        if (uri.contains("admin-dashboard") && !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp?error=forbidden");
            return;
        }

        // 2. Fetch context data for dashboard display
        SystemStats stats = talentDao.getSystemStats();
        List<Competition> competitions = talentDao.getCompetitions();
        List<TalentSubmission> submissions = talentDao.getSubmissions(null, null, null);

        request.setAttribute("stats", stats);
        request.setAttribute("competitions", competitions);
        request.setAttribute("submissions", submissions);

        if (currentUser.isAdmin() || uri.contains("admin-dashboard")) {
            request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
