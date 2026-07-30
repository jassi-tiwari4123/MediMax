package com.ohms.controller.doctor;

import com.ohms.dao.DoctorDAO;
import com.ohms.dao.DoctorDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Doctor;
import com.ohms.model.Prescription;
import com.ohms.model.PrescriptionItem;
import com.ohms.service.AppointmentService;
import com.ohms.service.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

/**
 * PrescriptionServlet — doctor creates a prescription for a completed appointment.
 *
 * URL: /doctor/prescription
 * GET  ?appointmentId=X → show prescription form
 * POST                  → save prescription, generate PDF
 *
 * Supports multiple medicine rows submitted as arrays:
 *   medicineName[], dosage[], morning[], afternoon[], night[], durationDays[]
 */
public class PrescriptionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionServlet.class);

    private final DoctorDAO          doctorDAO    = new DoctorDAOImpl();
    private final AppointmentService apptService  = new AppointmentService();
    private final PrescriptionService prescService = new PrescriptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");
        String apptIdStr = req.getParameter("appointmentId");

        try {
            Doctor doctor = doctorDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Doctor not found."));

            int apptId = Integer.parseInt(apptIdStr);
            req.setAttribute("appointment", apptService.getById(apptId));
            req.setAttribute("doctor", doctor);
            req.getRequestDispatcher("/jsp/doctor/prescription-form.jsp").forward(req, resp);

        } catch (OhmsException | NumberFormatException e) {
            logger.error("PrescriptionServlet GET error: {}", e.getMessage(), e);
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/doctor/appointments");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");

        try {
            Doctor doctor = doctorDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Doctor not found."));

            int    apptId    = Integer.parseInt(req.getParameter("appointmentId"));
            int    patientId = Integer.parseInt(req.getParameter("patientId"));
            String diagnosis = req.getParameter("diagnosis");
            String instructions = req.getParameter("instructions");
            String followUpStr  = req.getParameter("followUpDate");

            Prescription presc = new Prescription(apptId, doctor.getId(),
                                                   patientId, diagnosis);
            presc.setInstructions(instructions);
            if (followUpStr != null && !followUpStr.isBlank()) {
                presc.setFollowUpDate(LocalDate.parse(followUpStr));
            }

            // Parse multiple medicine rows
            String[] medicines   = req.getParameterValues("medicineName");
            String[] dosages     = req.getParameterValues("dosage");
            String[] mornings    = req.getParameterValues("morning");
            String[] afternoons  = req.getParameterValues("afternoon");
            String[] nights      = req.getParameterValues("night");
            String[] durations   = req.getParameterValues("durationDays");
            String[] itemInstr   = req.getParameterValues("itemInstructions");

            if (medicines != null) {
                for (int i = 0; i < medicines.length; i++) {
                    if (medicines[i] == null || medicines[i].isBlank()) continue;

                    PrescriptionItem item = new PrescriptionItem(
                        medicines[i],
                        dosages != null && dosages.length > i ? dosages[i] : "",
                        isChecked(mornings,   i),
                        isChecked(afternoons, i),
                        isChecked(nights,     i),
                        parseDuration(durations, i),
                        itemInstr != null && itemInstr.length > i ? itemInstr[i] : null
                    );
                    presc.addItem(item);
                }
            }

            prescService.create(presc);
            req.getSession().setAttribute("flashSuccess",
                "Prescription created and PDF generated successfully.");
            resp.sendRedirect(req.getContextPath() + "/doctor/appointments");

        } catch (OhmsException | NumberFormatException e) {
            logger.error("PrescriptionServlet POST error: {}", e.getMessage(), e);
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/doctor/appointments");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private boolean isChecked(String[] arr, int i) {
        if (arr == null || arr.length <= i) return false;
        return "on".equalsIgnoreCase(arr[i]) || "true".equalsIgnoreCase(arr[i]);
    }

    private Integer parseDuration(String[] arr, int i) {
        if (arr == null || arr.length <= i || arr[i] == null || arr[i].isBlank()) return null;
        try { return Integer.parseInt(arr[i].trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
