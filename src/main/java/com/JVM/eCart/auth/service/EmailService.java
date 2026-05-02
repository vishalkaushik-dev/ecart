package com.JVM.eCart.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import static com.JVM.eCart.constants.EmailConstants.*;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendActivationEmail(String email, String token) {

        String link = BASE_URL + ACTIVATE_PATH + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(SUBJECT_ACTIVATE_ACCOUNT);
        message.setText(MSG_ACTIVATE_ACCOUNT + link);

        mailSender.send(message);

        log.info("Email sent to Customer on: {}", email);
    }

    @Async
    public void sendSellerRegistrationEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(SUBJECT_SELLER_REGISTRATION);
        message.setText(MSG_SELLER_REGISTRATION);

        mailSender.send(message);

        log.info("Email sent to Seller on: {}", email);
    }

    @Async
    public void sendAccountLockedEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(SUBJECT_ACCOUNT_LOCKED);
        message.setText(MSG_ACCOUNT_LOCKED);
        message.setTo(email);

        mailSender.send(message);

        log.info("Account locked email sent to: {}", email);
    }

    @Async
    public void sendResetPasswordLinkMail(String email, String token) {

        String resetLink = BASE_URL + RESET_PASSWORD_PATH + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(SUBJECT_RESET_PASSWORD);
        message.setText(MSG_RESET_PASSWORD + resetLink);
        message.setTo(email);

        mailSender.send(message);

        log.info("Reset Password email sent to: {}", email);
    }

    @Async
    public void sendUpdatedPasswordConfirmationMail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(SUBJECT_PASSWORD_UPDATED);
        message.setText(MSG_PASSWORD_UPDATED);

        mailSender.send(message);

        log.info("Password update confirmation email sent to: {}", email);
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
