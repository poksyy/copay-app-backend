package com.copay.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setFrom("no-reply@copay.com");
        helper.setSubject("Password recovery");

        String emailContent = "<p>You have requested to reset your password. Click the link below:</p>"
                + "<p><a href='" + resetLink + "'>Reset password</a></p>"
                + "<p>If you did not request this, please ignore this email.</p>";

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}
