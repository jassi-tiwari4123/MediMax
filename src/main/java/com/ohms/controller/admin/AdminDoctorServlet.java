package com.ohms.controller.admin;

import com.ohms.dao.*;
import com.ohms.exception.OhmsException;
import com.ohms.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * AdminDoctorServlet — list, approve, reject, deactivate doctors.
 *
 * URL: /admin/doctors
 * GET  ?action=list   → all doctors
 * GET  ?action=pending → pending approval list
 * POST ?action=approve  → approve a doctor
 * POST ?action=reject   → reject a doctor
 * POST ?action=delete   → deactivate doctor's user account
 */
public class AdminDoctorServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminDoctorServlet.class);

    private final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private final UserDAO   userDAO   = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "pending" -> {
                    List<Doctor> pending = doctorDAO.findPending();
                    req.setAttribute("doctors", pending);
                    req.setAttribute("pageTitle", "Pending Doctor Approvals");
                }
                default -> {
                    String search = req.getParameter("search");
                    List<Doctor> doctors = (search != null && !search.isBlank())
                        ? doctorDAO.searchByName(search)
                        : doctorDAO.findAll();
                    req.setAttribute("doctors", doctors);
                    req.setAttribute("pageTitle", "Manage Doctors");
                    req.setAttribute("search", search);
                }
            }
            req.getRequestDispatcher("/jsp/admin/doctors.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("AdminDoctorServlet GET error: {}", e.getMessage(), e);
            req.setAttribute("error", "Failed to load doctors.");
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action   = req.getParameter("action");
        String idStr    = req.getParameter("doctorId");

        try {
            int doctorId = Integer.parseInt(idStr);

            switch (action) {
                case "approve" -> {
                    doctorDAO.updateStatus(doctorId, "APPROVED");
                    req.getSession().setAttribute("flashSuccess", "Doctor approved successfully.");
                }
                case "reject" -> {
                    doctorDAO.updateStatus(doctorId, "REJECTED");
                    req.getSession().setAttribute("flashSuccess", "Doctor registration rejected.");
                }
                case "deactivate" -> {
                    Doctor doc = doctorDAO.findById(doctorId)
                        .orElseThrow(() -> new OhmsException("Doctor not found."));
                    userDAO.deactivate(doc.getUserId());
                    req.getSession().setAttribute("flashSuccess", "Doctor account deactivated.");
                }
                default -> req.getSession().setAttribute("flashError", "Unknown action.");
            }

        } catch (OhmsException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashError", "Invalid doctor ID.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/doctors");
    }
}
