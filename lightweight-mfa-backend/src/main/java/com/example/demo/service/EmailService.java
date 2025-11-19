package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom("owolabitolani8@gmail.com");
            helper.setSubject(subject);
            helper.setText(text, true);

            emailSender.send(message);
        } catch (Exception e) {
            // Log the error instead of crashing
            System.err.println("Failed to send email: " + e.getMessage());
        }


    }
}
