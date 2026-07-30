package com.ohms.enums;

/**
 * Role — defines the three user roles in OHMS.
 *
 * INTERVIEW POINT:
 *   Enums are used instead of String constants so that:
 *   - The compiler catches typos (can't pass "ADMN" accidentally)
 *   - switch statements are exhaustive
 *   - Values are stored consistently in DB as strings via name()
 */
public enum Role {

    ADMIN,
    DOCTOR,
    PATIENT;

    /**
     * Case-insensitive factory method — useful when reading role from DB/request.
     *
     * @param value role string e.g. "admin" or "ADMIN"
     * @return matching Role
     * @throws IllegalArgumentException if no match found
     */
    public static Role fromString(String value) {
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
