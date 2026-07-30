package com.ohms.controller.auth;

import com.ohms.exception.OhmsException;
import com.ohms.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * ResetPasswordServlet — verifies OTP and updates the password.
 *
 * URL: /reset-password
 * GET  → show reset form (with email pre-filled)
 * POST → validate OTP, save new password, redirect to login
 */
public class ResetPasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServlet.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/common/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email           = req.getParameter("email");
        String otp             = req.getParameter("otp");
        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        try {
            authService.resetPassword(email, otp, newPassword, confirmPassword);
            req.setAttribute("success", "Password reset successfully. Please log in.");
            req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);

        } catch (OhmsException e) {
            logger.warn("Reset password failed: {}", e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/jsp/common/reset-password.jsp").forward(req, resp);
        }
    }
}
