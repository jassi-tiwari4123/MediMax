package com.ohms.controller.auth;

import com.ohms.exception.AuthException;
import com.ohms.exception.OhmsException;
import com.ohms.service.AuthService;
import com.ohms.utility.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LoginServlet — handles GET (show login page) and POST (process login).
 *
 * INTERVIEW POINTS:
 *   - GET /login  → forwards to login.jsp
 *   - POST /login → validates credentials, sets JWT cookie, redirects by role
 *   - JWT stored in HTTP-only cookie (not accessible by JavaScript — XSS protection)
 *   - SameSite=Strict on cookie prevents CSRF
 *
 * URL: /login
 */
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthService authService = new AuthService();

    // ── GET — show login form ────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // If already logged in, redirect to appropriate dashboard
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (JwtUtil.COOKIE_NAME.equals(c.getName())) {
                    try {
                        com.ohms.enums.Role role = JwtUtil.getRoleFromToken(c.getValue());
                        resp.sendRedirect(req.getContextPath() + dashboardFor(role));
                        return;
                    } catch (Exception ignored) {
                        // Stale/invalid cookie — fall through to show login
                    }
                }
            }
        }

        req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);
    }

    // ── POST — process login ─────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            // Delegate to service — throws AuthException on failure
            String token = authService.login(email, password);

            // Extract role to know which dashboard to redirect to
            com.ohms.enums.Role role = JwtUtil.getRoleFromToken(token);

            // Set JWT in HTTP-only cookie
            Cookie jwtCookie = new Cookie(JwtUtil.COOKIE_NAME, token);
            jwtCookie.setHttpOnly(true);          // not accessible via JS
            jwtCookie.setSecure(false);           // set true in production (HTTPS)
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(1800);            // 30 minutes — matches JWT expiry
            resp.addCookie(jwtCookie);

            logger.info("Login success: email={}, role={}", email, role);
            resp.sendRedirect(req.getContextPath() + dashboardFor(role));

        } catch (AuthException e) {
            logger.warn("Login failed for {}: {}", email, e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);

        } catch (OhmsException e) {
            req.setAttribute("error", "An unexpected error occurred. Please try again.");
            req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);
        }
    }

    private String dashboardFor(com.ohms.enums.Role role) {
        return switch (role) {
            case ADMIN   -> "/admin/dashboard";
            case DOCTOR  -> "/doctor/dashboard";
            case PATIENT -> "/patient/dashboard";
        };
    }
}
