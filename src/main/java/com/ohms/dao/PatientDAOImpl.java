package com.ohms.dao;

import com.ohms.enums.Gender;
import com.ohms.enums.Role;
import com.ohms.exception.DatabaseException;
import com.ohms.model.Patient;
import com.ohms.model.User;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PatientDAOImpl — JDBC implementation of PatientDAO.
 */
public class PatientDAOImpl implements PatientDAO {

    private static final Logger logger = LoggerFactory.getLogger(PatientDAOImpl.class);

    private static final String BASE_SELECT =
        "SELECT p.*, u.full_name, u.email, u.phone, u.gender, " +
        "u.profile_image, u.is_active " +
        "FROM patients p " +
        "JOIN users u ON p.user_id = u.id ";

    private static final String INSERT =
        "INSERT INTO patients (user_id, blood_group, address, " +
        "emergency_contact_name, emergency_contact_phone, medical_history) " +
        "VALUES (?,?,?,?,?,?)";

    private static final String UPDATE =
        "UPDATE patients SET blood_group=?, address=?, emergency_contact_name=?, " +
        "emergency_contact_phone=?, medical_history=?, updated_at=NOW() WHERE id=?";

    @Override
    public int save(Patient patient) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT,
                                         Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    patient.getUserId());
            ps.setString(2, patient.getBloodGroup());
            ps.setString(3, patient.getAddress());
            ps.setString(4, patient.getEmergencyContactName());
            ps.setString(5, patient.getEmergencyContactPhone());
            ps.setString(6, patient.getMedicalHistory());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    patient.setId(id);
                    return id;
                }
            }
            throw new DatabaseException("Patient insert failed — no generated key.");

        } catch (SQLException e) {
            throw new DatabaseException("save(Patient) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Patient patient) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, patient.getBloodGroup());
            ps.setString(2, patient.getAddress());
            ps.setString(3, patient.getEmergencyContactName());
            ps.setString(4, patient.getEmergencyContactPhone());
            ps.setString(5, patient.getMedicalHistory());
            ps.setInt(6,    patient.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("update(Patient) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Patient> findById(int id) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE p.id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findById(Patient) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Patient> findByUserId(int userId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE p.user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findByUserId(Patient) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> findAll() throws DatabaseException {
        String sql = BASE_SELECT + "ORDER BY u.full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return executeList(ps);
        } catch (SQLException e) {
            throw new DatabaseException("findAll(Patient) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> searchByName(String name) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE u.full_name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            return executeList(ps);
        } catch (SQLException e) {
            throw new DatabaseException("searchByName(Patient) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("count(Patient) failed: " + e.getMessage(), e);
        }
    }

    // ── Private helpers ──────────────────────────────────────────

    private List<Patient> executeList(PreparedStatement ps) throws SQLException {
        List<Patient> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setAddress(rs.getString("address"));
        p.setEmergencyContactName(rs.getString("emergency_contact_name"));
        p.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        p.setMedicalHistory(rs.getString("medical_history"));

        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) p.setCreatedAt(ca.toLocalDateTime());

        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) p.setUpdatedAt(ua.toLocalDateTime());

        User user = new User();
        user.setId(p.getUserId());
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setGender(Gender.fromString(rs.getString("gender")));
        user.setRole(Role.PATIENT);
        user.setProfileImage(rs.getString("profile_image"));
        user.setActive(rs.getBoolean("is_active"));
        p.setUser(user);

        return p;
    }
}
