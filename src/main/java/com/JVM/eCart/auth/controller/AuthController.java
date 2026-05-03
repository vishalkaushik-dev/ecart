package com.JVM.eCart.auth.controller;

import com.JVM.eCart.auth.dto.*;
import com.JVM.eCart.auth.service.IAuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final IAuthService authServiceImpl;

    @PostMapping("/customer/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegisterRequest customerRegisterRequest) {
        return ResponseEntity.ok(authServiceImpl.registerCustomer(customerRegisterRequest));
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam("token") String token) {
        return ResponseEntity.ok(authServiceImpl.activateAccount(token));
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<?> resendActivationLink(@RequestParam("email") String email) {
        return ResponseEntity.ok(authServiceImpl.resendActivationLink(email));
    }

    @PostMapping("/seller/register")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody SellerRegisterRequest sellerRegisterRequest) {
        return ResponseEntity.ok(authServiceImpl.registerSeller(sellerRegisterRequest));
    }

    @PostMapping({"/customer/login", "/seller/login", "/admin/login"})
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authServiceImpl.login(loginRequest.username(), loginRequest.password()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authServiceImpl.logout(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam
                                                @Email(message = "Invalid email format")
                                                @Pattern(
                                                        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                                                        message = "Email must contain a valid domain (e.g., .com, .in)"
                                                ) String email) {
        return ResponseEntity.ok(authServiceImpl.forgotPassword(email));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest, @RequestParam String token) {
        return ResponseEntity.ok(authServiceImpl.resetPassword(resetPasswordRequest,token));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authServiceImpl.refreshToken(request.refreshToken()));
    }
}
