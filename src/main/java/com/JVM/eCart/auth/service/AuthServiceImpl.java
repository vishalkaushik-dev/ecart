package com.JVM.eCart.auth.service;

import com.JVM.eCart.auth.dto.CustomerRegisterRequest;
import com.JVM.eCart.auth.dto.LoginResponse;
import com.JVM.eCart.auth.dto.ResetPasswordRequest;
import com.JVM.eCart.auth.dto.SellerRegisterRequest;
import com.JVM.eCart.auth.entity.*;
import com.JVM.eCart.auth.repository.*;
import com.JVM.eCart.customer.entity.Customer;
import com.JVM.eCart.security.jwt.JwtTokenProvider;
import com.JVM.eCart.seller.entity.Seller;
import com.JVM.eCart.seller.repository.SellerRepository;
import com.JVM.eCart.user.entity.Address;
import com.JVM.eCart.user.entity.User;
import com.JVM.eCart.user.repository.AddressRepository;
import com.JVM.eCart.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthServiceImpl implements  IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final TokenRepository tokenRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final AddressRepository addressRepository;

    @Override
    public String registerCustomer(@Valid CustomerRegisterRequest customerRegisterRequest) {
        if (userRepository.existsByEmail(customerRegisterRequest.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!customerRegisterRequest.password().equals(customerRegisterRequest.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        // auto-copy safe fields
        BeanUtils.copyProperties(customerRegisterRequest, user);
        user.setPassword(passwordEncoder.encode(customerRegisterRequest.password()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        user.getRoles().add(role);
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        // Add address in address table
        Address address = new Address();
        BeanUtils.copyProperties(customerRegisterRequest, address);
        address.setUser(user);
        addressRepository.save(address);

        // Create Customer
        Customer customer = new Customer();
        customer.setUser(user);
        customerRepository.save(customer);

        // Generate Token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(3));
        tokenRepository.save(verificationToken);

        emailService.sendActivationEmail(user.getEmail(), token);

        return "User registered successfully, Activation link has been sent to your email.";
    }

    @Override
    public String activateAccount(String token) {

        VerificationToken verificationToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            String newToken = UUID.randomUUID().toString();
            verificationToken.setToken(newToken);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(3));

            tokenRepository.save(verificationToken);

            emailService.sendActivationEmail(verificationToken.getUser().getEmail(),
                    verificationToken.getToken()
            );

            return "Token expired, A new activation link has been sent to your email.";
        }

        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        tokenRepository.deleteByToken(verificationToken.getToken());

        return "Account activated successfully";
    }

    @Override
    public String resendActivationLink(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email is not attached with any account in database"));

        if(user.isActive()) {
            return "Account is already active";
        }

        VerificationToken existingToken = tokenRepository.findByUser(user).orElse(null);
        if(existingToken != null) {
            LocalDateTime lastSentTime = existingToken.getExpiryDate().minusHours(3);

            if (lastSentTime.isAfter(LocalDateTime.now().minusMinutes(5))) {
                throw new RuntimeException("Please wait 5 minutes before requesting again");
            }
        }

        // delete previous token
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(3));

        tokenRepository.save(verificationToken);

        emailService.sendActivationEmail(user.getEmail(), token);
        return "Email Activation Link has been send to your email.";
    }

    @Override
    public String registerSeller(@Valid SellerRegisterRequest sellerRegisterRequest) {
        if(userRepository.existsByEmail(sellerRegisterRequest.email())) {
            throw new RuntimeException("Email already exists");
        }

        if(!sellerRegisterRequest.password().equals(sellerRegisterRequest.confirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if(sellerRepository.existsByGst(sellerRegisterRequest.gst())) {
            throw new RuntimeException("GST number already exists");
        }

        if(sellerRepository.existsByCompanyNameIgnoreCase(sellerRegisterRequest.companyName())) {
            throw new RuntimeException("Company name already exists");
        }

        User user = new User();
        BeanUtils.copyProperties(sellerRegisterRequest, user);
        user.setPassword(passwordEncoder.encode(sellerRegisterRequest.password()));
        Role role = roleRepository.findByAuthority("ROLE_SELLER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        // Add address in address table
        Address address = new Address();
        BeanUtils.copyProperties(sellerRegisterRequest, address);
        address.setUser(user);
        addressRepository.save(address);

        Seller seller = new Seller();
        BeanUtils.copyProperties(sellerRegisterRequest, seller);
        seller.setUser(user);
        sellerRepository.save(seller);

        emailService.sendSellerRegistrationEmail(sellerRegisterRequest.email());

        return "Seller registered successfully. Awaiting admin approval.";
    }

    @Override
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email does not exist"));

        if(!user.isActive()) {
            throw new RuntimeException("Account is not active, Please activate your account first");
        }

        if(user.isLocked()) {
            throw new RuntimeException("Account is locked");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {

            if(!user.isBootstrapAdmin()) {
                user.setInvalidAttemptCount(user.getInvalidAttemptCount() + 1);

                if(user.getInvalidAttemptCount() >= 3) {
                    user.setLocked(true);
                    emailService.sendAccountLockedEmail(user.getEmail());
                }
                userRepository.save(user);
            }

            throw new RuntimeException("Invalid credentials");
        }

        user.setInvalidAttemptCount(0);
        userRepository.save(user);
        System.out.println("Roles ===>"+ user.getRoles());
        String token = jwtTokenProvider.generateToken(user);
        return new LoginResponse(token, "Bearer", "Login Successful");
    }

    @Override
    public String logout(String tokenHeader) {
        // Invalidate token logic can be implemented here (e.g., add to blacklist)
        if(tokenHeader == null || !tokenHeader.startsWith("Bearer")) {
            throw new RuntimeException("Invalid token");
        }

        String token = tokenHeader.substring(7);

        if(!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Token is invalid or expired");
        }

        Date expiry = jwtTokenProvider.getExpirationDate(token);

        BlacklistedToken blacklistedToken = new BlacklistedToken(
                token,
                expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );

        blacklistedTokenRepository.save(blacklistedToken);

        return "Logout successful";
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email does not exist"));

        if(!user.isActive()) {
            throw new RuntimeException("Account is not activated");
        }

        forgotPasswordTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setUser(user);
        forgotPasswordToken.setToken(token);
        forgotPasswordToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        forgotPasswordTokenRepository.save(forgotPasswordToken);

        emailService.sendResetPasswordLinkMail(user.getEmail(), token);

        return "Reset password link sent to email";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {

        if(!request.password().equals(request.confirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        ForgotPasswordToken token = forgotPasswordTokenRepository.findByToken(request.token()).orElseThrow(() -> new RuntimeException("Invalid token"));

        if(token.getExpiryDate().isBefore(LocalDateTime.now())) {
            forgotPasswordTokenRepository.delete(token);
            throw new RuntimeException("Token has been expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        forgotPasswordTokenRepository.delete(token);

        emailService.sendUpdatedPasswordConfirmationMail(user.getEmail());

        return "Password updated successfully";
    }

    public User getLoggedInUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + email));
    }
}
