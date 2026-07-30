package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Specialization;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SpecializationDAOImpl — JDBC implementation.
 */
public class SpecializationDAOImpl implements SpecializationDAO {

    private static final Logger logger = LoggerFactory.getLogger(SpecializationDAOImpl.class);

    @Override
    public List<Specialization> findByDepartment(int departmentId) throws DatabaseException {
        String sql = "SELECT * FROM specializations WHERE department_id = ? ORDER BY name";
        List<Specialization> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Specialization s = new Specialization();
                    s.setId(rs.getInt("id"));
                    s.setDepartmentId(rs.getInt("department_id"));
                    s.setName(rs.getString("name"));
                    list.add(s);
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DatabaseException("findByDepartment(Specialization) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int save(Specialization spec) throws DatabaseException {
        String sql = "INSERT INTO specializations (department_id, name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, spec.getDepartmentId());
            ps.setString(2, spec.getName());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    spec.setId(keys.getInt(1));
                    return spec.getId();
                }
            }
            throw new DatabaseException("Specialization insert failed — no key.");

        } catch (SQLException e) {
            throw new DatabaseException("save(Specialization) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM specializations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("delete(Specialization) failed: " + e.getMessage(), e);
        }
    }
}
