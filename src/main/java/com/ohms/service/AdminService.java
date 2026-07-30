package com.ohms.service;

import com.ohms.dao.*;
import com.ohms.exception.OhmsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AdminService — aggregates dashboard statistics and report data.
 *
 * INTERVIEW POINT:
 *   Demonstrates the Facade pattern — AdminService calls multiple DAOs
 *   and returns a single consolidated stats map so the servlet/JSP
 *   doesn't need to know about individual tables.
 *
 *   Uses Map<String, Object> as a simple stats container.
 *   In a larger project this would be a DashboardStatsDTO class.
 */
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final UserDAO            userDAO     = new UserDAOImpl();
    private final DoctorDAO          doctorDAO   = new DoctorDAOImpl();
    private final PatientDAO         patientDAO  = new PatientDAOImpl();
    private final AppointmentDAO     apptDAO     = new AppointmentDAOImpl();
    private final DepartmentDAO      deptDAO     = new DepartmentDAOImpl();

    /**
     * Returns a map of all dashboard statistics in one DB round-trip set.
     *
     * Keys:
     *   totalPatients, totalDoctors, pendingDoctors,
     *   pendingAppointments, confirmedAppointments,
     *   completedAppointments, cancelledAppointments,
     *   totalDepartments
     */
    public Map<String, Object> getDashboardStats() throws OhmsException {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalPatients",          patientDAO.count());
        stats.put("totalDoctors",           doctorDAO.countApproved());
        stats.put("pendingDoctors",         doctorDAO.findPending().size());
        stats.put("totalDepartments",       deptDAO.findAll().size());
        stats.put("pendingAppointments",    apptDAO.countByStatus("PENDING"));
        stats.put("confirmedAppointments",  apptDAO.countByStatus("CONFIRMED"));
        stats.put("completedAppointments",  apptDAO.countByStatus("COMPLETED"));
        stats.put("cancelledAppointments",  apptDAO.countByStatus("CANCELLED"));

        logger.debug("Dashboard stats loaded: {}", stats);
        return stats;
    }
}
