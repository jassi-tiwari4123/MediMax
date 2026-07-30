package com.ohms.service;

import com.ohms.dao.*;
import com.ohms.exception.*;
import com.ohms.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * PrescriptionService — creates prescriptions and triggers PDF generation.
 *
 * INTERVIEW POINT:
 *   After saving the prescription to DB (Phase 7), we immediately generate
 *   a PDF (Phase 12) and store its path in the prescriptions table.
 *   This links the two phases cleanly.
 */
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAOImpl();
    private final AppointmentDAO  appointmentDAO  = new AppointmentDAOImpl();
    private final DoctorDAO       doctorDAO       = new DoctorDAOImpl();
    private final PatientDAO      patientDAO      = new PatientDAOImpl();
    private final EmailService    emailService    = new EmailService();
    private final PdfService      pdfService      = new PdfService();

    // ── Create Prescription ──────────────────────────────────────

    /**
     * Creates a prescription for a completed appointment.
     *
     * Steps:
     *  1. Validate appointment exists and is COMPLETED.
     *  2. Save prescription + items (in a transaction via DAO).
     *  3. Generate PDF.
     *  4. Store PDF path in DB.
     *  5. Notify patient by email.
     */
    public Prescription create(Prescription prescription) throws OhmsException {

        // Step 1 — Appointment must be COMPLETED
        Appointment appt = appointmentDAO.findById(prescription.getAppointmentId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Appointment", prescription.getAppointmentId()));

        if (appt.getStatus() != com.ohms.enums.AppointmentStatus.COMPLETED) {
            throw new AppointmentException(
                "Prescription can only be created for a COMPLETED appointment.");
        }

        // Step 2 — Save to DB (transactional — both header + items)
        int prescId = prescriptionDAO.saveFull(prescription);
        prescription.setId(prescId);

        // Step 3 — Generate PDF
        try {
            Doctor  doctor  = doctorDAO.findById(prescription.getDoctorId()).orElse(null);
            Patient patient = patientDAO.findById(prescription.getPatientId()).orElse(null);
            prescription.setDoctor(doctor);
            prescription.setPatient(patient);

            String pdfPath = pdfService.generatePrescriptionPdf(prescription);

            // Step 4 — Store PDF path
            prescriptionDAO.updatePdfPath(prescId, pdfPath);
            prescription.setPdfPath(pdfPath);

            // Step 5 — Email patient
            if (patient != null && doctor != null && patient.getUser() != null) {
                emailService.sendPrescriptionReady(
                    patient.getUser().getEmail(),
                    patient.getFullName(),
                    doctor.getFullName()
                );
            }
        } catch (Exception e) {
            logger.warn("PDF/email step failed for prescription {}: {}", prescId, e.getMessage());
            // Not fatal — prescription is saved; patient can download from portal
        }

        logger.info("Prescription {} created for appointment {}",
                    prescId, prescription.getAppointmentId());
        return prescription;
    }

    // ── Get Methods ──────────────────────────────────────────────

    public Prescription getById(int id) throws OhmsException {
        return prescriptionDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));
    }

    public Prescription getByAppointmentId(int apptId) throws OhmsException {
        return prescriptionDAO.findByAppointmentId(apptId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Prescription for appointment", apptId));
    }

    public List<Prescription> getByPatient(int patientId) throws OhmsException {
        return prescriptionDAO.findByPatient(patientId);
    }

    public List<Prescription> getByDoctor(int doctorId) throws OhmsException {
        return prescriptionDAO.findByDoctor(doctorId);
    }
}
