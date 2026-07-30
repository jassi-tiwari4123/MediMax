package com.ohms.dao;

import com.ohms.enums.DoctorStatus;
import com.ohms.exception.DatabaseException;
import com.ohms.model.Department;
import com.ohms.model.Doctor;
import com.ohms.model.User;
import com.ohms.enums.Gender;
import com.ohms.enums.Role;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DoctorDAOImpl — JDBC implementation with JOIN queries to pull
 * doctor + user + department data in a single query.
 */
public class DoctorDAOImpl implements DoctorDAO {

    private static final Logger logger = LoggerFactory.getLogger(DoctorDAOImpl.class);

    // Base SELECT with JOINs — reused by multiple methods
    private static final String BASE_SELECT =
        "SELECT d.*, " +
        "  u.full_name, u.email, u.phone, u.gender, u.date_of_birth, " +
        "  u.profile_image, u.is_active, u.created_at AS u_created, " +
        "  dep.id AS dep_id, dep.name AS dep_name, dep.description AS dep_desc " +
        "FROM doctors d " +
        "JOIN users u ON d.user_id = u.id " +
        "JOIN departments dep ON d.department_id = dep.id ";

    private static final String INSERT =
        "INSERT INTO doctors (user_id, department_id, specialization, " +
        "qualification, experience_yrs, consultation_fee, available_days, " +
        "available_from, available_to, bio, status) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE =
        "UPDATE doctors SET department_id=?, specialization=?, qualification=?, " +
        "experience_yrs=?, consultation_fee=?, available_days=?, " +
        "available_from=?, available_to=?, bio=?, updated_at=NOW() " +
        "WHERE id=?";

    private static final String UPDATE_STATUS =
        "UPDATE doctors SET status=?, updated_at=NOW() WHERE id=?";

    // ── save ─────────────────────────────────────────────────────

    @Override
    public int save(Doctor doctor) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT,
                                         Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    doctor.getUserId());
            ps.setInt(2,    doctor.getDepartmentId());
            ps.setString(3, doctor.getSpecialization());
            ps.setString(4, doctor.getQualification());
            ps.setInt(5,    doctor.getExperienceYears());
            ps.setBigDecimal(6, doctor.getConsultationFee());
            ps.setString(7, doctor.getAvailableDays());
            ps.setObject(8, doctor.getAvailableFrom());
            ps.setObject(9, doctor.getAvailableTo());
            ps.setString(10, doctor.getBio());
            ps.setString(11, doctor.getStatus().name());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    doctor.setId(id);
                    return id;
                }
            }
            throw new DatabaseException("Doctor insert failed — no generated key.");

        } catch (SQLException e) {
            throw new DatabaseException("save(Doctor) failed: " + e.getMessage(), e);
        }
    }

    // ── update ───────────────────────────────────────────────────

    @Override
    public void update(Doctor doctor) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setInt(1,    doctor.getDepartmentId());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getQualification());
            ps.setInt(4,    doctor.getExperienceYears());
            ps.setBigDecimal(5, doctor.getConsultationFee());
            ps.setString(6, doctor.getAvailableDays());
            ps.setObject(7, doctor.getAvailableFrom());
            ps.setObject(8, doctor.getAvailableTo());
            ps.setString(9, doctor.getBio());
            ps.setInt(10,   doctor.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("update(Doctor) failed: " + e.getMessage(), e);
        }
    }

    // ── updateStatus ─────────────────────────────────────────────

    @Override
    public void updateStatus(int doctorId, String status) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {

            ps.setString(1, status.toUpperCase());
            ps.setInt(2, doctorId);
            ps.executeUpdate();
            logger.info("Doctor {} status → {}", doctorId, status);

        } catch (SQLException e) {
            throw new DatabaseException("updateStatus failed: " + e.getMessage(), e);
        }
    }

    // ── findById ─────────────────────────────────────────────────

    @Override
    public Optional<Doctor> findById(int id) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE d.id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findById(Doctor) failed: " + e.getMessage(), e);
        }
    }

    // ── findByUserId ─────────────────────────────────────────────

    @Override
    public Optional<Doctor> findByUserId(int userId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE d.user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findByUserId(Doctor) failed: " + e.getMessage(), e);
        }
    }

    // ── findAll ──────────────────────────────────────────────────

    @Override
    public List<Doctor> findAll() throws DatabaseException {
        String sql = BASE_SELECT + "ORDER BY d.created_at DESC";
        return executeListQuery(sql);
    }

    // ── findApproved ─────────────────────────────────────────────

    @Override
    public List<Doctor> findApproved() throws DatabaseException {
        String sql = BASE_SELECT + "WHERE d.status='APPROVED' ORDER BY u.full_name";
        return executeListQuery(sql);
    }

    // ── findByDepartment ─────────────────────────────────────────

    @Override
    public List<Doctor> findByDepartment(int departmentId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE d.department_id=? AND d.status='APPROVED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            return executeList(ps);

        } catch (SQLException e) {
            throw new DatabaseException("findByDepartment failed: " + e.getMessage(), e);
        }
    }

    // ── searchByName ─────────────────────────────────────────────

    @Override
    public List<Doctor> searchByName(String name) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE u.full_name LIKE ? AND d.status='APPROVED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            return executeList(ps);

        } catch (SQLException e) {
            throw new DatabaseException("searchByName failed: " + e.getMessage(), e);
        }
    }

    // ── findPending ──────────────────────────────────────────────

    @Override
    public List<Doctor> findPending() throws DatabaseException {
        String sql = BASE_SELECT + "WHERE d.status='PENDING' ORDER BY d.created_at";
        return executeListQuery(sql);
    }

    // ── countApproved ────────────────────────────────────────────

    @Override
    public int countApproved() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM doctors WHERE status='APPROVED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("countApproved failed: " + e.getMessage(), e);
        }
    }

    // ── Private helpers ──────────────────────────────────────────

    private List<Doctor> executeListQuery(String sql) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return executeList(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Doctor list query failed: " + e.getMessage(), e);
        }
    }

    private List<Doctor> executeList(PreparedStatement ps) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getInt("id"));
        d.setUserId(rs.getInt("user_id"));
        d.setDepartmentId(rs.getInt("department_id"));
        d.setSpecialization(rs.getString("specialization"));
        d.setQualification(rs.getString("qualification"));
        d.setExperienceYears(rs.getInt("experience_yrs"));

        BigDecimal fee = rs.getBigDecimal("consultation_fee");
        d.setConsultationFee(fee != null ? fee : BigDecimal.ZERO);

        d.setAvailableDays(rs.getString("available_days"));

        Time from = rs.getTime("available_from");
        if (from != null) d.setAvailableFrom(from.toLocalTime());

        Time to = rs.getTime("available_to");
        if (to != null) d.setAvailableTo(to.toLocalTime());

        d.setBio(rs.getString("bio"));
        d.setStatus(DoctorStatus.fromString(rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) d.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) d.setUpdatedAt(updatedAt.toLocalDateTime());

        // Map joined User
        User user = new User();
        user.setId(d.getUserId());
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setGender(Gender.fromString(rs.getString("gender")));
        user.setRole(Role.DOCTOR);
        user.setProfileImage(rs.getString("profile_image"));
        user.setActive(rs.getBoolean("is_active"));
        d.setUser(user);

        // Map joined Department
        Department dept = new Department();
        dept.setId(rs.getInt("dep_id"));
        dept.setName(rs.getString("dep_name"));
        dept.setDescription(rs.getString("dep_desc"));
        d.setDepartment(dept);

        return d;
    }
}
