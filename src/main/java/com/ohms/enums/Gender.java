package com.ohms.enums;

/**
 * Gender — used on User, Doctor and Patient profiles.
 */
public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromString(String value) {
        for (Gender g : values()) {
            if (g.name().equalsIgnoreCase(value)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + value);
    }
}
