package com.ohms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Prescription — created by a doctor after a completed appointment.
 *
 * INTERVIEW POINTS:
 *   - Uses List<PrescriptionItem> demonstrating Generics + Collections.
 *   - One-to-one with Appointment; one-to-many with PrescriptionItem.
 */
public class Prescription {

    private int                    id;
    private int                    appointmentId;
    private int                    doctorId;
    private int                    patientId;
    private String                 diagnosis;
    private String                 instructions;
    private LocalDate              followUpDate;
    private String                 pdfPath;
    private LocalDateTime          createdAt;
    private LocalDateTime          updatedAt;

    // Joined / child data
    private List<PrescriptionItem> items;
    private Doctor                 doctor;
    private Patient                patient;
    private Appointment            appointment;

    // ── Constructors ─────────────────────────────────────────────

    public Prescription() {
        this.items = new ArrayList<>();
    }

    public Prescription(int appointmentId, int doctorId,
                        int patientId, String diagnosis) {
        this.appointmentId = appointmentId;
        this.doctorId      = doctorId;
        this.patientId     = patientId;
        this.diagnosis     = diagnosis;
        this.items         = new ArrayList<>();
    }

    // ── Item management ──────────────────────────────────────────

    public void addItem(PrescriptionItem item) {
        this.items.add(item);
    }

    public void removeItem(PrescriptionItem item) {
        this.items.remove(item);
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int                    getId()                             { return id; }
    public void                   setId(int id)                       { this.id = id; }

    public int                    getAppointmentId()                  { return appointmentId; }
    public void                   setAppointmentId(int aid)           { this.appointmentId = aid; }

    public int                    getDoctorId()                       { return doctorId; }
    public void                   setDoctorId(int did)                { this.doctorId = did; }

    public int                    getPatientId()                      { return patientId; }
    public void                   setPatientId(int pid)               { this.patientId = pid; }

    public String                 getDiagnosis()                      { return diagnosis; }
    public void                   setDiagnosis(String d)              { this.diagnosis = d; }

    public String                 getInstructions()                   { return instructions; }
    public void                   setInstructions(String inst)        { this.instructions = inst; }

    public LocalDate              getFollowUpDate()                   { return followUpDate; }
    public void                   setFollowUpDate(LocalDate d)        { this.followUpDate = d; }

    public String                 getPdfPath()                        { return pdfPath; }
    public void                   setPdfPath(String path)             { this.pdfPath = path; }

    public LocalDateTime          getCreatedAt()                      { return createdAt; }
    public void                   setCreatedAt(LocalDateTime t)       { this.createdAt = t; }

    public LocalDateTime          getUpdatedAt()                      { return updatedAt; }
    public void                   setUpdatedAt(LocalDateTime t)       { this.updatedAt = t; }

    public List<PrescriptionItem> getItems()                          { return items; }
    public void                   setItems(List<PrescriptionItem> it) { this.items = it; }

    public Doctor                 getDoctor()                         { return doctor; }
    public void                   setDoctor(Doctor doctor)            { this.doctor = doctor; }

    public Patient                getPatient()                        { return patient; }
    public void                   setPatient(Patient patient)         { this.patient = patient; }

    public Appointment            getAppointment()                    { return appointment; }
    public void                   setAppointment(Appointment appt)    { this.appointment = appt; }

    @Override
    public String toString() {
        return "Prescription{id=" + id + ", appointmentId=" + appointmentId
             + ", items=" + items.size() + "}";
    }
}
