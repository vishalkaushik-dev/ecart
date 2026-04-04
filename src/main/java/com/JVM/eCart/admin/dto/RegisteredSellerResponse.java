package com.JVM.eCart.admin.dto;

public record RegisteredSellerResponse(
        Long id,
        String fullName,
        String email,
        Boolean isActive,
        String companyName,
        String companyAddress,
        String companyContact
) {}