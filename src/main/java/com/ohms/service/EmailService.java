package com.ohms.service;

import com.ohms.utility.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * EmailService — sends transactional emails via JavaMail (SMTP/Gmail).
 *
 * INTERVIEW POINTS:
 *   - Uses JavaMail API configured from application.properties.
 *   - All email methods are isolated here so swapping providers is easy.
 *   - HTML emails for professional look.
 *   - Called from Services, not Servlets (business logic principle).
 *
 * Phase 11 (Email Integration) — full implementation.
 * For now: method stubs are complete; SMTP is wired up fully.
 */
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final Session mailSession;
    private final String  fromAddress;
    private final String  fromName;
    private final String  hospitalName;

    public EmailService() {
        this.fromAddress  = AppConfig.getMailFrom();
        this.fromName     = AppConfig.getMailName();
        this.hospitalName = AppConfig.getHospitalName();

        Properties props = new Properties();
        props.put("mail.smtp.host",            AppConfig.getMailHost());
        props.put("mail.smtp.port",            String.valueOf(AppConfig.getMailPort()));
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");

        String password = AppConfig.getMailPass();

        this.mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, password);
            }
        });
    }

    // ── Registration Success ─────────────────────────────────────

    public void sendRegistrationSuccess(String toEmail, String toName)
            throws MessagingException {

        String subject = "Welcome to " + hospitalName + " — Registration Successful!";
        String body    = buildHtml(toName,
            "<p>Your account has been successfully created on <strong>" + hospitalName + "</strong>.</p>" +
            "<p>You can now log in and start booking appointments.</p>" +
            "<p>If you have any questions, reply to this email.</p>"
        );
        send(toEmail, toName, subject, body);
        logger.info("Registration email sent to {}", toEmail);
    }

    // ── OTP Email ────────────────────────────────────────────────

    public void sendOtp(String toEmail, String toName, String otp)
            throws MessagingException {

        String subject = hospitalName + " — Password Reset OTP";
        String body    = buildHtml(toName,
            "<p>You requested a password reset. Use the OTP below:</p>" +
            "<h1 style='text-align:center;letter-spacing:8px;color:#0d6efd;'>" + otp + "</h1>" +
            "<p style='text-align:center;color:#888;'>This OTP is valid for <strong>10 minutes</strong>.</p>" +
            "<p>If you did not request this, please ignore this email.</p>"
        );
        send(toEmail, toName, subject, body);
        logger.info("OTP email sent to {}", toEmail);
    }

    // ── Appointment Confirmation ─────────────────────────────────

    public void sendAppointmentConfirmation(String toEmail, String patientName,
                                            String doctorName, String date,
                                            String time) throws MessagingException {
        String subject = hospitalName + " — Appointment Confirmed";
        String body    = buildHtml(patientName,
            "<p>Your appointment has been <strong style='color:green;'>confirmed</strong>.</p>" +
            "<table style='border-collapse:collapse;width:100%;'>" +
            "  <tr><td style='padding:8px;border:1px solid #ddd;'>Doctor</td>" +
            "      <td style='padding:8px;border:1px solid #ddd;'><strong>Dr. " + doctorName + "</strong></td></tr>" +
            "  <tr><td style='padding:8px;border:1px solid #ddd;'>Date</td>" +
            "      <td style='padding:8px;border:1px solid #ddd;'><strong>" + date + "</strong></td></tr>" +
            "  <tr><td style='padding:8px;border:1px solid #ddd;'>Time</td>" +
            "      <td style='padding:8px;border:1px solid #ddd;'><strong>" + time + "</strong></td></tr>" +
            "</table>" +
            "<p>Please arrive 10 minutes early.</p>"
        );
        send(toEmail, patientName, subject, body);
    }

    // ── Appointment Cancellation ─────────────────────────────────

    public void sendAppointmentCancellation(String toEmail, String patientName,
                                            String doctorName, String date,
                                            String reason) throws MessagingException {
        String subject = hospitalName + " — Appointment Cancelled";
        String body    = buildHtml(patientName,
            "<p>Your appointment with <strong>Dr. " + doctorName + "</strong> " +
            "on <strong>" + date + "</strong> has been <strong style='color:red;'>cancelled</strong>.</p>" +
            (reason != null && !reason.isBlank()
                ? "<p><strong>Reason:</strong> " + reason + "</p>" : "") +
            "<p>You can book a new appointment from your dashboard.</p>"
        );
        send(toEmail, patientName, subject, body);
    }

    // ── Prescription Ready ───────────────────────────────────────

    public void sendPrescriptionReady(String toEmail, String patientName,
                                      String doctorName) throws MessagingException {
        String subject = hospitalName + " — Prescription Ready";
        String body    = buildHtml(patientName,
            "<p>Your prescription from <strong>Dr. " + doctorName + "</strong> is ready.</p>" +
            "<p>Log in to your account and go to <strong>Prescriptions</strong> to download your PDF.</p>"
        );
        send(toEmail, patientName, subject, body);
    }

    // ── Core send method ─────────────────────────────────────────

    private void send(String toEmail, String toName, String subject, String htmlBody)
            throws MessagingException {

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(fromAddress, fromName));
        message.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(toEmail, toName));
        message.setSubject(subject);
        message.setContent(htmlBody, "text/html; charset=utf-8");
        Transport.send(message);
    }

    // ── HTML email template ──────────────────────────────────────

    private String buildHtml(String recipientName, String contentHtml) {
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>" +
               "<div style='background:#0d6efd;padding:20px;text-align:center;'>" +
               "  <h2 style='color:#fff;margin:0;'>" + hospitalName + "</h2>" +
               "</div>" +
               "<div style='padding:20px;border:1px solid #eee;'>" +
               "  <p>Dear <strong>" + recipientName + "</strong>,</p>" +
               contentHtml +
               "  <br><hr style='border:1px solid #eee;'>" +
               "  <p style='color:#888;font-size:12px;'>This is an automated email. Please do not reply.</p>" +
               "</div>" +
               "</body></html>";
    }
}
