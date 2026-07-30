package com.ohms.service;

import com.ohms.dao.UserDAO;
import com.ohms.dao.UserDAOImpl;
import com.ohms.dao.DoctorDAO;
import com.ohms.dao.DoctorDAOImpl;
import com.ohms.dao.PatientDAO;
import com.ohms.dao.PatientDAOImpl;
import com.ohms.dao.EmailOtpDAO;
import com.ohms.dao.EmailOtpDAOImpl;
import com.ohms.enums.Role;
import com.ohms.exception.*;
import com.ohms.model.*;
import com.ohms.utility.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * AuthService — business logic for registration, login, logout, OTP reset.
 *
 * INTERVIEW POINTS:
 *   Service layer sits between Controller and DAO:
 *     Controller → AuthService → UserDAO / DoctorDAO / PatientDAO
 *
 *   - Validates inputs before touching the DB.
 *   - Coordinates multiple DAO calls in correct order.
 *   - Throws specific exceptions so the servlet can give precise error messages.
 *   - Password hashing happens HERE — DAO only stores the hash.
 */
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // Dependencies — instantiated directly (no DI framework)
    private final UserDAO     userDAO     = new UserDAOImpl();
    private final DoctorDAO   doctorDAO   = new DoctorDAOImpl();
    private final PatientDAO  patientDAO  = new PatientDAOImpl();
    private final EmailOtpDAO otpDAO      = new EmailOtpDAOImpl();
    private final EmailService emailService = new EmailService();

    // ── Patient Registration ─────────────────────────────────────

    /**
     * Registers a new patient.
     *
     * Steps:
     *  1. Validate all input fields.
     *  2. Check for duplicate email/phone.
     *  3. Hash password.
     *  4. Insert into users table.
     *  5. Insert into patients table.
     *  6. Send welcome email.
     */
    public User registerPatient(String fullName, String email, String phone,
                                String password, String confirmPassword,
                                String gender)
            throws OhmsException {

        // Step 1 — Input validation
        ValidationUtil.validateRegistration(fullName, email, phone,
                                            password, confirmPassword);

        // Step 2 — Duplicate checks
        if (userDAO.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email", email);
        }
        if (userDAO.findByPhone(phone).isPresent()) {
            throw new DuplicateResourceException("Phone", phone);
        }

        // Step 3 — Hash password
        String hash = PasswordUtil.hash(password);

        // Step 4 — Create user
        User user = new User(fullName, email, phone, hash,
                             Role.PATIENT,
                             com.ohms.enums.Gender.fromString(gender));
        int userId = userDAO.save(user);

        // Step 5 — Create patient profile
        Patient patient = new Patient(userId);
        patientDAO.save(patient);

        // Step 6 — Send welcome email (non-critical, catch silently)
        try {
            emailService.sendRegistrationSuccess(email, fullName);
        } catch (Exception e) {
            logger.warn("Welcome email failed for {}: {}", email, e.getMessage());
        }

        logger.info("Patient registered: userId={}, email={}", userId, email);
        return user;
    }

    // ── Doctor Registration ──────────────────────────────────────

    /**
     * Registers a new doctor — status defaults to PENDING until admin approves.
     */
    public User registerDoctor(String fullName, String email, String phone,
                               String password, String confirmPassword,
                               String gender, int departmentId,
                               String specialization, String qualification,
                               int experienceYears, java.math.BigDecimal consultationFee)
            throws OhmsException {

        ValidationUtil.validateRegistration(fullName, email, phone,
                                            password, confirmPassword);

        if (userDAO.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email", email);
        }
        if (userDAO.findByPhone(phone).isPresent()) {
            throw new DuplicateResourceException("Phone", phone);
        }

        String hash = PasswordUtil.hash(password);
        User user = new User(fullName, email, phone, hash,
                             Role.DOCTOR,
                             com.ohms.enums.Gender.fromString(gender));
        int userId = userDAO.save(user);

        Doctor doctor = new Doctor(userId, departmentId, specialization,
                                   qualification, experienceYears, consultationFee);
        doctorDAO.save(doctor);

        try {
            emailService.sendRegistrationSuccess(email, fullName);
        } catch (Exception e) {
            logger.warn("Welcome email failed for {}: {}", email, e.getMessage());
        }

        logger.info("Doctor registered (PENDING): userId={}, email={}", userId, email);
        return user;
    }

    // ── Login ────────────────────────────────────────────────────

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @return JWT token string
     * @throws AuthException if credentials invalid or account disabled
     */
    public String login(String email, String password) throws OhmsException {

        if (ValidationUtil.isNullOrBlank(email) || ValidationUtil.isNullOrBlank(password)) {
            throw new AuthException("Email and password are required.",
                                    AuthException.INVALID_CREDENTIALS);
        }

        // Find user
        Optional<User> optUser = userDAO.findByEmail(email.trim().toLowerCase());
        if (optUser.isEmpty()) {
            throw new AuthException("Invalid email or password.",
                                    AuthException.INVALID_CREDENTIALS);
        }

        User user = optUser.get();

        // Check account status
        if (!user.isActive()) {
            throw new AuthException("Your account has been deactivated. Contact admin.",
                                    AuthException.ACCOUNT_DISABLED);
        }

        // Verify password
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new AuthException("Invalid email or password.",
                                    AuthException.INVALID_CREDENTIALS);
        }

        // Generate JWT
        String token = JwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        logger.info("Login successful: userId={}, role={}", user.getId(), user.getRole());
        return token;
    }

    // ── Forgot Password ──────────────────────────────────────────

    /**
     * Sends an OTP to the user's email for password reset.
     * Silently succeeds even if email not found — prevents user enumeration.
     */
    public void sendPasswordResetOtp(String email) throws OhmsException {
        Optional<User> optUser = userDAO.findByEmail(email.trim().toLowerCase());
        if (optUser.isEmpty()) {
            logger.info("Password reset requested for unknown email: {}", email);
            return; // Don't reveal whether email exists
        }

        User user = optUser.get();

        // Generate 6-digit OTP, valid for 10 minutes
        String otp       = PasswordUtil.generateOtp(6);
        int    expiryMin = AppConfig.getInt("otp.expiry.minutes", 10);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMin);

        EmailOtp otpRecord = new EmailOtp(user.getId(), otp,
                                          EmailOtp.Purpose.PASSWORD_RESET, expiresAt);
        otpDAO.save(otpRecord);

        // Send email
        emailService.sendOtp(email, user.getFullName(), otp);
        logger.info("Password reset OTP sent to userId={}", user.getId());
    }

    // ── Reset Password ───────────────────────────────────────────

    /**
     * Verifies OTP and updates the user's password.
     */
    public void resetPassword(String email, String otp,
                              String newPassword, String confirmPassword)
            throws OhmsException {

        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            throw new ValidationException(
                "Password must be at least 8 characters with uppercase, " +
                "lowercase, digit, and special character.");
        }

        User user = userDAO.findByEmail(email)
                           .orElseThrow(() -> new ResourceNotFoundException("User", email));

        EmailOtp otpRecord = otpDAO.findLatestValid(user.getId(),
                                                    EmailOtp.Purpose.PASSWORD_RESET)
                                   .orElseThrow(() -> new ValidationException(
                                       "OTP is invalid or expired."));

        if (!otpRecord.getOtpCode().equals(otp)) {
            throw new ValidationException("Incorrect OTP entered.");
        }

        // Mark OTP as used
        otpDAO.markUsed(otpRecord.getId());

        // Update password
        String newHash = PasswordUtil.hash(newPassword);
        userDAO.updatePassword(user.getId(), newHash);
        logger.info("Password reset successful for userId={}", user.getId());
    }
}
