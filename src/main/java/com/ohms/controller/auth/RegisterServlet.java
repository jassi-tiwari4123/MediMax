package com.ohms.controller.auth;

import com.ohms.exception.OhmsException;
import com.ohms.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * RegisterServlet — patient and doctor self-registration.
 *
 * URL: /register
 * GET  → show registration form
 * POST → process registration, redirect to login on success
 */
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/common/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String role         = req.getParameter("role");          // "PATIENT" or "DOCTOR"
        String fullName     = req.getParameter("fullName");
        String email        = req.getParameter("email");
        String phone        = req.getParameter("phone");
        String password     = req.getParameter("password");
        String confirm      = req.getParameter("confirmPassword");
        String gender       = req.getParameter("gender");

        try {
            if ("DOCTOR".equalsIgnoreCase(role)) {
                int        deptId      = Integer.parseInt(req.getParameter("departmentId"));
                String     spec        = req.getParameter("specialization");
                String     qual        = req.getParameter("qualification");
                int        exp         = Integer.parseInt(req.getParameter("experienceYears"));
                BigDecimal fee         = new BigDecimal(req.getParameter("consultationFee"));

                authService.registerDoctor(fullName, email, phone, password, confirm,
                                           gender, deptId, spec, qual, exp, fee);

                req.setAttribute("success",
                    "Registration submitted! Your account is pending admin approval.");
            } else {
                authService.registerPatient(fullName, email, phone, password, confirm, gender);
                req.setAttribute("success",
                    "Registration successful! Please log in.");
            }

            req.getRequestDispatcher("/jsp/common/login.jsp").forward(req, resp);

        } catch (OhmsException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("formData", req.getParameterMap()); // re-populate form
            req.getRequestDispatcher("/jsp/common/register.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid numeric value in the form. Please check your inputs.");
            req.getRequestDispatcher("/jsp/common/register.jsp").forward(req, resp);
        }
    }
}
