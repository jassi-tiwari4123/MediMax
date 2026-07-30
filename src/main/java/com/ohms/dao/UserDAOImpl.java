package com.ohms.dao;

import com.ohms.enums.Gender;
import com.ohms.enums.Role;
import com.ohms.exception.DatabaseException;
import com.ohms.model.User;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDAOImpl — JDBC implementation of UserDAO.
 *
 * INTERVIEW POINTS:
 *   - PreparedStatement prevents SQL injection on every query.
 *   - try-with-resources ensures Connection/Statement/ResultSet are closed.
 *   - mapRow() centralises ResultSet → User conversion (DRY principle).
 *   - Transactions used for multi-step operations.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    // ── SQL Constants ────────────────────────────────────────────
    private static final String INSERT =
        "INSERT INTO users (full_name, email, phone, password_hash, " +
        "role_id, gender, date_of_birth, profile_image, is_active) " +
        "VALUES (?, ?, ?, ?, " +
        "(SELECT id FROM roles WHERE name = ?), ?, ?, ?, ?)";

    private static final String UPDATE =
        "UPDATE users SET full_name=?, phone=?, gender=?, " +
        "date_of_birth=?, profile_image=?, updated_at=NOW() " +
        "WHERE id=?";

    private static final String DEACTIVATE =
        "UPDATE users SET is_active=0, updated_at=NOW() WHERE id=?";

    private static final String FIND_BY_ID =
        "SELECT u.*, r.name AS role_name FROM users u " +
        "JOIN roles r ON u.role_id = r.id WHERE u.id=?";

    private static final String FIND_BY_EMAIL =
        "SELECT u.*, r.name AS role_name FROM users u " +
        "JOIN roles r ON u.role_id = r.id WHERE u.email=?";

    private static final String FIND_BY_PHONE =
        "SELECT u.*, r.name AS role_name FROM users u " +
        "JOIN roles r ON u.role_id = r.id WHERE u.phone=?";

    private static final String FIND_BY_ROLE =
        "SELECT u.*, r.name AS role_name FROM users u " +
        "JOIN roles r ON u.role_id = r.id WHERE r.name=? AND u.is_active=1";

    private static final String UPDATE_PASSWORD =
        "UPDATE users SET password_hash=?, updated_at=NOW() WHERE id=?";

    private static final String COUNT_ACTIVE =
        "SELECT COUNT(*) FROM users WHERE is_active=1";

    // ── save ─────────────────────────────────────────────────────

    @Override
    public int save(User user) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT,
                                         Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getGender().name());
            ps.setObject(7, user.getDateOfBirth());  // null-safe
            ps.setString(8, user.getProfileImage());
            ps.setBoolean(9, user.isActive());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException("Inserting user failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    user.setId(generatedId);
                    logger.info("User saved with id={}", generatedId);
                    return generatedId;
                }
            }
            throw new DatabaseException("Inserting user failed, no generated key.");

        } catch (SQLException e) {
            logger.error("save() failed: {}", e.getMessage());
            throw new DatabaseException("Failed to save user: " + e.getMessage(), e);
        }
    }

    // ── update ───────────────────────────────────────────────────

    @Override
    public void update(User user) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getGender().name());
            ps.setObject(4, user.getDateOfBirth());
            ps.setString(5, user.getProfileImage());
            ps.setInt(6, user.getId());

            ps.executeUpdate();
            logger.info("User updated: id={}", user.getId());

        } catch (SQLException e) {
            logger.error("update() failed: {}", e.getMessage());
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    // ── deactivate ───────────────────────────────────────────────

    @Override
    public void deactivate(int userId) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DEACTIVATE)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
            logger.info("User deactivated: id={}", userId);

        } catch (SQLException e) {
            throw new DatabaseException("Failed to deactivate user: " + e.getMessage(), e);
        }
    }

    // ── findById ─────────────────────────────────────────────────

    @Override
    public Optional<User> findById(int id) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findById failed: " + e.getMessage(), e);
        }
    }

    // ── findByEmail ──────────────────────────────────────────────

    @Override
    public Optional<User> findByEmail(String email) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findByEmail failed: " + e.getMessage(), e);
        }
    }

    // ── findByPhone ──────────────────────────────────────────────

    @Override
    public Optional<User> findByPhone(String phone) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PHONE)) {

            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findByPhone failed: " + e.getMessage(), e);
        }
    }

    // ── findByRole ───────────────────────────────────────────────

    @Override
    public List<User> findByRole(String role) throws DatabaseException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ROLE)) {

            ps.setString(1, role.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapRow(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new DatabaseException("findByRole failed: " + e.getMessage(), e);
        }
    }

    // ── updatePassword ───────────────────────────────────────────

    @Override
    public void updatePassword(int userId, String newPasswordHash)
            throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PASSWORD)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
            logger.info("Password updated for userId={}", userId);

        } catch (SQLException e) {
            throw new DatabaseException("updatePassword failed: " + e.getMessage(), e);
        }
    }

    // ── countActiveUsers ─────────────────────────────────────────

    @Override
    public int countActiveUsers() throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_ACTIVE);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("countActiveUsers failed: " + e.getMessage(), e);
        }
    }

    // ── Private helper — maps one ResultSet row to a User object ─

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromString(rs.getString("role_name")));
        user.setGender(Gender.fromString(rs.getString("gender")));

        java.sql.Date dob = rs.getDate("date_of_birth");
        if (dob != null) user.setDateOfBirth(dob.toLocalDate());

        user.setProfileImage(rs.getString("profile_image"));
        user.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) user.setUpdatedAt(updatedAt.toLocalDateTime());

        return user;
    }
}
