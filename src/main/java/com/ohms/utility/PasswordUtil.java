package com.ohms.utility;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * PasswordUtil — BCrypt-based password hashing and verification.
 *
 * INTERVIEW POINTS:
 *   - BCrypt is a one-way hashing algorithm with a built-in salt.
 *   - "strength" (cost factor) of 12 means 2^12 = 4096 iterations — slow
 *     enough to deter brute-force attacks but fast enough for normal use.
 *   - We NEVER store plain-text passwords.
 *   - verify() is timing-safe — BCrypt library handles that internally.
 *
 * Usage:
 *   String hash = PasswordUtil.hash("myPassword");
 *   boolean ok  = PasswordUtil.verify("myPassword", hash);
 */
public final class PasswordUtil {

    /** BCrypt cost factor — configurable via application.properties */
    private static final int STRENGTH =
        AppConfig.getInt("bcrypt.strength", 12);

    // Prevent instantiation
    private PasswordUtil() {}

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param plainPassword the raw password entered by the user
     * @return BCrypt hash string (60 characters)
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }
        return BCrypt.withDefaults()
                     .hashToString(STRENGTH, plainPassword.toCharArray());
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainPassword password provided at login
     * @param hashedPassword hash stored in DB
     * @return true if password matches, false otherwise
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer()
                                     .verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }

    /**
     * Generates a random numeric OTP of specified length.
     *
     * @param length number of digits
     * @return numeric OTP string
     */
    public static String generateOtp(int length) {
        StringBuilder sb = new StringBuilder();
        java.util.Random rng = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(rng.nextInt(10));
        }
        return sb.toString();
    }
}
