package com.JVM.eCart.admin.service;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.admin.dto.RegisteredSellerResponse;
import com.JVM.eCart.user.entity.User;
import com.JVM.eCart.auth.service.EmailService;
import com.JVM.eCart.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public Page<RegisteredCustomerResponse> getAllRegisteredCustomers (int pageSize, int pageOffset, String sort, String email) {

        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        if (email == null) {
            email = "";
        }
        return userRepository.findRegisteredCustomers("ROLE_CUSTOMER",email, pageable);
    }

    public Page<RegisteredSellerResponse> getAllRegisteredSellers(int pageSize, int pageOffset, String sort, String email) {

        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(sort));
        if(email == null) {
            email = "";
        }
        return userRepository.findRegisteredSellers("ROLE_SELLER", email, pageable);
    }

    public String activateCustomer(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean isCustomer = user.getRoles().stream().anyMatch(role -> role.getAuthority().equalsIgnoreCase("ROLE_CUSTOMER"));
        if(!isCustomer) {
            throw new RuntimeException("User with id: " + userId +  " is not a customer");
        }

        if(user.isActive()) {
            return "Customer is already active.";
        }

        user.setActive(true);
        userRepository.save(user);

        String mailContent = "Dear " + user.getFirstName() + ",\n\nYour account has been activated. You can now log in and start shopping on our platform.\n\nBest regards,\nE-Cart Team";
        emailService.sendMail(user.getEmail(), "Account Activated", mailContent);
        return "Customer has been activated.";
    }

    public String deactivateCustomer(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean isCustomer = user.getRoles().stream().anyMatch(role -> role.getAuthority().equalsIgnoreCase("ROLE_CUSTOMER"));
        if(!isCustomer) {
            throw new RuntimeException("User with id: " + userId +  " is not a customer");
        }

        if(!user.isActive()) {
            return "Customer is already inactive.";
        }

        user.setActive(false);
        userRepository.save(user);

        String mailContent = "Dear " + user.getFirstName() + ",\n\nYour account has been deactivated. Please contact support team to activate again.\n\nBest regards,\nE-Cart Team";
        emailService.sendMail(user.getEmail(), "Account Deactivated", mailContent);
        return "Customer has been deactivated.";
    }

    public String activateSeller(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean isSeller = user.getRoles().stream().anyMatch(role -> role.getAuthority().equalsIgnoreCase("ROLE_SELLER"));
        if(!isSeller) {
            throw new RuntimeException("User with id: " + userId +  " is not a seller");
        }

        if(user.isActive()) {
            return "Seller is already active.";
        }

        user.setActive(true);
        userRepository.save(user);

        String mailContent = "Dear " + user.getFirstName() + ",\n\nYour account has been activated. You can now log in and start shopping on our platform.\n\nBest regards,\nE-Cart Team";
        emailService.sendMail(user.getEmail(), "Account Activated", mailContent);
        return "Seller has been activated.";
    }

    public String deactivateSeller(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean isSeller = user.getRoles().stream().anyMatch(role -> role.getAuthority().equalsIgnoreCase("ROLE_SELLER"));
        if(!isSeller) {
            throw new RuntimeException("User with id: " + userId +  " is not a seller");
        }

        if(!user.isActive()) {
            return "Seller is already inactive.";
        }

        user.setActive(false);
        userRepository.save(user);

        String mailContent = "Dear " + user.getFirstName() + ",\n\nYour account has been deactivated. Please contact support team to activate again.\n\nBest regards,\nE-Cart Team";
        emailService.sendMail(user.getEmail(), "Account Deactivated", mailContent);
        return "Seller has been deactivated.";
    }

}
