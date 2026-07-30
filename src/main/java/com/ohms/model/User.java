package com.ohms.model;

import com.ohms.enums.Gender;
import com.ohms.enums.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User — base entity representing any authenticated user in OHMS.
 *
 * INTERVIEW POINTS:
 *   Encapsulation  — all fields are private; accessed via getters/setters.
 *   Inheritance    — Doctor and Patient extend this class (indirectly via
 *                    composition, but User is the auth record for all).
 *   Constructor Overloading — multiple constructors for different use cases.
 */
public class User {

    // ── Fields ──────────────────────────────────────────────────
    private int           id;
    private String        fullName;
    private String        email;
    private String        phone;
    private String        passwordHash;   // never expose raw password
    private Role          role;
    private Gender        gender;
    private LocalDate     dateOfBirth;
    private String        profileImage;
    private boolean       active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Constructor Overloading ──────────────────────────────────

    /** Default constructor — required for JDBC ResultSet mapping */
    public User() {}

    /** Minimal constructor — for registration */
    public User(String fullName, String email, String phone,
                String passwordHash, Role role, Gender gender) {
        this.fullName     = fullName;
        this.email        = email;
        this.phone        = phone;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.gender       = gender;
        this.active       = true;
    }

    /** Full constructor — for reconstructing from DB */
    public User(int id, String fullName, String email, String phone,
                String passwordHash, Role role, Gender gender,
                LocalDate dateOfBirth, String profileImage,
                boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id           = id;
        this.fullName     = fullName;
        this.email        = email;
        this.phone        = phone;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.gender       = gender;
        this.dateOfBirth  = dateOfBirth;
        this.profileImage = profileImage;
        this.active       = active;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int            getId()           { return id; }
    public void           setId(int id)     { this.id = id; }

    public String         getFullName()                  { return fullName; }
    public void           setFullName(String fullName)   { this.fullName = fullName; }

    public String         getEmail()                     { return email; }
    public void           setEmail(String email)         { this.email = email; }

    public String         getPhone()                     { return phone; }
    public void           setPhone(String phone)         { this.phone = phone; }

    public String         getPasswordHash()              { return passwordHash; }
    public void           setPasswordHash(String hash)   { this.passwordHash = hash; }

    public Role           getRole()                      { return role; }
    public void           setRole(Role role)             { this.role = role; }

    public Gender         getGender()                    { return gender; }
    public void           setGender(Gender gender)       { this.gender = gender; }

    public LocalDate      getDateOfBirth()               { return dateOfBirth; }
    public void           setDateOfBirth(LocalDate dob)  { this.dateOfBirth = dob; }

    public String         getProfileImage()              { return profileImage; }
    public void           setProfileImage(String img)    { this.profileImage = img; }

    public boolean        isActive()                     { return active; }
    public void           setActive(boolean active)      { this.active = active; }

    public LocalDateTime  getCreatedAt()                 { return createdAt; }
    public void           setCreatedAt(LocalDateTime t)  { this.createdAt = t; }

    public LocalDateTime  getUpdatedAt()                 { return updatedAt; }
    public void           setUpdatedAt(LocalDateTime t)  { this.updatedAt = t; }

    // ── Utility ─────────────────────────────────────────────────

    /** Never include passwordHash in toString — security best practice */
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + fullName + "', email='" + email
             + "', role=" + role + ", active=" + active + "}";
    }
}
