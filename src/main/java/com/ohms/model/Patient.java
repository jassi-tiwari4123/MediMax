package com.ohms.model;

import java.time.LocalDateTime;

/**
 * Patient — profile data linked to a user of role PATIENT.
 *
 * INTERVIEW POINT:
 *   Same composition pattern as Doctor.
 *   One Patient row per User row — one-to-one relationship.
 */
public class Patient {

    private int           id;
    private int           userId;
    private String        bloodGroup;
    private String        address;
    private String        emergencyContactName;
    private String        emergencyContactPhone;
    private String        medicalHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined field
    private User          user;

    // ── Constructors ─────────────────────────────────────────────

    public Patient() {}

    public Patient(int userId) {
        this.userId = userId;
    }

    public Patient(int userId, String bloodGroup, String address,
                   String emergencyContactName, String emergencyContactPhone) {
        this.userId               = userId;
        this.bloodGroup           = bloodGroup;
        this.address              = address;
        this.emergencyContactName  = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int           getId()                              { return id; }
    public void          setId(int id)                        { this.id = id; }

    public int           getUserId()                          { return userId; }
    public void          setUserId(int userId)                { this.userId = userId; }

    public String        getBloodGroup()                      { return bloodGroup; }
    public void          setBloodGroup(String bg)             { this.bloodGroup = bg; }

    public String        getAddress()                         { return address; }
    public void          setAddress(String address)           { this.address = address; }

    public String        getEmergencyContactName()            { return emergencyContactName; }
    public void          setEmergencyContactName(String n)    { this.emergencyContactName = n; }

    public String        getEmergencyContactPhone()           { return emergencyContactPhone; }
    public void          setEmergencyContactPhone(String p)   { this.emergencyContactPhone = p; }

    public String        getMedicalHistory()                  { return medicalHistory; }
    public void          setMedicalHistory(String mh)         { this.medicalHistory = mh; }

    public LocalDateTime getCreatedAt()                       { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)        { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()                       { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime t)        { this.updatedAt = t; }

    public User          getUser()                            { return user; }
    public void          setUser(User user)                   { this.user = user; }

    public String getFullName() {
        return (user != null) ? user.getFullName() : "Unknown";
    }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", userId=" + userId
             + ", bloodGroup='" + bloodGroup + "'}";
    }
}
