package com.ohms.filter;

import com.ohms.exception.AuthException;
import com.ohms.utility.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AuthFilter — validates JWT token on every protected request.
 *
 * INTERVIEW POINTS:
 *   Filters run BEFORE the servlet processes the request.
 *   This is the standard place for cross-cutting concerns
 *   like authentication — the servlet itself never sees an unauthenticated request.
 *
 *   Flow:
 *     Request → AuthFilter.doFilter() → [token valid?]
 *       YES: set userId/email/role as request attributes → forward to servlet
 *       NO:  redirect to /login
 *
 *   Token is read from an HTTP-only cookie (more secure than localStorage).
 */
public class AuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig config) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = extractTokenFromCookie(request);

        if (token == null) {
            logger.debug("No JWT cookie found — redirecting to login.");
            redirectToLogin(request, response);
            return;
        }

        try {
            Claims claims = JwtUtil.validateToken(token);

            // Attach user info to request so servlets can read them
            request.setAttribute("userId", JwtUtil.extractUserId(claims));
            request.setAttribute("email",  JwtUtil.extractEmail(claims));
            request.setAttribute("role",   JwtUtil.extractRole(claims).name());

            chain.doFilter(req, res); // proceed to servlet

        } catch (AuthException e) {
            logger.warn("Auth failed: {}", e.getMessage());
            // Clear invalid cookie and redirect
            clearJwtCookie(response);
            redirectToLogin(request, response);
        }
    }

    @Override
    public void destroy() {}

    // ── Helpers ──────────────────────────────────────────────────

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (JwtUtil.COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void redirectToLogin(HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException {
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/login?sessionExpired=true");
    }

    private void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtUtil.COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
