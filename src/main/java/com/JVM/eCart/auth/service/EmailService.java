package com.JVM.eCart.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendActivationEmail(String email, String token) {

        String link = "http://localhost:8080/auth/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Activate Your Account");
        message.setText("Click the link to activate your account:\n" + link);

        mailSender.send(message);

        System.out.println("Email sent to Customer on: " + email);
    }

    @Async
    public void sendSellerRegistrationEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Seller Registration Received");
        message.setText("Thank you for registering as a seller. Your application is under review. We will notify you once it's approved.");

        mailSender.send(message);

        System.out.println("Email sent to Seller on: " + email);
    }

    @Async
    public void sendAccountLockedEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Account Locked");
        message.setText("Your account has been locked due to multiple failed attempts");
        message.setTo(email);

        mailSender.send(message);

        System.out.println("Account locked email sent to: " + email);
    }

    @Async
    public void sendResetPasswordLinkMail(String email, String token) {

        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Reset Password");
        message.setText("Click the link to reset your Password:\n" + resetLink);
        message.setTo(email);

        mailSender.send(message);

        System.out.println("Reset Password email sent to: " + email);
    }

    @Async
    public void sendUpdatedPasswordConfirmationMail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Updated");
        message.setText("Your password has been updated successfully");

        mailSender.send(message);

        System.out.println("Password update confirmation email sent to: " + email);
    }

    @Async
    public void sendMail(String email, String subject, String mailContent) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(mailContent);
        mailSender.send(message);
    }
}
