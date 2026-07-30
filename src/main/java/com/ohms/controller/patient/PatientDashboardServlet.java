package com.ohms.controller.patient;

import com.ohms.dao.PatientDAO;
import com.ohms.dao.PatientDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Patient;
import com.ohms.service.AppointmentService;
import com.ohms.service.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * PatientDashboardServlet — patient's home page with upcoming appointments
 * and recent prescriptions.
 *
 * URL: /patient/dashboard
 */
public class PatientDashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PatientDashboardServlet.class);

    private final PatientDAO         patientDAO   = new PatientDAOImpl();
    private final AppointmentService apptService  = new AppointmentService();
    private final PrescriptionService prescService = new PrescriptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");

        try {
            Patient patient = patientDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Patient profile not found."));

            req.setAttribute("patient",       patient);
            req.setAttribute("appointments",  apptService.getPatientAppointments(patient.getId()));
            req.setAttribute("prescriptions", prescService.getByPatient(patient.getId()));

            req.getRequestDispatcher("/jsp/patient/dashboard.jsp").forward(req, resp);

        } catch (OhmsException e) {
            logger.error("PatientDashboard error: {}", e.getMessage(), e);
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }
}
