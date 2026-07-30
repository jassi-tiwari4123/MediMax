package com.ohms.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * RoleFilter — enforces role-based access control AFTER AuthFilter validates the token.
 *
 * INTERVIEW POINTS:
 *   Runs after AuthFilter (filter chain order is defined by web.xml mapping order).
 *   By the time this filter runs, "role" is already set as a request attribute.
 *
 *   Rules:
 *     /admin/*  → only ADMIN
 *     /doctor/* → only DOCTOR
 *     /patient/* → only PATIENT
 *
 *   If role doesn't match the URL prefix, redirect to /403.
 */
public class RoleFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RoleFilter.class);

    @Override
    public void init(FilterConfig config) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String role       = (String) request.getAttribute("role");
        String requestURI = request.getRequestURI();

        if (role == null) {
            // AuthFilter should have set this — if not, reject
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        boolean allowed = isAllowed(requestURI, role);

        if (!allowed) {
            logger.warn("Access denied: role={}, uri={}", role, requestURI);
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                               "You do not have permission to access this page.");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}

    // ── Role-URI mapping ─────────────────────────────────────────

    private boolean isAllowed(String uri, String role) {
        if (uri.contains("/admin/"))   return "ADMIN".equals(role);
        if (uri.contains("/doctor/"))  return "DOCTOR".equals(role);
        if (uri.contains("/patient/")) return "PATIENT".equals(role);
        return true; // public URLs not in any protected path
    }
}
