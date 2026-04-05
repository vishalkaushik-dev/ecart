package com.JVM.eCart.customer.controller;

import com.JVM.eCart.customer.dto.AddAddressRequest;
import com.JVM.eCart.customer.dto.UpdateCustomerProfileRequest;
import com.JVM.eCart.customer.service.CustomerService;
import com.JVM.eCart.seller.dto.AddressDto;
import com.JVM.eCart.seller.dto.UpdatePasswordRequest;
import com.JVM.eCart.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;

    @GetMapping("/view-profile")
    public ResponseEntity<?> viewProfile() {
        return ResponseEntity.ok(customerService.viewProfile());
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses() {
        return ResponseEntity.ok(customerService.getAddresses());
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateCustomerProfileRequest request) {
        return ResponseEntity.ok(userService.updateCustomerProfile(request));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PatchMapping("/address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDto request) {
        return ResponseEntity.ok(userService.updateAddress(id, request));
    }

    @PostMapping("/address")
    public ResponseEntity<?> addCustomerNewAddress(@Valid @RequestBody AddAddressRequest request) {
        return ResponseEntity.ok(userService.addCustomerNewAddress(request));
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteAddress(id));
    }
}
