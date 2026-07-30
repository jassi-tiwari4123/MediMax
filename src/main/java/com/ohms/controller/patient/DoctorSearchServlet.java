package com.ohms.controller.patient;

import com.ohms.dao.DepartmentDAO;
import com.ohms.dao.DepartmentDAOImpl;
import com.ohms.dao.DoctorDAO;
import com.ohms.dao.DoctorDAOImpl;
import com.ohms.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * DoctorSearchServlet — patient searches and filters doctors.
 *
 * URL: /patient/search-doctors
 * GET  ?name=X&departmentId=Y → filtered list of approved doctors
 *
 * Demonstrates: search + filter by department (Collections filtering).
 */
public class DoctorSearchServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DoctorSearchServlet.class);

    private final DoctorDAO     doctorDAO = new DoctorDAOImpl();
    private final DepartmentDAO deptDAO   = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name       = req.getParameter("name");
        String deptIdStr  = req.getParameter("departmentId");

        try {
            List<Doctor> doctors;

            if (deptIdStr != null && !deptIdStr.isBlank() && !deptIdStr.equals("0")) {
                int deptId = Integer.parseInt(deptIdStr);
                doctors = doctorDAO.findByDepartment(deptId);
                // Further filter by name if provided
                if (name != null && !name.isBlank()) {
                    final String nameLower = name.toLowerCase();
                    doctors = doctors.stream()
                        .filter(d -> d.getFullName().toLowerCase().contains(nameLower))
                        .toList();
                }
            } else if (name != null && !name.isBlank()) {
                doctors = doctorDAO.searchByName(name);
            } else {
                doctors = doctorDAO.findApproved();
            }

            req.setAttribute("doctors",     doctors);
            req.setAttribute("departments", deptDAO.findActive());
            req.setAttribute("searchName",  name);
            req.setAttribute("searchDept",  deptIdStr);

            req.getRequestDispatcher("/jsp/patient/search-doctors.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("DoctorSearchServlet error: {}", e.getMessage(), e);
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }
}
