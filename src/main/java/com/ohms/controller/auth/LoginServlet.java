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
 *   - Always clears stale cookies before showing login form
 *
 * URL: /login
 */
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthService authService = new AuthService();

    // ── GET — show login form ─────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String sessionExpired = req.getParameter("sessionExpired");
        String logout         = req.getParameter("logout");

        // If coming from logout or session expiry — clear cookie and show login
        if (logout != null || sessionExpired != null) {
            clearJwtCookie(resp);
            req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);
            return;
        }

        // If valid JWT cookie exists — auto-redirect to dashboard
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (JwtUtil.COOKIE_NAME.equals(c.getName())) {
                    try {
                        com.ohms.enums.Role role = JwtUtil.getRoleFromToken(c.getValue());
                        resp.sendRedirect(req.getContextPath() + dashboardFor(role));
                        return;
                    } catch (Exception ignored) {
                        // Invalid/expired cookie — clear it and show login
                        clearJwtCookie(resp);
                    }
                }
            }
        }

        req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);
    }

    // ── POST — process login ──────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        // Always clear any existing JWT cookie before setting a new one
        // This ensures switching accounts works correctly
        clearJwtCookie(resp);

        try {
            String token = authService.login(email, password);
            com.ohms.enums.Role role = JwtUtil.getRoleFromToken(token);

            // Set new JWT cookie for the logged-in user
            Cookie jwtCookie = new Cookie(JwtUtil.COOKIE_NAME, token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);   // set true in production (HTTPS)
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(1800);    // 30 minutes
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String dashboardFor(com.ohms.enums.Role role) {
        return switch (role) {
            case ADMIN   -> "/admin/dashboard";
            case DOCTOR  -> "/doctor/dashboard";
            case PATIENT -> "/patient/dashboard";
        };
    }

    private void clearJwtCookie(HttpServletResponse resp) {
        Cookie clear = new Cookie(JwtUtil.COOKIE_NAME, "");
        clear.setMaxAge(0);
        clear.setPath("/");
        clear.setHttpOnly(true);
        resp.addCookie(clear);
    }
}
