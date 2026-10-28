package com.talenthunt.servlet;

import com.talenthunt.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to enforce authentication and access control across protected pages.
 * Fulfills Phase 4: Restrict dashboard access without login.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/dashboard.jsp", "/admin-dashboard.jsp", "/dashboard", "/admin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            // Not authenticated: redirect to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        User user = (User) session.getAttribute("user");
        String uri = httpRequest.getRequestURI();

        // Admin-only restriction check
        if (uri.contains("admin") && !user.isAdmin()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard.jsp?error=forbidden");
            return;
        }

        // Proceed to requested protected resource
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
