package com.ohms.controller.patient;

import com.ohms.dao.PatientDAO;
import com.ohms.dao.PatientDAOImpl;
import com.ohms.dao.UserDAO;
import com.ohms.dao.UserDAOImpl;
import com.ohms.exception.OhmsException;
import com.ohms.model.Patient;
import com.ohms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

/**
 * PatientProfileServlet — view and update patient profile with image upload.
 * URL: /patient/profile
 */
@MultipartConfig(maxFileSize = 2 * 1024 * 1024) // 2MB max
public class PatientProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PatientProfileServlet.class);

    private final PatientDAO patientDAO = new PatientDAOImpl();
    private final UserDAO    userDAO    = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        try {
            Patient patient = patientDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Patient profile not found."));
            req.setAttribute("patient", patient);
            req.getRequestDispatcher("/jsp/patient/profile.jsp").forward(req, resp);
        } catch (OhmsException e) {
            req.getRequestDispatcher("/jsp/error/500.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = (int) req.getAttribute("userId");

        try {
            Patient patient = patientDAO.findByUserId(userId)
                .orElseThrow(() -> new OhmsException("Patient profile not found."));
            User user = patient.getUser();

            // Update user fields
            user.setFullName(req.getParameter("fullName"));
            user.setPhone(req.getParameter("phone"));
            String dob = req.getParameter("dateOfBirth");
            if (dob != null && !dob.isBlank()) {
                user.setDateOfBirth(LocalDate.parse(dob));
            }

            // Handle profile image upload
            Part filePart = req.getPart("profileImage");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getRealPath("/images/profiles");
                Files.createDirectories(Paths.get(uploadDir));
                String fileName = "patient_" + userId + "_" + System.currentTimeMillis()
                                + getExtension(filePart.getSubmittedFileName());
                filePart.write(uploadDir + File.separator + fileName);
                user.setProfileImage("images/profiles/" + fileName);
            }

            userDAO.update(user);

            // Update patient fields
            patient.setBloodGroup(req.getParameter("bloodGroup"));
            patient.setAddress(req.getParameter("address"));
            patient.setEmergencyContactName(req.getParameter("emergencyContactName"));
            patient.setEmergencyContactPhone(req.getParameter("emergencyContactPhone"));
            patient.setMedicalHistory(req.getParameter("medicalHistory"));
            patientDAO.update(patient);

            req.getSession().setAttribute("flashSuccess", "Profile updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/patient/profile");

        } catch (OhmsException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/patient/profile");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return ".jpg";
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
