package com.ohms.controller.admin;

import com.ohms.dao.DepartmentDAO;
import com.ohms.dao.DepartmentDAOImpl;
import com.ohms.dao.SpecializationDAO;
import com.ohms.dao.SpecializationDAOImpl;
import com.ohms.model.Department;
import com.ohms.model.Specialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AdminDepartmentServlet — CRUD for hospital departments + specializations.
 */
public class AdminDepartmentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminDepartmentServlet.class);
    private final DepartmentDAO    deptDAO = new DepartmentDAOImpl();
    private final SpecializationDAO specDAO = new SpecializationDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("departments", deptDAO.findAll());
            req.getRequestDispatcher("/jsp/admin/departments.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("AdminDepartmentServlet GET error: {}", e.getMessage(), e);
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        try {
            switch (action) {
                case "add" -> {
                    Department d = new Department(
                        req.getParameter("name"),
                        req.getParameter("description")
                    );
                    int deptId = deptDAO.save(d);

                    // Save specializations (one per line)
                    String specsRaw = req.getParameter("specializations");
                    if (specsRaw != null && !specsRaw.isBlank()) {
                        String[] lines = specsRaw.split("\\r?\\n");
                        for (String line : lines) {
                            String trimmed = line.trim();
                            if (!trimmed.isEmpty()) {
                                Specialization spec = new Specialization(deptId, trimmed);
                                specDAO.save(spec);
                            }
                        }
                    }
                    req.getSession().setAttribute("flashSuccess",
                        "Department added with specializations.");
                }
                case "edit" -> {
                    int deptId = Integer.parseInt(req.getParameter("deptId"));
                    Department d = deptDAO.findById(deptId).orElseThrow();
                    d.setName(req.getParameter("name"));
                    d.setDescription(req.getParameter("description"));
                    deptDAO.update(d);
                    req.getSession().setAttribute("flashSuccess", "Department updated.");
                }
                case "toggle" -> {
                    int deptId = Integer.parseInt(req.getParameter("deptId"));
                    deptDAO.toggleActive(deptId);
                    req.getSession().setAttribute("flashSuccess", "Department status toggled.");
                }
                default -> req.getSession().setAttribute("flashError", "Unknown action.");
            }
        } catch (Exception e) {
            logger.error("AdminDepartmentServlet POST error: {}", e.getMessage(), e);
            req.getSession().setAttribute("flashError", "Operation failed: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/departments");
    }
}
