package com.ohms.controller.admin;

import com.ohms.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AdminAppointmentServlet — admin view of all appointments with status filter.
 *
 * URL: /admin/appointments
 * GET  ?status=PENDING|CONFIRMED|COMPLETED|CANCELLED|ALL
 */
public class AdminAppointmentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminAppointmentServlet.class);
    private final AppointmentService apptService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String status = req.getParameter("status");

        try {
            if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
                req.setAttribute("appointments", apptService.getAll());
                req.setAttribute("selectedStatus", "ALL");
            } else {
                req.setAttribute("appointments",
                    apptService.getAll().stream()
                        .filter(a -> a.getStatus().name().equalsIgnoreCase(status))
                        .toList());
                req.setAttribute("selectedStatus", status.toUpperCase());
            }

            req.getRequestDispatcher("/jsp/admin/appointments.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("AdminAppointmentServlet error: {}", e.getMessage(), e);
            req.setAttribute("error", "Failed to load appointments.");
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }
}
