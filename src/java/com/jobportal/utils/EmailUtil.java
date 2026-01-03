package com.jobportal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Email utility for sending notifications.
 */
public class EmailUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    private static Session session;

    static {
        initializeMailSession();
    }

    private static void initializeMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", ConfigManager.getProperty("email.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", ConfigManager.getProperty("email.smtp.starttls.enable", "true"));
        props.put("mail.smtp.host", ConfigManager.getProperty("email.smtp.host"));
        props.put("mail.smtp.port", ConfigManager.getProperty("email.smtp.port"));

        final String username = ConfigManager.getProperty("email.smtp.username");
        final String password = ConfigManager.getProperty("email.smtp.password");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Sends an email.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body (HTML supported)
     * @return true if email sent successfully
     */
    public static boolean sendEmail(String to, String subject, String body) {
        boolean enabled = ConfigManager.getBooleanProperty("email.enabled", false);
        if (!enabled) {
            logger.info("Email sending is disabled. Would have sent to: {} with subject: {}", to, subject);
            return true; // Return true to not break application flow
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigManager.getProperty("email.from")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email sent successfully to: {}", to);
            return true;

        } catch (MessagingException e) {
            logger.error("Failed to send email to: " + to, e);
            return false;
        }
    }

    /**
     * Sends application status update notification.
     */
    public static boolean sendApplicationStatusEmail(String studentEmail, String studentName, 
                                                     String jobTitle, String status) {
        String subject = "Application Status Update - " + jobTitle;
        String body = buildApplicationStatusEmailBody(studentName, jobTitle, status);
        return sendEmail(studentEmail, subject, body);
    }

    /**
     * Sends new application notification to employer.
     */
    public static boolean sendNewApplicationNotification(String employerEmail, String employerName,
                                                         String jobTitle, String studentName) {
        String subject = "New Application Received - " + jobTitle;
        String body = buildNewApplicationEmailBody(employerName, jobTitle, studentName);
        return sendEmail(employerEmail, subject, body);
    }

    /**
     * Sends welcome email to new user.
     */
    public static boolean sendWelcomeEmail(String userEmail, String userName, String role) {
        String subject = "Welcome to Sri Lankan Job Portal";
        String body = buildWelcomeEmailBody(userName, role);
        return sendEmail(userEmail, subject, body);
    }

    private static String buildApplicationStatusEmailBody(String studentName, String jobTitle, String status) {
        return String.format(
            "<html><body>" +
            "<h2>Application Status Update</h2>" +
            "<p>Dear %s,</p>" +
            "<p>Your application for the position of <strong>%s</strong> has been updated.</p>" +
            "<p>Current Status: <strong>%s</strong></p>" +
            "<p>Please log in to your account for more details.</p>" +
            "<p>Best regards,<br>Sri Lankan Job Portal Team</p>" +
            "</body></html>",
            studentName, jobTitle, status.toUpperCase()
        );
    }

    private static String buildNewApplicationEmailBody(String employerName, String jobTitle, String studentName) {
        return String.format(
            "<html><body>" +
            "<h2>New Application Received</h2>" +
            "<p>Dear %s,</p>" +
            "<p>A new application has been submitted for your job posting: <strong>%s</strong></p>" +
            "<p>Applicant: <strong>%s</strong></p>" +
            "<p>Please log in to your account to review the application.</p>" +
            "<p>Best regards,<br>Sri Lankan Job Portal Team</p>" +
            "</body></html>",
            employerName, jobTitle, studentName
        );
    }

    private static String buildWelcomeEmailBody(String userName, String role) {
        return String.format(
            "<html><body>" +
            "<h2>Welcome to Sri Lankan Job Portal!</h2>" +
            "<p>Dear %s,</p>" +
            "<p>Thank you for registering as a <strong>%s</strong> on our platform.</p>" +
            "<p>You can now access all the features available to %ss.</p>" +
            "<p>If you have any questions, please don't hesitate to contact us.</p>" +
            "<p>Best regards,<br>Sri Lankan Job Portal Team</p>" +
            "</body></html>",
            userName, role, role
        );
    }
}
