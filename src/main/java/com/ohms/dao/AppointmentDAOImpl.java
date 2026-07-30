package com.ohms.dao;

import com.ohms.enums.AppointmentStatus;
import com.ohms.exception.DatabaseException;
import com.ohms.model.Appointment;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AppointmentDAOImpl — JDBC implementation of AppointmentDAO.
 *
 * INTERVIEW POINT:
 *   isSlotTaken() runs a SELECT before INSERT to enforce the
 *   "no double-booking" rule at the application layer.
 *   The UNIQUE constraint in the DB is a safety net on top of this.
 */
public class AppointmentDAOImpl implements AppointmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentDAOImpl.class);

    private static final String BASE_SELECT =
        "SELECT a.*, " +
        "  pu.full_name AS patient_name, pu.email AS patient_email, " +
        "  du.full_name AS doctor_name,  du.email AS doctor_email, " +
        "  dep.name AS dept_name " +
        "FROM appointments a " +
        "JOIN patients pat ON a.patient_id = pat.id " +
        "JOIN users pu ON pat.user_id = pu.id " +
        "JOIN doctors doc ON a.doctor_id = doc.id " +
        "JOIN users du ON doc.user_id = du.id " +
        "JOIN departments dep ON doc.department_id = dep.id ";

    private static final String INSERT =
        "INSERT INTO appointments (patient_id, doctor_id, appointment_date, " +
        "appointment_time, status, reason) VALUES (?,?,?,?,?,?)";

    private static final String UPDATE_STATUS =
        "UPDATE appointments SET status=?, cancelled_by=?, cancel_reason=?, " +
        "updated_at=NOW() WHERE id=?";

    private static final String UPDATE =
        "UPDATE appointments SET appointment_date=?, appointment_time=?, " +
        "status=?, notes=?, diagnosis=?, updated_at=NOW() WHERE id=?";

    private static final String SLOT_CHECK =
        "SELECT COUNT(*) FROM appointments " +
        "WHERE doctor_id=? AND appointment_date=? AND appointment_time=? " +
        "AND status IN ('PENDING','CONFIRMED')";

    // ── save ─────────────────────────────────────────────────────

    @Override
    public int save(Appointment appt) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT,
                                         Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    appt.getPatientId());
            ps.setInt(2,    appt.getDoctorId());
            ps.setDate(3,   Date.valueOf(appt.getAppointmentDate()));
            ps.setTime(4,   Time.valueOf(appt.getAppointmentTime()));
            ps.setString(5, appt.getStatus().name());
            ps.setString(6, appt.getReason());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    appt.setId(id);
                    logger.info("Appointment saved: id={}", id);
                    return id;
                }
            }
            throw new DatabaseException("Appointment insert failed — no generated key.");

        } catch (SQLException e) {
            throw new DatabaseException("save(Appointment) failed: " + e.getMessage(), e);
        }
    }

    // ── update ───────────────────────────────────────────────────

    @Override
    public void update(Appointment appt) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setDate(1,   Date.valueOf(appt.getAppointmentDate()));
            ps.setTime(2,   Time.valueOf(appt.getAppointmentTime()));
            ps.setString(3, appt.getStatus().name());
            ps.setString(4, appt.getNotes());
            ps.setString(5, appt.getDiagnosis());
            ps.setInt(6,    appt.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("update(Appointment) failed: " + e.getMessage(), e);
        }
    }

    // ── updateStatus ─────────────────────────────────────────────

    @Override
    public void updateStatus(int id, String status,
                             String cancelledBy, String reason)
            throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {

            ps.setString(1, status.toUpperCase());
            ps.setString(2, cancelledBy);
            ps.setString(3, reason);
            ps.setInt(4, id);
            ps.executeUpdate();
            logger.info("Appointment {} status → {}", id, status);

        } catch (SQLException e) {
            throw new DatabaseException("updateStatus failed: " + e.getMessage(), e);
        }
    }

    // ── findById ─────────────────────────────────────────────────

    @Override
    public Optional<Appointment> findById(int id) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE a.id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findById(Appointment) failed: " + e.getMessage(), e);
        }
    }

    // ── findByPatient ────────────────────────────────────────────

    @Override
    public List<Appointment> findByPatient(int patientId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE a.patient_id=? ORDER BY a.appointment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            return executeList(ps);

        } catch (SQLException e) {
            throw new DatabaseException("findByPatient failed: " + e.getMessage(), e);
        }
    }

    // ── findByDoctor ─────────────────────────────────────────────

    @Override
    public List<Appointment> findByDoctor(int doctorId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE a.doctor_id=? ORDER BY a.appointment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            return executeList(ps);

        } catch (SQLException e) {
            throw new DatabaseException("findByDoctor failed: " + e.getMessage(), e);
        }
    }

    // ── findByDoctorAndDate ──────────────────────────────────────

    @Override
    public List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date)
            throws DatabaseException {
        String sql = BASE_SELECT +
            "WHERE a.doctor_id=? AND a.appointment_date=? ORDER BY a.appointment_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            return executeList(ps);

        } catch (SQLException e) {
            throw new DatabaseException("findByDoctorAndDate failed: " + e.getMessage(), e);
        }
    }

    // ── findAll ──────────────────────────────────────────────────

    @Override
    public List<Appointment> findAll() throws DatabaseException {
        String sql = BASE_SELECT + "ORDER BY a.appointment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return executeList(ps);
        } catch (SQLException e) {
            throw new DatabaseException("findAll(Appointments) failed: " + e.getMessage(), e);
        }
    }

    // ── findByStatus ─────────────────────────────────────────────

    @Override
    public List<Appointment> findByStatus(String status) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE a.status=? ORDER BY a.appointment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase());
            return executeList(ps);

        } catch (SQLException e) {
            throw new DatabaseException("findByStatus failed: " + e.getMessage(), e);
        }
    }

    // ── isSlotTaken ──────────────────────────────────────────────

    @Override
    public boolean isSlotTaken(int doctorId, LocalDate date, LocalTime time)
            throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SLOT_CHECK)) {

            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(time));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("isSlotTaken failed: " + e.getMessage(), e);
        }
    }

    // ── countByStatus ────────────────────────────────────────────

    @Override
    public int countByStatus(String status) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("countByStatus failed: " + e.getMessage(), e);
        }
    }

    // ── Private helpers ──────────────────────────────────────────

    private List<Appointment> executeList(PreparedStatement ps) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));

        Date d = rs.getDate("appointment_date");
        if (d != null) a.setAppointmentDate(d.toLocalDate());

        Time t = rs.getTime("appointment_time");
        if (t != null) a.setAppointmentTime(t.toLocalTime());

        a.setStatus(AppointmentStatus.fromString(rs.getString("status")));
        a.setReason(rs.getString("reason"));
        a.setNotes(rs.getString("notes"));
        a.setDiagnosis(rs.getString("diagnosis"));
        a.setCancelledBy(rs.getString("cancelled_by"));
        a.setCancelReason(rs.getString("cancel_reason"));

        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) a.setCreatedAt(ca.toLocalDateTime());

        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) a.setUpdatedAt(ua.toLocalDateTime());

        // Lightweight joined data used for display
        // Full Patient/Doctor objects are loaded by Service layer when needed
        return a;
    }
}
