package com.ohms.utility;

import com.ohms.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ValidationUtil — reusable server-side input validation.
 *
 * INTERVIEW POINTS:
 *   - Server-side validation is mandatory even if the frontend validates.
 *   - Uses regex Pattern (compiled once as constants) for efficiency.
 *   - Returns all errors at once using List<String> (not fail-fast).
 *   - Method Overloading: multiple isValidEmail(), isValidPhone() signatures.
 */
public final class ValidationUtil {

    // ── Compiled regex patterns ──────────────────────────────────
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[6-9]\\d{9}$");                 // Indian mobile numbers

    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    private static final Pattern NAME_PATTERN =
        Pattern.compile("^[a-zA-Z .'\\-]{2,100}$");

    private ValidationUtil() {}

    // ── Individual validators ────────────────────────────────────

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isValidEmail(String email) {
        return !isNullOrBlank(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return !isNullOrBlank(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Method Overloading — validate phone with a custom pattern.
     */
    public static boolean isValidPhone(String phone, Pattern customPattern) {
        return !isNullOrBlank(phone) && customPattern.matcher(phone.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        return !isNullOrBlank(password)
            && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        return !isNullOrBlank(name) && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    // ── Bulk user registration validation ────────────────────────

    /**
     * Validates all user registration fields and collects all errors.
     *
     * @throws ValidationException if any field is invalid
     */
    public static void validateRegistration(String fullName, String email,
                                            String phone, String password,
                                            String confirmPassword)
            throws ValidationException {

        List<String> errors = new ArrayList<>();

        if (!isValidName(fullName))
            errors.add("Full name must be 2–100 characters, letters only.");

        if (!isValidEmail(email))
            errors.add("Please enter a valid email address.");

        if (!isValidPhone(phone))
            errors.add("Please enter a valid 10-digit Indian mobile number.");

        if (!isStrongPassword(password))
            errors.add("Password must be at least 8 characters and include "
                     + "uppercase, lowercase, digit, and special character.");

        if (!password.equals(confirmPassword))
            errors.add("Passwords do not match.");

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    // ── Sanitization ─────────────────────────────────────────────

    /**
     * Trims and escapes basic HTML characters to prevent XSS.
     * Note: for full XSS prevention, use OWASP Java HTML Sanitizer.
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim()
                    .replace("&",  "&amp;")
                    .replace("<",  "&lt;")
                    .replace(">",  "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'",  "&#x27;");
    }
}
