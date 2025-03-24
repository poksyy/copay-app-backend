package com.copay.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
        helper.setSubject("Password Recovery");

        ClassPathResource imageResource = new ClassPathResource("static/img/copay_banner.png");

        String emailContent = "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "    <title>Password Recovery</title>"
                + "    <style>"
                + "        body {"
                + "            font-family: Arial, sans-serif;"
                + "            background-color: #f4f4f4;"
                + "            margin: 0;"
                + "            padding: 0;"
                + "            display: flex;"
                + "            flex-direction: column;"
                + "            justify-content: center;"
                + "            align-items: center;"
                + "            height: 100vh;"
                + "        }"
                + "        .banner {"
                + "            text-align: center;"
                + "            margin-bottom: 20px;"
                + "        }"
                + "        .banner img {"
                + "            max-width: 25%;"
                + "        }"
                + "        .email-container {"
                + "            background-color: #ffffff;"
                + "            padding: 20px;"
                + "            border-radius: 8px;"
                + "            box-shadow: 0 0 10px rgba(255, 255, 255, 0.1);"
                + "            width: 300px;"
                + "            text-align: center;"
                + "        }"
                + "        .email-container h2 {"
                + "            font-size: 24px;"
                + "            color: #000000;"
                + "        }"
                + "        .email-body p {"
                + "            color: #333333;"
                + "            line-height: 1.6;"
                + "        }"
                + "        .reset-button {"
                + "            display: inline-block;"
                + "            background-color: #000000;"
                + "            color: #ffffff;"
                + "            padding: 10px 20px;"
                + "            text-decoration: none;"
                + "            border-radius: 28px;"
                + "            font-size: 16px;"
                + "            font-weight: bold;"
                + "            text-transform: uppercase;"
                + "            transition: background-color 0.3s ease;"
                + "        }"
                + "        .reset-button:hover {"
                + "            background-color: #2c2c2c;"
                + "        }"
                + "        .email-footer {"
                + "            text-align: center;"
                + "            padding: 10px;"
                + "            color: #666666;"
                + "            font-size: 12px;"
                + "        }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class='banner'>"
                + "        <img src='cid:copayLogo' alt='copay-banner'/>"
                + "    </div>"
                + "    <div class='email-container'>"
                + "        <h2>Reset Password</h2>"
                + "        <div class='email-body'>"
                + "            <p>You have requested to reset your password. Click the button below to proceed:</p>"
                + "            <a href='" + resetLink + "' class='reset-button'>Reset Password</a>"
                + "            <p>If you did not request this, please ignore this email.</p>"
                + "        </div>"
                + "    </div>"
                + "    <div class='email-footer'>"
                + "        <p>&copy; 2024 Copay. All rights reserved.</p>"
                + "    </div>"
                + "</body>"
                + "</html>";

        // True means it is HTML.
        helper.setText(emailContent, true); 
        helper.addInline("copayLogo", imageResource);
        mailSender.send(message);
        
    }
}
