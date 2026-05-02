package com.JVM.eCart.auth.service;

import com.JVM.eCart.user.entity.User;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LoginAttemptService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean handleInvalidAttempt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);

        if (user.getInvalidAttemptCount() >= 3) {
            user.setLocked(true);
            String mailContent = "Dear " + user.getFirstName() + ",\n\nYour account has been locked. Please connect admin team to unlock your account.\n\nBest regards,\nE-Cart Team";
            emailService.sendMail(user.getEmail(), "Account Locked", mailContent);
        }

        userRepository.save(user);

        if (user.getInvalidAttemptCount() >= 3)
            return true;
        else
            return false;
    }
}
