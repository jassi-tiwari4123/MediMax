package com.ohms.controller.admin;

import com.ohms.dao.*;
import com.ohms.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AdminDashboardServlet — aggregates stats for the admin dashboard.
 *
 * URL: /admin/dashboard
 *
 * Sets request attributes:
 *   totalPatients, totalDoctors, pendingDoctors,
 *   pendingAppointments, confirmedAppointments,
 *   completedAppointments, cancelledAppointments
 */
public class AdminDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);

    private final UserDAO            userDAO     = new UserDAOImpl();
    private final DoctorDAO          doctorDAO   = new DoctorDAOImpl();
    private final PatientDAO         patientDAO  = new PatientDAOImpl();
    private final AppointmentService apptService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("totalPatients",           patientDAO.count());
            req.setAttribute("totalDoctors",            doctorDAO.countApproved());
            req.setAttribute("pendingDoctors",          doctorDAO.findPending().size());
            req.setAttribute("pendingAppointments",     apptService.countPending());
            req.setAttribute("confirmedAppointments",   apptService.countConfirmed());
            req.setAttribute("completedAppointments",   apptService.countCompleted());
            req.setAttribute("cancelledAppointments",   apptService.countCancelled());
            req.setAttribute("recentAppointments",      apptService.getAll());

            req.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Admin dashboard error: {}", e.getMessage(), e);
            req.setAttribute("error", "Failed to load dashboard data.");
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }
}
