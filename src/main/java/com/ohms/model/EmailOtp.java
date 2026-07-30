package com.ohms.model;

import java.time.LocalDateTime;

/**
 * EmailOtp — stores a one-time password for password reset / email verify.
 *
 * INTERVIEW POINT:
 *   OTP is hashed before storing (same as passwords) — security practice.
 *   expires_at column lets us prune old records and reject expired OTPs.
 */
public class EmailOtp {

    public enum Purpose { PASSWORD_RESET, EMAIL_VERIFY }

    private int           id;
    private int           userId;
    private String        otpCode;      // plain OTP (only for sending; store hash)
    private Purpose       purpose;
    private boolean       used;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    // ── Constructors ─────────────────────────────────────────────

    public EmailOtp() {}

    public EmailOtp(int userId, String otpCode,
                    Purpose purpose, LocalDateTime expiresAt) {
        this.userId    = userId;
        this.otpCode   = otpCode;
        this.purpose   = purpose;
        this.expiresAt = expiresAt;
        this.used      = false;
    }

    // ── Business logic ───────────────────────────────────────────

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int           getId()                         { return id; }
    public void          setId(int id)                   { this.id = id; }

    public int           getUserId()                     { return userId; }
    public void          setUserId(int userId)           { this.userId = userId; }

    public String        getOtpCode()                    { return otpCode; }
    public void          setOtpCode(String otp)          { this.otpCode = otp; }

    public Purpose       getPurpose()                    { return purpose; }
    public void          setPurpose(Purpose purpose)     { this.purpose = purpose; }

    public boolean       isUsed()                        { return used; }
    public void          setUsed(boolean used)           { this.used = used; }

    public LocalDateTime getExpiresAt()                  { return expiresAt; }
    public void          setExpiresAt(LocalDateTime t)   { this.expiresAt = t; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
}
