package com.ohms.controller.doctor;

import com.ohms.dao.DoctorDAO;
import com.ohms.dao.DoctorDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Doctor;
import com.ohms.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * DoctorAppointmentServlet — doctor views/manages their appointments.
 *
 * URL: /doctor/appointments
 * GET  → list all doctor's appointments
 * POST ?action=confirm   → confirm a pending appointment
 * POST ?action=complete  → mark as completed (with notes/diagnosis)
 * POST ?action=cancel    → cancel an appointment
 */
public class DoctorAppointmentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAppointmentServlet.class);

    private final DoctorDAO          doctorDAO   = new DoctorDAOImpl();
    private final AppointmentService apptService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");

        try {
            Doctor doctor = doctorDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Doctor profile not found."));

            var appointments = apptService.getDoctorAppointments(doctor.getId());

            long pending   = appointments.stream().filter(a -> a.getStatus().name().equals("PENDING")).count();
            long confirmed = appointments.stream().filter(a -> a.getStatus().name().equals("CONFIRMED")).count();
            long completed = appointments.stream().filter(a -> a.getStatus().name().equals("COMPLETED")).count();

            req.setAttribute("doctor",        doctor);
            req.setAttribute("appointments",  appointments);
            req.setAttribute("pendingCount",  pending);
            req.setAttribute("confirmedCount",confirmed);
            req.setAttribute("completedCount",completed);
            req.getRequestDispatcher("/jsp/doctor/appointments.jsp").forward(req, resp);

        } catch (OhmsException e) {
            logger.error("DoctorAppointmentServlet GET error: {}", e.getMessage(), e);
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        int apptId;

        try {
            apptId = Integer.parseInt(req.getParameter("appointmentId"));
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashError", "Invalid appointment ID.");
            resp.sendRedirect(req.getContextPath() + "/doctor/appointments");
            return;
        }

        try {
            switch (action) {
                case "confirm" -> {
                    apptService.confirm(apptId);
                    req.getSession().setAttribute("flashSuccess", "Appointment confirmed.");
                }
                case "complete" -> {
                    String notes     = req.getParameter("notes");
                    String diagnosis = req.getParameter("diagnosis");
                    apptService.complete(apptId, notes, diagnosis);
                    req.getSession().setAttribute("flashSuccess",
                        "Appointment marked as completed.");
                }
                case "cancel" -> {
                    String reason = req.getParameter("cancelReason");
                    apptService.cancel(apptId, "DOCTOR", reason);
                    req.getSession().setAttribute("flashSuccess", "Appointment cancelled.");
                }
                default -> req.getSession().setAttribute("flashError", "Unknown action.");
            }
        } catch (OhmsException e) {
            logger.warn("DoctorAppointmentServlet POST error: {}", e.getMessage());
            req.getSession().setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/doctor/appointments");
    }
}
