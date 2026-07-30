package com.ohms.controller.doctor;

import com.ohms.dao.DepartmentDAO;
import com.ohms.dao.DepartmentDAOImpl;
import com.ohms.dao.DoctorDAO;
import com.ohms.dao.DoctorDAOImpl;
import com.ohms.dao.UserDAO;
import com.ohms.dao.UserDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Doctor;
import com.ohms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;

/**
 * DoctorProfileServlet — view and update doctor profile with image upload.
 * URL: /doctor/profile
 */
@MultipartConfig(maxFileSize = 2 * 1024 * 1024) // 2MB max
public class DoctorProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DoctorProfileServlet.class);

    private final DoctorDAO     doctorDAO = new DoctorDAOImpl();
    private final UserDAO       userDAO   = new UserDAOImpl();
    private final DepartmentDAO deptDAO   = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        try {
            Doctor doctor = doctorDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Doctor profile not found."));
            req.setAttribute("doctor",      doctor);
            req.setAttribute("departments", deptDAO.findActive());
            req.getRequestDispatcher("/jsp/doctor/profile.jsp").forward(req, resp);
        } catch (OhmsException e) {
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");

        try {
            Doctor doctor = doctorDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Doctor profile not found."));
            User user = doctor.getUser();

            // Update user fields
            user.setFullName(req.getParameter("fullName"));
            user.setPhone(req.getParameter("phone"));

            // Handle profile image upload
            Part filePart = req.getPart("profileImage");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getRealPath("/images/profiles");
                Files.createDirectories(Paths.get(uploadDir));
                String fileName = "doctor_" + userId + "_" + System.currentTimeMillis()
                                + getExtension(filePart.getSubmittedFileName());
                filePart.write(uploadDir + File.separator + fileName);
                user.setProfileImage("images/profiles/" + fileName);
            }

            userDAO.update(user);

            // Update doctor fields
            doctor.setBio(req.getParameter("bio"));
            doctor.setQualification(req.getParameter("qualification"));
            doctor.setAvailableDays(req.getParameter("availableDays"));
            String feeStr = req.getParameter("consultationFee");
            if (feeStr != null && !feeStr.isBlank()) {
                doctor.setConsultationFee(new BigDecimal(feeStr));
            }
            doctorDAO.update(doctor);

            req.getSession().setAttribute("flashSuccess", "Profile updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/doctor/profile");

        } catch (OhmsException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/doctor/profile");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return ".jpg";
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
