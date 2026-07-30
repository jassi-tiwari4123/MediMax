package com.ohms.controller.patient;

import com.ohms.dao.DepartmentDAO;
import com.ohms.dao.DepartmentDAOImpl;
import com.ohms.dao.DoctorDAO;
import com.ohms.dao.DoctorDAOImpl;
import com.ohms.dao.PatientDAO;
import com.ohms.dao.PatientDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Patient;
import com.ohms.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * AppointmentBookingServlet — patient books an appointment.
 *
 * URL: /patient/book-appointment
 * GET  ?doctorId=X → show booking form pre-loaded with doctor info
 * POST             → submit booking
 *
 * Also handles appointment cancellation:
 * POST ?action=cancel → cancel patient's own appointment
 */
public class AppointmentBookingServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentBookingServlet.class);

    private final PatientDAO         patientDAO  = new PatientDAOImpl();
    private final DoctorDAO          doctorDAO   = new DoctorDAOImpl();
    private final DepartmentDAO      deptDAO     = new DepartmentDAOImpl();
    private final AppointmentService apptService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String doctorIdStr = req.getParameter("doctorId");

        try {
            req.setAttribute("departments", deptDAO.findActive());

            if (doctorIdStr != null && !doctorIdStr.isBlank()) {
                int doctorId = Integer.parseInt(doctorIdStr);
                req.setAttribute("selectedDoctor", doctorDAO.findById(doctorId).orElse(null));
            }

            req.setAttribute("doctors", doctorDAO.findApproved());
            req.getRequestDispatcher("/jsp/patient/book-appointment.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("BookingServlet GET error: {}", e.getMessage(), e);
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    userId = (int) req.getAttribute("userId");
        String action = req.getParameter("action");

        try {
            Patient patient = patientDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Patient profile not found."));

            if ("cancel".equals(action)) {
                int    apptId = Integer.parseInt(req.getParameter("appointmentId"));
                String reason = req.getParameter("cancelReason");
                apptService.cancel(apptId, "PATIENT", reason);
                req.getSession().setAttribute("flashSuccess", "Appointment cancelled successfully.");
                resp.sendRedirect(req.getContextPath() + "/patient/dashboard");
                return;
            }

            // Default action: book
            int       doctorId = Integer.parseInt(req.getParameter("doctorId"));
            LocalDate date     = LocalDate.parse(req.getParameter("appointmentDate"));
            LocalTime time     = LocalTime.parse(req.getParameter("appointmentTime"));
            String    reason   = req.getParameter("reason");

            apptService.book(patient.getId(), doctorId, date, time, reason);

            req.getSession().setAttribute("flashSuccess",
                "Appointment booked successfully! Check your email for confirmation.");
            resp.sendRedirect(req.getContextPath() + "/patient/dashboard");

        } catch (OhmsException e) {
            logger.warn("Booking failed: {}", e.getMessage());
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/patient/book-appointment");
        } catch (Exception e) {
            logger.error("Booking unexpected error: {}", e.getMessage(), e);
            req.getSession().setAttribute("flashError",
                "An unexpected error occurred. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/patient/book-appointment");
        }
    }
}
