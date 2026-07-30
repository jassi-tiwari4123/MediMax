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
 * DoctorDashboardServlet — loads today's appointments and summary stats.
 *
 * URL: /doctor/dashboard
 *
 * Reads userId from request attribute set by AuthFilter.
 */
public class DoctorDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DoctorDashboardServlet.class);

    private final DoctorDAO          doctorDAO   = new DoctorDAOImpl();
    private final AppointmentService apptService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");

        try {
            Doctor doctor = doctorDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Doctor profile not found."));

            req.setAttribute("doctor",        doctor);
            req.setAttribute("appointments",  apptService.getDoctorAppointments(doctor.getId()));
            req.setAttribute("pendingCount",  apptService.countPending());
            req.setAttribute("confirmedCount",apptService.countConfirmed());
            req.setAttribute("completedCount",apptService.countCompleted());

            req.getRequestDispatcher("/jsp/doctor/dashboard.jsp").forward(req, resp);

        } catch (OhmsException e) {
            logger.error("DoctorDashboard error: {}", e.getMessage(), e);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }
}
