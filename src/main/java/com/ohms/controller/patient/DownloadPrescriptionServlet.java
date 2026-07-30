package com.ohms.controller.patient;

import com.ohms.dao.PatientDAO;
import com.ohms.dao.PatientDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Patient;
import com.ohms.model.Prescription;
import com.ohms.service.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;

/**
 * DownloadPrescriptionServlet — streams a prescription PDF to the browser.
 *
 * URL: /patient/download-prescription?prescriptionId=X
 *
 * INTERVIEW POINTS:
 *   - File Handling: reads bytes from disk and streams to response.
 *   - Security: verifies the prescription belongs to the requesting patient
 *     before allowing download (authorization check).
 *   - Sets Content-Disposition: attachment so browser prompts save-as.
 */
public class DownloadPrescriptionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DownloadPrescriptionServlet.class);

    private final PatientDAO         patientDAO   = new PatientDAOImpl();
    private final PrescriptionService prescService = new PrescriptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    userId    = (int) req.getAttribute("userId");
        String prescIdStr = req.getParameter("prescriptionId");

        try {
            int prescId = Integer.parseInt(prescIdStr);

            Patient patient = patientDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Patient not found."));

            Prescription presc = prescService.getById(prescId);

            // Authorization — patient can only download their own prescriptions
            if (presc.getPatientId() != patient.getId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                               "You are not authorized to download this prescription.");
                return;
            }

            String pdfPath = presc.getPdfPath();
            if (pdfPath == null || pdfPath.isBlank()) {
                req.getSession().setAttribute("flashError",
                    "PDF not yet generated. Please contact your doctor.");
                resp.sendRedirect(req.getContextPath() + "/patient/dashboard");
                return;
            }

            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists() || !pdfFile.isFile()) {
                req.getSession().setAttribute("flashError",
                    "Prescription PDF file not found on server.");
                resp.sendRedirect(req.getContextPath() + "/patient/dashboard");
                return;
            }

            // Stream the file to the browser
            String fileName = "Prescription_" + prescId + ".pdf";
            resp.setContentType("application/pdf");
            resp.setContentLengthLong(pdfFile.length());
            resp.setHeader("Content-Disposition",
                           "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out   = resp.getOutputStream();
                 InputStream  in    = Files.newInputStream(pdfFile.toPath())) {

                byte[] buffer = new byte[4096];
                int    read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }

            logger.info("Prescription {} downloaded by patientId={}",
                        prescId, patient.getId());

        } catch (OhmsException e) {
            logger.warn("Download prescription error: {}", e.getMessage());
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/patient/dashboard");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid prescription ID.");
        }
    }
}
