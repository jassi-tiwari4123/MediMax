package com.ohms.controller.auth;

import com.ohms.exception.OhmsException;
import com.ohms.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * ForgotPasswordServlet — sends OTP to the user's email.
 *
 * URL: /forgot-password
 * GET  → show "enter email" form
 * POST → trigger OTP email, redirect to reset-password page
 */
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordServlet.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/common/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");

        try {
            authService.sendPasswordResetOtp(email);
            // Always show success — prevents user enumeration attacks
            req.setAttribute("success",
                "If that email is registered, an OTP has been sent. Check your inbox.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/jsp/common/reset-password.jsp").forward(req, resp);

        } catch (OhmsException e) {
            logger.error("Forgot password error: {}", e.getMessage());
            req.setAttribute("error", "Something went wrong. Please try again.");
            req.getRequestDispatcher("/jsp/common/forgot-password.jsp").forward(req, resp);
        }
    }
}
