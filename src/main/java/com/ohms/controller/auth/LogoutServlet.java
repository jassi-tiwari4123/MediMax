package com.ohms.controller.auth;

import com.ohms.utility.JwtUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LogoutServlet — clears the JWT cookie and redirects to login.
 *
 * INTERVIEW POINT:
 *   JWT is stateless — there's no server-side session to invalidate.
 *   Logout is achieved by deleting the cookie from the browser.
 *   For production, a token blacklist in Redis can be added
 *   to immediately revoke tokens before their natural expiry.
 *
 * URL: /logout
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logout(req, resp);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Expire the JWT cookie immediately
        Cookie cookie = new Cookie(JwtUtil.COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);

        // Invalidate any server-side session too (defensive)
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();

        resp.sendRedirect(req.getContextPath() + "/login?logout=true");
    }
}
