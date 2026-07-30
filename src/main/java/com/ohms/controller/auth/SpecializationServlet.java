package com.ohms.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohms.dao.SpecializationDAO;
import com.ohms.dao.SpecializationDAOImpl;
import com.ohms.model.Specialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * SpecializationServlet — returns specializations for a given department as JSON.
 *
 * URL: /get-specializations?departmentId=1
 *
 * Called via AJAX from the registration page when user selects a department.
 * Returns: [{"id":1,"name":"Cardiologist"}, ...]
 */
public class SpecializationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SpecializationServlet.class);
    private final SpecializationDAO specDAO = new SpecializationDAOImpl();
    private final ObjectMapper      mapper  = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String deptIdStr = req.getParameter("departmentId");

        try {
            int deptId = Integer.parseInt(deptIdStr);
            List<Specialization> specs = specDAO.findByDepartment(deptId);
            mapper.writeValue(resp.getWriter(), specs);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("[]");
        } catch (Exception e) {
            logger.error("SpecializationServlet error: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("[]");
        }
    }
}
