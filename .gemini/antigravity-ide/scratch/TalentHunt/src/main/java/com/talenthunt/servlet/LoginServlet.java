package com.talenthunt.servlet;

import com.talenthunt.exception.AuthenticationException;
import com.talenthunt.exception.ValidationException;
import com.talenthunt.model.User;
import com.talenthunt.service.UserOperations;
import com.talenthunt.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet handling User Authentication (Login).
 * Fulfills Phase 4: Server-Side Integration and Execution (CLO1, CLO2, CLO3).
 * Supports both traditional JSP form submission and modern asynchronous JSON flow.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/api/servlet/login"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserOperations userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If user already logged in, redirect straight to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String portal = request.getParameter("portal"); // "USER" or "ADMIN"

        if (portal == null || portal.trim().isEmpty()) {
            portal = "USER";
        }

        boolean isJsonRequest = "application/json".equalsIgnoreCase(request.getHeader("Accept")) ||
                                "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

        try {
            // Validate credentials and authenticate using Service Layer Interface
            User user = userService.authenticate(email, password, portal);

            // Implement session tracking using HttpSession
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout

            if (isJsonRequest) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format(
                    "{\"success\":true,\"message\":\"Login successful\",\"user\":{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}}",
                    user.getId(), user.getName(), user.getEmail(), user.getRole()
                ));
            } else {
                if (user.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
                }
            }

        } catch (ValidationException | AuthenticationException ex) {
            if (isJsonRequest) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
            } else {
                request.setAttribute("errorMessage", ex.getMessage());
                request.setAttribute("prevEmail", email);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        }
    }
}
