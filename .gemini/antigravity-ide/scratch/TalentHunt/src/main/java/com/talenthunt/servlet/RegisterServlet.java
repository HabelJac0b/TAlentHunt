package com.talenthunt.servlet;

import com.talenthunt.exception.UserAlreadyExistsException;
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
 * Servlet handling User Registration.
 * Fulfills Phase 4: Server-Side Integration and Execution (CLO1, CLO2, CLO3).
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register", "/api/servlet/register"})
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserOperations userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (role == null || role.trim().isEmpty()) {
            role = "CONTESTANT";
        }

        boolean isJsonRequest = "application/json".equalsIgnoreCase(request.getHeader("Accept")) ||
                                "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

        try {
            User newUser = new User(name, email, password, role);
            
            // Server-side validation & registration via UserOperations interface
            User registeredUser = userService.register(newUser);

            // Establish HttpSession session tracking
            HttpSession session = request.getSession(true);
            session.setAttribute("user", registeredUser);
            session.setAttribute("userId", registeredUser.getId());
            session.setAttribute("userName", registeredUser.getName());
            session.setAttribute("userRole", registeredUser.getRole());

            if (isJsonRequest) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format(
                    "{\"success\":true,\"message\":\"Account registered successfully\",\"user\":{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}}",
                    registeredUser.getId(), registeredUser.getName(), registeredUser.getEmail(), registeredUser.getRole()
                ));
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard.jsp?registered=true");
            }

        } catch (ValidationException | UserAlreadyExistsException ex) {
            if (isJsonRequest) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
            } else {
                request.setAttribute("errorMessage", ex.getMessage());
                request.setAttribute("prevName", name);
                request.setAttribute("prevEmail", email);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        }
    }
}
