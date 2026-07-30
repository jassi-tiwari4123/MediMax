package com.ohms.model;

import java.time.LocalDateTime;

/**
 * Department — represents a hospital department (Cardiology, ENT, etc.)
 *
 * INTERVIEW POINT:
 *   Simple POJO demonstrating Encapsulation.
 *   Used in doctor search/filter and admin management.
 */
public class Department {

    private int           id;
    private String        name;
    private String        description;
    private boolean       active;
    private LocalDateTime createdAt;

    // ── Constructors ─────────────────────────────────────────────

    public Department() {}

    public Department(String name, String description) {
        this.name        = name;
        this.description = description;
        this.active      = true;
    }

    public Department(int id, String name, String description,
                      boolean active, LocalDateTime createdAt) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.active      = active;
        this.createdAt   = createdAt;
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int            getId()                         { return id; }
    public void           setId(int id)                   { this.id = id; }

    public String         getName()                       { return name; }
    public void           setName(String name)            { this.name = name; }

    public String         getDescription()                { return description; }
    public void           setDescription(String desc)     { this.description = desc; }

    public boolean        isActive()                      { return active; }
    public void           setActive(boolean active)       { this.active = active; }

    public LocalDateTime  getCreatedAt()                  { return createdAt; }
    public void           setCreatedAt(LocalDateTime t)   { this.createdAt = t; }

    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "'}";
    }
}
