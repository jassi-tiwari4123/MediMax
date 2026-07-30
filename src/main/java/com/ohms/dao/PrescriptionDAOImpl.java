package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Prescription;
import com.ohms.model.PrescriptionItem;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PrescriptionDAOImpl — JDBC implementation.
 *
 * INTERVIEW POINT — Transaction management:
 *   saveFull() sets autoCommit=false, inserts prescription then items,
 *   commits on success, rolls back on any failure.
 *   This is a manual transaction; production would use connection pool
 *   with transaction management (e.g., via Spring @Transactional).
 */
public class PrescriptionDAOImpl implements PrescriptionDAO {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionDAOImpl.class);

    private static final String INSERT_PRESC =
        "INSERT INTO prescriptions (appointment_id, doctor_id, patient_id, " +
        "diagnosis, instructions, follow_up_date) VALUES (?,?,?,?,?,?)";

    private static final String INSERT_ITEM =
        "INSERT INTO prescription_items (prescription_id, medicine_name, dosage, " +
        "morning, afternoon, night, duration_days, instructions) VALUES (?,?,?,?,?,?,?,?)";

    private static final String UPDATE_PRESC =
        "UPDATE prescriptions SET diagnosis=?, instructions=?, follow_up_date=?, " +
        "updated_at=NOW() WHERE id=?";

    private static final String UPDATE_PDF =
        "UPDATE prescriptions SET pdf_path=?, updated_at=NOW() WHERE id=?";

    // ── saveFull (with transaction) ──────────────────────────────

    @Override
    public int saveFull(Prescription presc) throws DatabaseException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);   // BEGIN TRANSACTION

            // 1) Insert prescription header
            int prescId = insertPrescription(conn, presc);
            presc.setId(prescId);

            // 2) Insert each item
            for (PrescriptionItem item : presc.getItems()) {
                item.setPrescriptionId(prescId);
                insertItem(conn, item);
            }

            conn.commit();   // COMMIT
            logger.info("Prescription {} saved with {} items.", prescId, presc.getItems().size());
            return prescId;

        } catch (SQLException e) {
            DBConnection.rollbackQuietly(conn);
            throw new DatabaseException("saveFull(Prescription) failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnection.closeQuietly(conn);
        }
    }

    private int insertPrescription(Connection conn, Prescription p) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_PRESC,
                                         Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    p.getAppointmentId());
            ps.setInt(2,    p.getDoctorId());
            ps.setInt(3,    p.getPatientId());
            ps.setString(4, p.getDiagnosis());
            ps.setString(5, p.getInstructions());
            ps.setObject(6, p.getFollowUpDate() != null
                            ? Date.valueOf(p.getFollowUpDate()) : null);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            throw new SQLException("Prescription insert produced no key.");
        }
    }

    private void insertItem(Connection conn, PrescriptionItem item) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_ITEM)) {
            ps.setInt(1,     item.getPrescriptionId());
            ps.setString(2,  item.getMedicineName());
            ps.setString(3,  item.getDosage());
            ps.setBoolean(4, item.isMorning());
            ps.setBoolean(5, item.isAfternoon());
            ps.setBoolean(6, item.isNight());
            ps.setObject(7,  item.getDurationDays());
            ps.setString(8,  item.getInstructions());
            ps.executeUpdate();
        }
    }

    // ── update ───────────────────────────────────────────────────

    @Override
    public void update(Prescription p) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PRESC)) {

            ps.setString(1, p.getDiagnosis());
            ps.setString(2, p.getInstructions());
            ps.setObject(3, p.getFollowUpDate() != null
                            ? Date.valueOf(p.getFollowUpDate()) : null);
            ps.setInt(4, p.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("update(Prescription) failed: " + e.getMessage(), e);
        }
    }

    // ── updatePdfPath ────────────────────────────────────────────

    @Override
    public void updatePdfPath(int prescriptionId, String pdfPath) throws DatabaseException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PDF)) {

            ps.setString(1, pdfPath);
            ps.setInt(2, prescriptionId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("updatePdfPath failed: " + e.getMessage(), e);
        }
    }

    // ── findById ─────────────────────────────────────────────────

    @Override
    public Optional<Prescription> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM prescriptions WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prescription p = mapRow(rs);
                    p.setItems(findItemsByPrescription(p.getId()));
                    return Optional.of(p);
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findById(Prescription) failed: " + e.getMessage(), e);
        }
    }

    // ── findByAppointmentId ──────────────────────────────────────

    @Override
    public Optional<Prescription> findByAppointmentId(int apptId) throws DatabaseException {
        String sql = "SELECT * FROM prescriptions WHERE appointment_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, apptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prescription p = mapRow(rs);
                    p.setItems(findItemsByPrescription(p.getId()));
                    return Optional.of(p);
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findByAppointmentId failed: " + e.getMessage(), e);
        }
    }

    // ── findByPatient ────────────────────────────────────────────

    @Override
    public List<Prescription> findByPatient(int patientId) throws DatabaseException {
        return findByField("patient_id", patientId);
    }

    // ── findByDoctor ─────────────────────────────────────────────

    @Override
    public List<Prescription> findByDoctor(int doctorId) throws DatabaseException {
        return findByField("doctor_id", doctorId);
    }

    // ── findItemsByPrescription ──────────────────────────────────

    @Override
    public List<PrescriptionItem> findItemsByPrescription(int prescId)
            throws DatabaseException {
        String sql = "SELECT * FROM prescription_items WHERE prescription_id=?";
        List<PrescriptionItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, prescId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapItemRow(rs));
            }
            return items;

        } catch (SQLException e) {
            throw new DatabaseException("findItemsByPrescription failed: " + e.getMessage(), e);
        }
    }

    // ── Private helpers ──────────────────────────────────────────

    private List<Prescription> findByField(String field, int value) throws DatabaseException {
        String sql = "SELECT * FROM prescriptions WHERE " + field + "=? ORDER BY created_at DESC";
        List<Prescription> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prescription p = mapRow(rs);
                    p.setItems(findItemsByPrescription(p.getId()));
                    list.add(p);
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DatabaseException("findByField failed: " + e.getMessage(), e);
        }
    }

    private Prescription mapRow(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setId(rs.getInt("id"));
        p.setAppointmentId(rs.getInt("appointment_id"));
        p.setDoctorId(rs.getInt("doctor_id"));
        p.setPatientId(rs.getInt("patient_id"));
        p.setDiagnosis(rs.getString("diagnosis"));
        p.setInstructions(rs.getString("instructions"));

        Date fud = rs.getDate("follow_up_date");
        if (fud != null) p.setFollowUpDate(fud.toLocalDate());

        p.setPdfPath(rs.getString("pdf_path"));

        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) p.setCreatedAt(ca.toLocalDateTime());

        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) p.setUpdatedAt(ua.toLocalDateTime());

        return p;
    }

    private PrescriptionItem mapItemRow(ResultSet rs) throws SQLException {
        PrescriptionItem item = new PrescriptionItem();
        item.setId(rs.getInt("id"));
        item.setPrescriptionId(rs.getInt("prescription_id"));
        item.setMedicineName(rs.getString("medicine_name"));
        item.setDosage(rs.getString("dosage"));
        item.setMorning(rs.getBoolean("morning"));
        item.setAfternoon(rs.getBoolean("afternoon"));
        item.setNight(rs.getBoolean("night"));

        int dur = rs.getInt("duration_days");
        item.setDurationDays(rs.wasNull() ? null : dur);

        item.setInstructions(rs.getString("instructions"));
        return item;
    }
}
