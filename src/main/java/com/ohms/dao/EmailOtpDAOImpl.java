package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.EmailOtp;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * EmailOtpDAOImpl — JDBC implementation of EmailOtpDAO.
 */
public class EmailOtpDAOImpl implements EmailOtpDAO {

    private static final Logger logger = LoggerFactory.getLogger(EmailOtpDAOImpl.class);

    private static final String INSERT =
        "INSERT INTO email_otp (user_id, otp_code, purpose, is_used, expires_at) " +
        "VALUES (?,?,?,0,?)";

    private static final String MARK_USED =
        "UPDATE email_otp SET is_used=1 WHERE id=?";

    private static final String FIND_LATEST_VALID =
        "SELECT * FROM email_otp " +
        "WHERE user_id=? AND purpose=? AND is_used=0 AND expires_at > NOW() " +
        "ORDER BY created_at DESC LIMIT 1";

    private static final String DELETE_EXPIRED =
        "DELETE FROM email_otp WHERE expires_at <= NOW()";

    @Override
    public void save(EmailOtp otp) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT,
                                         Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    otp.getUserId());
            ps.setString(2, otp.getOtpCode());
            ps.setString(3, otp.getPurpose().name());
            ps.setTimestamp(4, Timestamp.valueOf(otp.getExpiresAt()));

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) otp.setId(keys.getInt(1));
            }

        } catch (SQLException e) {
            throw new DatabaseException("save(EmailOtp) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void markUsed(int otpId) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(MARK_USED)) {

            ps.setInt(1, otpId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("markUsed(EmailOtp) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<EmailOtp> findLatestValid(int userId, EmailOtp.Purpose purpose)
            throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_LATEST_VALID)) {

            ps.setInt(1, userId);
            ps.setString(2, purpose.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findLatestValid(EmailOtp) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteExpired() throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_EXPIRED)) {

            int deleted = ps.executeUpdate();
            logger.info("Deleted {} expired OTPs.", deleted);

        } catch (SQLException e) {
            throw new DatabaseException("deleteExpired(EmailOtp) failed: " + e.getMessage(), e);
        }
    }

    private EmailOtp mapRow(ResultSet rs) throws SQLException {
        EmailOtp otp = new EmailOtp();
        otp.setId(rs.getInt("id"));
        otp.setUserId(rs.getInt("user_id"));
        otp.setOtpCode(rs.getString("otp_code"));
        otp.setPurpose(EmailOtp.Purpose.valueOf(rs.getString("purpose")));
        otp.setUsed(rs.getBoolean("is_used"));

        Timestamp exp = rs.getTimestamp("expires_at");
        if (exp != null) otp.setExpiresAt(exp.toLocalDateTime());

        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) otp.setCreatedAt(ca.toLocalDateTime());

        return otp;
    }
}
