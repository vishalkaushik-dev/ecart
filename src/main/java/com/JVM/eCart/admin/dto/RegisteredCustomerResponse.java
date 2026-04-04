package com.JVM.eCart.admin.dto;

public record RegisteredCustomerResponse(
        Long id,
        String fullName,
        String email,
        Boolean isActive
) { }
