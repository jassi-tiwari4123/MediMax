package com.ohms.service;

import com.ohms.dao.*;
import com.ohms.exception.*;
import com.ohms.model.Doctor;
import com.ohms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * DoctorService — business logic for doctor profile and search operations.
 *
 * INTERVIEW POINT:
 *   Demonstrates how the Service layer coordinates between multiple DAOs
 *   without the Controller knowing which tables are involved.
 *   The Controller only calls service methods — stays thin and focused
 *   on HTTP concerns (request/response).
 */
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorDAO     doctorDAO     = new DoctorDAOImpl();
    private final UserDAO       userDAO       = new UserDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    // ── Get Doctor by userId ─────────────────────────────────────

    public Doctor getDoctorByUserId(int userId) throws OhmsException {
        return doctorDAO.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor profile", userId));
    }

    public Doctor getDoctorById(int doctorId) throws OhmsException {
        return doctorDAO.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
    }

    // ── Update Doctor Profile ────────────────────────────────────

    /**
     * Updates both the users table (name, phone) and doctors table
     * (specialization, bio, fees, etc.) in sequence.
     *
     * INTERVIEW POINT — coordinates two DAO calls.
     */
    public void updateProfile(Doctor doctor, User user) throws OhmsException {
        userDAO.update(user);
        doctorDAO.update(doctor);
        logger.info("Doctor profile updated: doctorId={}", doctor.getId());
    }

    // ── Search & Filter ──────────────────────────────────────────

    public List<Doctor> searchByName(String name) throws OhmsException {
        return doctorDAO.searchByName(name);
    }

    public List<Doctor> filterByDepartment(int deptId) throws OhmsException {
        return doctorDAO.findByDepartment(deptId);
    }

    public List<Doctor> getAllApproved() throws OhmsException {
        return doctorDAO.findApproved();
    }

    public List<Doctor> getPendingApprovals() throws OhmsException {
        return doctorDAO.findPending();
    }

    // ── Admin operations ─────────────────────────────────────────

    public void approve(int doctorId) throws OhmsException {
        ensureExists(doctorId);
        doctorDAO.updateStatus(doctorId, "APPROVED");
        logger.info("Doctor {} approved", doctorId);
    }

    public void reject(int doctorId) throws OhmsException {
        ensureExists(doctorId);
        doctorDAO.updateStatus(doctorId, "REJECTED");
        logger.info("Doctor {} rejected", doctorId);
    }

    public void deactivate(int doctorId) throws OhmsException {
        Doctor doc = getDoctorById(doctorId);
        userDAO.deactivate(doc.getUserId());
        logger.info("Doctor {} deactivated", doctorId);
    }

    // ── Stats ────────────────────────────────────────────────────

    public int countApproved() throws OhmsException {
        return doctorDAO.countApproved();
    }

    // ── Private ──────────────────────────────────────────────────

    private void ensureExists(int doctorId) throws OhmsException {
        doctorDAO.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
    }
}
