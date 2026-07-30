package com.ohms.model;

/**
 * PrescriptionItem — a single medicine entry inside a Prescription.
 *
 * INTERVIEW POINT:
 *   Demonstrates composition — Prescription HAS-MANY PrescriptionItems.
 *   Each item captures morning/afternoon/night dosage as boolean flags.
 */
public class PrescriptionItem {

    private int     id;
    private int     prescriptionId;
    private String  medicineName;
    private String  dosage;           // e.g. "500mg"
    private boolean morning;
    private boolean afternoon;
    private boolean night;
    private Integer durationDays;
    private String  instructions;

    // ── Constructors ─────────────────────────────────────────────

    public PrescriptionItem() {}

    public PrescriptionItem(String medicineName, String dosage,
                            boolean morning, boolean afternoon, boolean night,
                            Integer durationDays, String instructions) {
        this.medicineName  = medicineName;
        this.dosage        = dosage;
        this.morning       = morning;
        this.afternoon     = afternoon;
        this.night         = night;
        this.durationDays  = durationDays;
        this.instructions  = instructions;
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int     getId()                             { return id; }
    public void    setId(int id)                       { this.id = id; }

    public int     getPrescriptionId()                 { return prescriptionId; }
    public void    setPrescriptionId(int pid)          { this.prescriptionId = pid; }

    public String  getMedicineName()                   { return medicineName; }
    public void    setMedicineName(String name)        { this.medicineName = name; }

    public String  getDosage()                         { return dosage; }
    public void    setDosage(String dosage)            { this.dosage = dosage; }

    public boolean isMorning()                         { return morning; }
    public void    setMorning(boolean morning)         { this.morning = morning; }

    public boolean isAfternoon()                       { return afternoon; }
    public void    setAfternoon(boolean afternoon)     { this.afternoon = afternoon; }

    public boolean isNight()                           { return night; }
    public void    setNight(boolean night)             { this.night = night; }

    public Integer getDurationDays()                   { return durationDays; }
    public void    setDurationDays(Integer days)       { this.durationDays = days; }

    public String  getInstructions()                   { return instructions; }
    public void    setInstructions(String inst)        { this.instructions = inst; }

    /** Human-readable schedule e.g. "Morning + Night" */
    public String getScheduleSummary() {
        StringBuilder sb = new StringBuilder();
        if (morning)   sb.append("Morning ");
        if (afternoon) sb.append("Afternoon ");
        if (night)     sb.append("Night");
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return "PrescriptionItem{medicine='" + medicineName + "', dosage='"
             + dosage + "', schedule='" + getScheduleSummary() + "'}";
    }
}
