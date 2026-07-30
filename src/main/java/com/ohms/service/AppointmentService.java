package com.ohms.service;

import com.ohms.dao.*;
import com.ohms.enums.AppointmentStatus;
import com.ohms.exception.*;
import com.ohms.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

/**
 * AppointmentService — all business logic for booking, cancelling,
 * rescheduling, and updating appointment status.
 *
 * INTERVIEW POINTS:
 *   - Validates business rules BEFORE touching the database.
 *   - isSlotTaken() prevents double-booking (enforced at app + DB level).
 *   - Uses Comparable<Appointment> to sort the list (demonstrates Collections.sort).
 *   - Sends emails after state changes.
 */
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final DoctorDAO      doctorDAO      = new DoctorDAOImpl();
    private final PatientDAO     patientDAO     = new PatientDAOImpl();
    private final EmailService   emailService   = new EmailService();

    // ── Book Appointment ─────────────────────────────────────────

    /**
     * Books an appointment after checking:
     * 1. Appointment date is not in the past.
     * 2. Doctor exists and is approved.
     * 3. Slot is not already taken.
     *
     * @return saved Appointment
     */
    public Appointment book(int patientId, int doctorId,
                            LocalDate date, LocalTime time,
                            String reason)
            throws OhmsException {

        // Rule 1 — no past dates
        if (date.isBefore(LocalDate.now())) {
            throw new AppointmentException(
                "Appointment date cannot be in the past.",
                AppointmentException.PAST_DATE);
        }

        // Rule 2 — doctor must be APPROVED
        Doctor doctor = doctorDAO.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));

        if (doctor.getStatus() != com.ohms.enums.DoctorStatus.APPROVED) {
            throw new AppointmentException(
                "This doctor is not currently available for appointments.");
        }

        // Rule 3 — prevent double booking
        if (appointmentDAO.isSlotTaken(doctorId, date, time)) {
            throw new AppointmentException(
                "The selected time slot is already booked. Please choose another.",
                AppointmentException.DOUBLE_BOOKING);
        }

        // Save
        Appointment appt = new Appointment(patientId, doctorId, date, time, reason);
        int apptId = appointmentDAO.save(appt);
        appt.setId(apptId);

        // Email patient
        try {
            Patient patient = patientDAO.findById(patientId).orElse(null);
            if (patient != null && patient.getUser() != null) {
                emailService.sendAppointmentConfirmation(
                    patient.getUser().getEmail(),
                    patient.getFullName(),
                    doctor.getFullName(),
                    date.toString(),
                    time.toString()
                );
            }
        } catch (Exception e) {
            logger.warn("Appointment confirmation email failed: {}", e.getMessage());
        }

        logger.info("Appointment booked: id={}, patient={}, doctor={}", apptId, patientId, doctorId);
        return appt;
    }

    // ── Cancel Appointment ───────────────────────────────────────

    /**
     * Cancels an appointment. Only PENDING or CONFIRMED appointments can be cancelled.
     *
     * @param cancelledBy "PATIENT", "DOCTOR", or "ADMIN"
     */
    public void cancel(int appointmentId, String cancelledBy, String reason)
            throws OhmsException {

        Appointment appt = appointmentDAO.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        if (!appt.canBeCancelledByPatient()) {
            throw new AppointmentException(
                "Appointment in status '" + appt.getStatus().getDisplayName() +
                "' cannot be cancelled.",
                AppointmentException.INVALID_STATUS_CHANGE);
        }

        appointmentDAO.updateStatus(appointmentId,
                                    AppointmentStatus.CANCELLED.name(),
                                    cancelledBy, reason);

        // Email patient
        try {
            Patient patient = patientDAO.findById(appt.getPatientId()).orElse(null);
            Doctor  doctor  = doctorDAO.findById(appt.getDoctorId()).orElse(null);
            if (patient != null && doctor != null && patient.getUser() != null) {
                emailService.sendAppointmentCancellation(
                    patient.getUser().getEmail(),
                    patient.getFullName(),
                    doctor.getFullName(),
                    appt.getAppointmentDate().toString(),
                    reason
                );
            }
        } catch (Exception e) {
            logger.warn("Cancellation email failed: {}", e.getMessage());
        }

        logger.info("Appointment {} cancelled by {}", appointmentId, cancelledBy);
    }

    // ── Confirm Appointment (Doctor) ─────────────────────────────

    public void confirm(int appointmentId) throws OhmsException {
        Appointment appt = appointmentDAO.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        if (!appt.canBeConfirmedByDoctor()) {
            throw new AppointmentException(
                "Only PENDING appointments can be confirmed.",
                AppointmentException.INVALID_STATUS_CHANGE);
        }

        appointmentDAO.updateStatus(appointmentId,
                                    AppointmentStatus.CONFIRMED.name(), null, null);

        // Send confirmation email to patient
        try {
            Patient patient = patientDAO.findById(appt.getPatientId()).orElse(null);
            Doctor  doctor  = doctorDAO.findById(appt.getDoctorId()).orElse(null);
            if (patient != null && doctor != null && patient.getUser() != null) {
                emailService.sendAppointmentConfirmation(
                    patient.getUser().getEmail(),
                    patient.getFullName(),
                    doctor.getFullName(),
                    appt.getAppointmentDate().toString(),
                    appt.getAppointmentTime().toString()
                );
            }
        } catch (Exception e) {
            logger.warn("Confirmation email failed: {}", e.getMessage());
        }

        logger.info("Appointment {} confirmed.", appointmentId);
    }

    // ── Complete Appointment (Doctor) ────────────────────────────

    public void complete(int appointmentId, String notes, String diagnosis)
            throws OhmsException {

        Appointment appt = appointmentDAO.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        if (!appt.canBeCompleted()) {
            throw new AppointmentException(
                "Only CONFIRMED appointments can be marked as completed.",
                AppointmentException.INVALID_STATUS_CHANGE);
        }

        appt.setStatus(AppointmentStatus.COMPLETED);
        appt.setNotes(notes);
        appt.setDiagnosis(diagnosis);
        appointmentDAO.update(appt);
        logger.info("Appointment {} completed.", appointmentId);
    }

    // ── Fetch Methods ────────────────────────────────────────────

    /**
     * Returns sorted list of appointments for a patient.
     * Demonstrates: Collections.sort() with Comparable<Appointment>.
     */
    public List<Appointment> getPatientAppointments(int patientId) throws OhmsException {
        List<Appointment> list = appointmentDAO.findByPatient(patientId);
        Collections.sort(list);  // uses Appointment.compareTo() — sort by date
        return list;
    }

    public List<Appointment> getDoctorAppointments(int doctorId) throws OhmsException {
        return appointmentDAO.findByDoctor(doctorId);
    }

    public List<Appointment> getAll() throws OhmsException {
        return appointmentDAO.findAll();
    }

    public Appointment getById(int id) throws OhmsException {
        return appointmentDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    // ── Dashboard Counts ─────────────────────────────────────────

    public int countPending()   throws OhmsException { return appointmentDAO.countByStatus("PENDING");   }
    public int countConfirmed() throws OhmsException { return appointmentDAO.countByStatus("CONFIRMED"); }
    public int countCompleted() throws OhmsException { return appointmentDAO.countByStatus("COMPLETED"); }
    public int countCancelled() throws OhmsException { return appointmentDAO.countByStatus("CANCELLED"); }
}
