package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Department;
import com.ohms.utility.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DepartmentDAOImpl — JDBC implementation of DepartmentDAO.
 */
public class DepartmentDAOImpl implements DepartmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentDAOImpl.class);

    @Override
    public int save(Department dept) throws DatabaseException {
        String sql = "INSERT INTO departments (name, description, is_active) VALUES (?,?,1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dept.getName());
            ps.setString(2, dept.getDescription());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) { dept.setId(keys.getInt(1)); return dept.getId(); }
            }
            throw new DatabaseException("Department insert failed — no key.");

        } catch (SQLException e) {
            throw new DatabaseException("save(Department) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Department dept) throws DatabaseException {
        String sql = "UPDATE departments SET name=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dept.getName());
            ps.setString(2, dept.getDescription());
            ps.setInt(3, dept.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("update(Department) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void toggleActive(int id) throws DatabaseException {
        String sql = "UPDATE departments SET is_active = NOT is_active WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("toggleActive(Department) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Department> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM departments WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("findById(Department) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Department> findAll() throws DatabaseException {
        return query("SELECT * FROM departments ORDER BY name");
    }

    @Override
    public List<Department> findActive() throws DatabaseException {
        return query("SELECT * FROM departments WHERE is_active=1 ORDER BY name");
    }

    private List<Department> query(String sql) throws DatabaseException {
        List<Department> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            throw new DatabaseException("Department query failed: " + e.getMessage(), e);
        }
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setDescription(rs.getString("description"));
        d.setActive(rs.getBoolean("is_active"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) d.setCreatedAt(ca.toLocalDateTime());
        return d;
    }
}
