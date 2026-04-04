package com.JVM.eCart.admin.controller;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.admin.dto.RegisteredSellerResponse;
import com.JVM.eCart.admin.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/registered-customers")
    public ResponseEntity<?> getRegisteredCustomers(@RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(defaultValue = "0") int pageOffset,
                                                    @RequestParam(defaultValue = "id") String sort,
                                                    @RequestParam(required = false) String email) {

        Page<RegisteredCustomerResponse> page = adminService.getAllRegisteredCustomers(pageSize, pageOffset, sort, email);

        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("customers", page.getContent());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/registered-sellers")
    public ResponseEntity<?> getRegisteredSellers(@RequestParam(defaultValue = "10") int pageSize,
                                                                         @RequestParam(defaultValue = "0") int pageOffset,
                                                                         @RequestParam(defaultValue = "id") String sort,
                                                                         @RequestParam(required = false) String email) {

        Page<RegisteredSellerResponse> page = adminService.getAllRegisteredSellers(pageSize,pageOffset,sort,email);
        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("sellers", page.getContent());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/customers/{userId}/activate")
    public ResponseEntity<?> activateCustomer(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.activateCustomer(userId));
    }

    @PatchMapping("/customers/{userId}/deactivate")
    public ResponseEntity<?> deactivateCustomer(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deactivateCustomer(userId));
    }

    @PatchMapping("/sellers/{userId}/activate")
    public ResponseEntity<?> activateSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.activateSeller(userId));
    }

    @PatchMapping("/sellers/{userId}/deactivate")
    public ResponseEntity<?> deactivateSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deactivateSeller(userId));
    }

}
