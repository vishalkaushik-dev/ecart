package com.JVM.eCart.admin.controller;

import com.JVM.eCart.admin.dto.RegisteredCustomerResponse;
import com.JVM.eCart.admin.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        Page<RegisteredCustomerResponse> page = adminService.getAllRegistererdCustomers( pageSize, pageOffset, sort, email);

        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("customers", page.getContent());

        return ResponseEntity.ok(response);
    }
}
