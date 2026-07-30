package com.ohms.model;

import com.ohms.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Appointment — links a patient to a doctor at a specific date/time.
 *
 * INTERVIEW POINTS:
 *   - Demonstrates Comparable<Appointment> — sorted by date ascending.
 *   - Uses enums for type-safe status management.
 *   - Joined fields (patient, doctor) are populated at service layer
 *     to avoid N+1 queries in presentation.
 */
public class Appointment implements Comparable<Appointment> {

    private int               id;
    private int               patientId;
    private int               doctorId;
    private LocalDate         appointmentDate;
    private LocalTime         appointmentTime;
    private AppointmentStatus status;
    private String            reason;
    private String            notes;
    private String            diagnosis;
    private String            cancelledBy;
    private String            cancelReason;
    private LocalDateTime     createdAt;
    private LocalDateTime     updatedAt;

    // Joined fields
    private Patient           patient;
    private Doctor            doctor;

    // ── Constructors ─────────────────────────────────────────────

    public Appointment() {}

    /** Constructor for booking a new appointment */
    public Appointment(int patientId, int doctorId,
                       LocalDate date, LocalTime time, String reason) {
        this.patientId       = patientId;
        this.doctorId        = doctorId;
        this.appointmentDate = date;
        this.appointmentTime = time;
        this.reason          = reason;
        this.status          = AppointmentStatus.PENDING;
    }

    // ── Comparable — sort by date ascending ──────────────────────

    @Override
    public int compareTo(Appointment other) {
        int dateCmp = this.appointmentDate.compareTo(other.appointmentDate);
        if (dateCmp != 0) return dateCmp;
        return this.appointmentTime.compareTo(other.appointmentTime);
    }

    // ── Status transition helpers ────────────────────────────────

    public boolean canBeCancelledByPatient() {
        return status == AppointmentStatus.PENDING
            || status == AppointmentStatus.CONFIRMED;
    }

    public boolean canBeConfirmedByDoctor() {
        return status == AppointmentStatus.PENDING;
    }

    public boolean canBeCompleted() {
        return status == AppointmentStatus.CONFIRMED;
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int               getId()                             { return id; }
    public void              setId(int id)                       { this.id = id; }

    public int               getPatientId()                      { return patientId; }
    public void              setPatientId(int pid)               { this.patientId = pid; }

    public int               getDoctorId()                       { return doctorId; }
    public void              setDoctorId(int did)                { this.doctorId = did; }

    public LocalDate         getAppointmentDate()                { return appointmentDate; }
    public void              setAppointmentDate(LocalDate d)     { this.appointmentDate = d; }

    public LocalTime         getAppointmentTime()                { return appointmentTime; }
    public void              setAppointmentTime(LocalTime t)     { this.appointmentTime = t; }

    public AppointmentStatus getStatus()                         { return status; }
    public void              setStatus(AppointmentStatus s)      { this.status = s; }

    public String            getReason()                         { return reason; }
    public void              setReason(String reason)            { this.reason = reason; }

    public String            getNotes()                          { return notes; }
    public void              setNotes(String notes)              { this.notes = notes; }

    public String            getDiagnosis()                      { return diagnosis; }
    public void              setDiagnosis(String d)              { this.diagnosis = d; }

    public String            getCancelledBy()                    { return cancelledBy; }
    public void              setCancelledBy(String c)            { this.cancelledBy = c; }

    public String            getCancelReason()                   { return cancelReason; }
    public void              setCancelReason(String r)           { this.cancelReason = r; }

    public LocalDateTime     getCreatedAt()                      { return createdAt; }
    public void              setCreatedAt(LocalDateTime t)       { this.createdAt = t; }

    public LocalDateTime     getUpdatedAt()                      { return updatedAt; }
    public void              setUpdatedAt(LocalDateTime t)       { this.updatedAt = t; }

    public Patient           getPatient()                        { return patient; }
    public void              setPatient(Patient patient)         { this.patient = patient; }

    public Doctor            getDoctor()                         { return doctor; }
    public void              setDoctor(Doctor doctor)            { this.doctor = doctor; }

    @Override
    public String toString() {
        return "Appointment{id=" + id + ", date=" + appointmentDate
             + ", time=" + appointmentTime + ", status=" + status + "}";
    }
}
