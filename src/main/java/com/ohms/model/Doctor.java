package com.ohms.model;

import com.ohms.enums.DoctorStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Doctor — profile data for a doctor user.
 *
 * INTERVIEW POINTS:
 *   Composition  — Doctor HAS-A User (userId links to users table).
 *                  We store the full User object when joining tables,
 *                  or just userId when we only need the FK.
 *   Comparable   — implements Comparable<Doctor> to sort by experience,
 *                  demonstrating the Comparable interface.
 *   Encapsulation — all fields private.
 */
public class Doctor implements Comparable<Doctor> {

    private int           id;
    private int           userId;
    private int           departmentId;
    private String        specialization;
    private String        qualification;
    private int           experienceYears;
    private BigDecimal    consultationFee;
    private String        availableDays;     // "MON,WED,FRI"
    private LocalTime     availableFrom;
    private LocalTime     availableTo;
    private String        bio;
    private DoctorStatus  status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined fields — populated when we JOIN users table
    private User          user;
    private Department    department;

    // ── Constructors ─────────────────────────────────────────────

    public Doctor() {}

    /** Constructor for new doctor registration */
    public Doctor(int userId, int departmentId, String specialization,
                  String qualification, int experienceYears,
                  BigDecimal consultationFee) {
        this.userId          = userId;
        this.departmentId    = departmentId;
        this.specialization  = specialization;
        this.qualification   = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.status          = DoctorStatus.PENDING;
    }

    // ── Comparable — sort doctors by experience (descending) ─────

    /**
     * Natural ordering: doctor with MORE experience comes first.
     * Demonstrates Comparable interface implementation.
     */
    @Override
    public int compareTo(Doctor other) {
        // descending order of experience
        return Integer.compare(other.experienceYears, this.experienceYears);
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int           getId()                             { return id; }
    public void          setId(int id)                       { this.id = id; }

    public int           getUserId()                         { return userId; }
    public void          setUserId(int userId)               { this.userId = userId; }

    public int           getDepartmentId()                   { return departmentId; }
    public void          setDepartmentId(int deptId)         { this.departmentId = deptId; }

    public String        getSpecialization()                 { return specialization; }
    public void          setSpecialization(String s)         { this.specialization = s; }

    public String        getQualification()                  { return qualification; }
    public void          setQualification(String q)          { this.qualification = q; }

    public int           getExperienceYears()                { return experienceYears; }
    public void          setExperienceYears(int y)           { this.experienceYears = y; }

    public BigDecimal    getConsultationFee()                { return consultationFee; }
    public void          setConsultationFee(BigDecimal fee)  { this.consultationFee = fee; }

    public String        getAvailableDays()                  { return availableDays; }
    public void          setAvailableDays(String days)       { this.availableDays = days; }

    public LocalTime     getAvailableFrom()                  { return availableFrom; }
    public void          setAvailableFrom(LocalTime t)       { this.availableFrom = t; }

    public LocalTime     getAvailableTo()                    { return availableTo; }
    public void          setAvailableTo(LocalTime t)         { this.availableTo = t; }

    public String        getBio()                            { return bio; }
    public void          setBio(String bio)                  { this.bio = bio; }

    public DoctorStatus  getStatus()                         { return status; }
    public void          setStatus(DoctorStatus status)      { this.status = status; }

    public LocalDateTime getCreatedAt()                      { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)       { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()                      { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime t)       { this.updatedAt = t; }

    public User          getUser()                           { return user; }
    public void          setUser(User user)                  { this.user = user; }

    public Department    getDepartment()                     { return department; }
    public void          setDepartment(Department dept)      { this.department = dept; }

    // ── Convenience method ───────────────────────────────────────

    /** Returns doctor's full name from the joined User object */
    public String getFullName() {
        return (user != null) ? user.getFullName() : "Unknown";
    }

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", specialization='" + specialization
             + "', experience=" + experienceYears + "yrs, status=" + status + "}";
    }
}
