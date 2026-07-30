package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.EmailOtp;
import java.util.Optional;

/**
 * EmailOtpDAO — stores and retrieves OTP records for password reset.
 */
public interface EmailOtpDAO {

    void             save(EmailOtp otp)                                   throws DatabaseException;
    void             markUsed(int otpId)                                  throws DatabaseException;

    /**
     * Returns the most recent unused, non-expired OTP for a user + purpose.
     */
    Optional<EmailOtp> findLatestValid(int userId, EmailOtp.Purpose purpose) throws DatabaseException;

    /** Cleanup — remove all expired OTPs (can be run by a scheduled task). */
    void             deleteExpired()                                      throws DatabaseException;
}
