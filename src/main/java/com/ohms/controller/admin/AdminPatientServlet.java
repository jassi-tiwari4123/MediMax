package com.ohms.controller.admin;

import com.ohms.dao.*;
import com.ohms.exception.OhmsException;
import com.ohms.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * AdminPatientServlet — list and manage patient accounts.
 *
 * URL: /admin/patients
 * GET  → list all patients (with optional search)
 * POST ?action=deactivate → disable a patient account
 */
public class AdminPatientServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminPatientServlet.class);

    private final PatientDAO patientDAO = new PatientDAOImpl();
    private final UserDAO    userDAO    = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String search = req.getParameter("search");
            List<Patient> patients = (search != null && !search.isBlank())
                ? patientDAO.searchByName(search)
                : patientDAO.findAll();

            req.setAttribute("patients", patients);
            req.setAttribute("search", search);
            req.getRequestDispatcher("/jsp/admin/patients.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("AdminPatientServlet GET error: {}", e.getMessage(), e);
            req.setAttribute("error", "Failed to load patients.");
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action    = req.getParameter("action");
        String patientId = req.getParameter("patientId");

        try {
            if ("deactivate".equals(action)) {
                Patient patient = patientDAO.findById(Integer.parseInt(patientId))
                    .orElseThrow(() -> new OhmsException("Patient not found."));
                userDAO.deactivate(patient.getUserId());
                req.getSession().setAttribute("flashSuccess", "Patient account deactivated.");
            }
        } catch (OhmsException | NumberFormatException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/patients");
    }
}
