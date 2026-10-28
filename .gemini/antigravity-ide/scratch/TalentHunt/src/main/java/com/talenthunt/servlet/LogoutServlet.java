package com.talenthunt.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet handling User Logout and Session Invalidation.
 * Fulfills Phase 4: Implement logout and session invalidation.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout", "/api/servlet/logout"})
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
            session.removeAttribute("userId");
            session.removeAttribute("userName");
            session.removeAttribute("userRole");
            session.invalidate(); // Invalidate existing session completely
        }

        boolean isJsonRequest = "application/json".equalsIgnoreCase(request.getHeader("Accept")) ||
                                "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

        if (isJsonRequest) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"message\":\"Logged out successfully.\"}");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp?logout=true");
        }
    }
}
